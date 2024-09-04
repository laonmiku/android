package com.example.ex05;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BlogFragment extends Fragment {
    JSONArray array = new JSONArray();
    BlogAdater adapter = new BlogAdater();
    String query = "대형카페";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view=inflater.inflate(R.layout.fragment_blog, container, false);
        new BlogThread().execute();
        ListView list=view.findViewById(R.id.list);
        list.setAdapter(adapter);

        EditText edit =view.findViewById(R.id.query);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                query = charSequence.toString();
                new BlogThread().execute();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    class BlogThread extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            if(query.equals("")) query = "오늘날씨";
            String url = "https://dapi.kakao.com/v2/search/blog?query="+query;
            String result = KakaoAPI.connect(url);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                array = new JSONObject(s).getJSONArray("documents");
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            super.onPostExecute(s);
        }
    }

    class BlogAdater extends BaseAdapter{

        @Override
        public int getCount() {
            return array.length();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View item = getLayoutInflater().inflate(R.layout.item_blog,viewGroup,false);
            try {
                JSONObject obj= array.getJSONObject(i);
                TextView title = item.findViewById(R.id.title);
                title.setText(Html.fromHtml(obj.getString("title")));
                TextView contents = item.findViewById(R.id.contents);
                contents.setText(Html.fromHtml(obj.getString("contents")));
                //hrml 출력하기하면 <b></b>이런 채그 안나옴!
                String url = obj.getString("url");

                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), WEbActivity.class);
                        intent.putExtra("url",url);
                        startActivity(intent);
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return item;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);
        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                query = s;
                new BlogThread().execute();
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
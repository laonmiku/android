package com.example.ex05;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookFragment extends Fragment {
    JSONArray array=new JSONArray();
    BookAdapter adapter = new BookAdapter();
    String query="자바";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_blog, container, false);
        new BookThread().execute();
        ListView list=view.findViewById(R.id.list);
        list.setAdapter(adapter);

        EditText edit = view.findViewById(R.id.query);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                query=edit.getText().toString();
                new BookThread().execute();
            }
        });
        return view;
    }

    class BookThread extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            if(query.equals("")) query="자바";
            String url="https://dapi.kakao.com/v3/search/book?target=title&query=" + query;
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

    class BookAdapter extends BaseAdapter{
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
            View item=getLayoutInflater().inflate(R.layout.item_book, viewGroup, false);
            try {
                JSONObject obj = array.getJSONObject(i);
                String strTitle=obj.getString("title");
                String strImage=obj.getString("thumbnail");
                String strPrice=obj.getString("sale_price");
                String strAuthors=obj.getString("authors");
                String strContents=obj.getString("contents");
                TextView title=item.findViewById(R.id.title);
                title.setText(strTitle);
                TextView price=item.findViewById(R.id.price);
                price.setText(strPrice);
                TextView authors=item.findViewById(R.id.authors);
                authors.setText(strAuthors);
                ImageView image=item.findViewById(R.id.image);
                if(strImage.equals("")) {
                    image.setImageResource(R.drawable.no_image);
                }else {
                    Picasso.with(getActivity()).load(strImage).into(image);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return item;
        }
    }
}//Flagment
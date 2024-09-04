package com.example.ex04;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

public class LocalActivity extends AppCompatActivity {
    JSONArray array = new JSONArray();
    LocalAdapter adapter = new LocalAdapter();
    String query = "프랭크버거";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        getSupportActionBar().setTitle("지역검색");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new LocalThread().execute();
        ListView list=findViewById(R.id.list);
        list.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class LocalThread extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            String url="https://dapi.kakao.com/v2/local/search/keyword.json?query="+query;
            String result=KakaoAPI.connect(url);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                array= new JSONObject(s).getJSONArray("documents");
                adapter.notifyDataSetChanged();
                Log.i("size", array.length() + "");
            }catch (Exception e){
                Log.e("PostExecute:" , e.toString());
            }
            super.onPostExecute(s);
        }
    }

    class LocalAdapter extends BaseAdapter {
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
            View item = getLayoutInflater().inflate(R.layout.item_local, viewGroup, false);
            try {
                JSONObject obj = array.getJSONObject(i);

                String strName = obj.getString("place_name");
               String strAddress = obj.getString("address_name");
                String strPhone = obj.getString("phone");
                String strX = obj.getString("x");
               String strY = obj.getString("y");

                TextView name = item.findViewById(R.id.name);
                name.setText(strName);
                TextView address = item.findViewById(R.id.address);
                address.setText(strAddress);
                TextView phone = item.findViewById(R.id.phone);
                phone.setText(strPhone);

                ImageView local = item.findViewById(R.id.local);
                local.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(LocalActivity.this, MapsActivity.class);
                        intent.putExtra("x",strX);
                        intent.putExtra("y",strY);
                        intent.putExtra("name",strName);
                        startActivity(intent);
                    }
                });
            }catch (Exception e){
                Log.e("getView:",e.toString());
            }
            return item;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        SearchView searchView=(SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                query = s ;
                new LocalActivity.LocalThread().execute();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
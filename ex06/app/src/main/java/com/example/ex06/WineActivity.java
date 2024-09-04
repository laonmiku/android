package com.example.ex06;

import static com.example.ex06.RemoteService.BASE_URL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WineActivity extends AppCompatActivity {
    Retrofit retrofit;
    RemoteService service;
    JSONArray array = new JSONArray();
    WineAdapter adapter = new WineAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wine);
        getSupportActionBar().setTitle("와인목록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView list=findViewById(R.id.list);
        list.setAdapter(adapter);
        StaggeredGridLayoutManager manager=
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //list.setLayoutManager(new LinearLayoutManager(this));
        list.setLayoutManager(manager);
        getList();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getList() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RemoteService.class);
        Call<List<HashMap<String, Object>>> call = service.list();
        call.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                try {
                    array = new JSONArray(response.body());
                    adapter.notifyDataSetChanged();
                    Log.i("size", array.length()+"");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                Log.i("result", response.body().toString());
            }

            @Override
            public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                Log.i("error", "Fail");
            }
        });
    }

    class WineAdapter extends RecyclerView.Adapter<WineAdapter.ViewHolder>{
        @NonNull
        @Override
        public WineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item=getLayoutInflater().inflate(R.layout.item_wine,parent,false);
            return new ViewHolder(item);
        }

        @Override
        public void onBindViewHolder(@NonNull WineAdapter.ViewHolder holder, int position) {
            try {
                JSONObject obj = array.getJSONObject(position);
                holder.name.setText(obj.getString("wine_name"));
                holder.price.setText(obj.getString("wine_price"));
                String strRating=obj.getString("wine_rating");
                holder.rating.setRating(Float.parseFloat(strRating));
                String url=obj.getString("wine_link");
                holder.item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(WineActivity.this, WebActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int getItemCount() {
            return array.length();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView name, price;
            RatingBar rating;
            CardView item;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name=itemView.findViewById(R.id.name);
                price=itemView.findViewById(R.id.price);
                rating=itemView.findViewById(R.id.rating);
                item = itemView.findViewById(R.id.item);
            }
        }
    }
}//Activity
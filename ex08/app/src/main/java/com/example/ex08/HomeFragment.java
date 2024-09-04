package com.example.ex08;

import static com.example.ex08.RemoteService.BASE_URL;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.HashMap;

public class HomeFragment extends Fragment {
    Retrofit retrofit;
    RemoteService remoteService;
    int page=1;
    int total=0;
    JSONArray array=new JSONArray();
    WineAdapter adapter=new WineAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        remoteService=retrofit.create(RemoteService.class);
        getList();

        RecyclerView list=view.findViewById(R.id.list);
        list.setAdapter(adapter);
        StaggeredGridLayoutManager manager=
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        list.setLayoutManager(manager);
        return view;
    }//onCreateView

    public void getList(){
        Call<HashMap<String,Object>> call=remoteService.list(page);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                JSONObject object=new JSONObject(response.body());
                try {
                    total = object.getInt("total");
                    array = object.getJSONArray("list");
                    Log.i("total", total + "");
                    Log.i("length", array.length() + "");
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {

            }
        });
    }//getList()

    class WineAdapter extends RecyclerView.Adapter<WineAdapter.ViewHolder>{
        @NonNull
        @Override
        public WineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item = getLayoutInflater().inflate(R.layout.item_wine,parent,false);
            return new ViewHolder(item);
        }

        @Override
        public void onBindViewHolder(@NonNull WineAdapter.ViewHolder holder, int position) {
            try {
                JSONObject obj=array.getJSONObject(position);
                String image=obj.getString("wine_image");
                int index = obj.getInt("index");
                Picasso.with(getActivity()).load(image).into(holder.image);
                holder.index.setText(String.valueOf(index));
                float rating=Float.parseFloat(obj.getString("rating"));
                holder.rating.setRating(rating);
                String country=obj.getString("wine_country");
                holder.country.setText(country);
                String type=obj.getString("wine_type");
                holder.type.setText(type);
                String name=obj.getString("wine_name");
                holder.name.setText(name);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int getItemCount() {
            return array.length();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView image;
            TextView name, type, country, price, index;
            RatingBar rating;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                image=itemView.findViewById(R.id.image);
                name=itemView.findViewById(R.id.name);
                type=itemView.findViewById(R.id.type);
                country=itemView.findViewById(R.id.country);
                price=itemView.findViewById(R.id.price);
                rating=itemView.findViewById(R.id.rating);
                index=itemView.findViewById(R.id.index);
            }
        }
    }
}//Fragment
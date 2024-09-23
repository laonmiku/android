package com.example.ex08;

import static com.example.ex08.RemoteService.BASE_URL;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    FirebaseDatabase db = FirebaseDatabase.getInstance();

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
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        list.setLayoutManager(manager);

        FloatingActionButton top=view.findViewById(R.id.top);
        top.setVisibility(View.INVISIBLE);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!list.canScrollVertically(1)){
                    top.setVisibility(View.VISIBLE);
                }
            }
        });
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                top.setVisibility(View.INVISIBLE);
                list.scrollToPosition(0);
            }
        });
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

                HashMap<String,Object> vo=new HashMap<>();
                vo.put("index", obj.getString("index"));
                vo.put("image", obj.getString("wine_image"));
                vo.put("name", obj.getString("wine_name"));

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
                if(user == null){
                    holder.cart.setVisibility(View.INVISIBLE);
                }else{
                    holder.cart.setVisibility(View.VISIBLE);
                }
                //장바구니 클릭
                holder.cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference ref=db.getReference("/cart/" + user.getUid() + "/" + index);
                        ref.setValue(vo);
                        Toast.makeText(getActivity(),"등록성공!", Toast.LENGTH_SHORT).show();
                    }
                });
                //카드뷰를 클릭한 경우
                holder.wine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getActivity(), ReadActivity.class);
                        intent.putExtra("index", index);
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
            ImageView image, cart;
            TextView name, type, country, price, index;
            RatingBar rating;
            CardView wine;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                cart = itemView.findViewById(R.id.cart);
                image=itemView.findViewById(R.id.image);
                name=itemView.findViewById(R.id.name);
                type=itemView.findViewById(R.id.type);
                country=itemView.findViewById(R.id.country);
                price=itemView.findViewById(R.id.price);
                rating=itemView.findViewById(R.id.rating);
                index=itemView.findViewById(R.id.index);
                wine=itemView.findViewById(R.id.wine);
            }
        }
    }
}//Fragment
package com.example.ex08;

import static com.example.ex08.RemoteService.BASE_URL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReadActivity extends AppCompatActivity {
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    Retrofit retrofit;
    RemoteService service;
    ArrayList<ReviewVO> array=new ArrayList<>();
    ReviewAdapter adapter = new ReviewAdapter();

    int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        ImageView image=findViewById(R.id.image);
        TextView name=findViewById(R.id.name);
        TextView txtIndex=findViewById(R.id.index);
        RatingBar ratingBar=findViewById(R.id.rating);

        Intent intent=getIntent();
        index = intent.getIntExtra("index", 0);
        getSupportActionBar().setTitle("와인정보: " + index);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RemoteService.class);
        Call<HashMap<String, Object>> call=service.read(index);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                HashMap<String,Object> vo=response.body();
                name.setText(vo.get("wine_name").toString());
                Picasso.with(ReadActivity.this)
                        .load(vo.get("wine_image").toString()).into(image);
                txtIndex.setText(index + "");
                Float rating =Float.parseFloat(vo.get("rating").toString());
                ratingBar.setRating(rating);
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {

            }
        });

        findViewById(R.id.write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ReadActivity.this, ReviewInsertActivity.class);
                intent.putExtra("name", name.getText().toString());
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });
        getList();
        ListView list=findViewById(R.id.list);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getList(){
        db.collection("review")
                .whereEqualTo("index", index)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot doc:task.getResult()){
                            ReviewVO vo=new ReviewVO();
                            vo.setId(doc.getId());
                            vo.setEmail(doc.getData().get("email").toString());
                            vo.setContents(doc.getData().get("contents").toString());
                            vo.setDate(doc.getData().get("date").toString());
                            float rating=Float.parseFloat(doc.getData().get("rating").toString());
                            vo.setRating(rating);
                            array.add(vo);
                        }
                        Log.i("size", array.size() + "");
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    class ReviewAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return array.size();
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
            View item=getLayoutInflater().inflate(R.layout.item_review, viewGroup, false);
            ReviewVO vo=array.get(i);
            TextView email=item.findViewById(R.id.email);
            TextView contents=item.findViewById(R.id.contents);
            TextView date=item.findViewById(R.id.date);
            email.setText(vo.getEmail());
            contents.setText(vo.getContents());
            date.setText(vo.getDate());
            RatingBar ratingBar=item.findViewById(R.id.ratingBar);
            ratingBar.setRating(vo.getRating());
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(ReadActivity.this, ReviewReadActivity.class);
                    intent.putExtra("id", vo.getId());
                    startActivity(intent);
                }
            });
            return item;
        }
    }

    @Override
    protected void onRestart() {
        array.clear();
        getList();
        super.onRestart();
    }
}
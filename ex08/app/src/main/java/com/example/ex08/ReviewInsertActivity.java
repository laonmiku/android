package com.example.ex08;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewInsertActivity extends AppCompatActivity {
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    int index;
    TextView rating;
    RatingBar rating_indicator;
    EditText contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_insert);
        getSupportActionBar().setTitle("리뷰쓰기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rating=findViewById(R.id.rating);
        rating_indicator=findViewById(R.id.ratingBar);
        contents = findViewById(R.id.contents);

        Intent intent = getIntent();
        index=intent.getIntExtra("index", 0);

        rating_indicator.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating_indicator.setRating(v);
                rating.setText(String.valueOf(v));
            }
        });

        findViewById(R.id.insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contents.equals("")) return;
                ReviewVO vo=new ReviewVO();
                vo.setEmail(user.getEmail());
                vo.setIndex(index);
                vo.setContents(contents.getText().toString());
                vo.setRating(rating_indicator.getRating());
                Date date=new Date();
                SimpleDateFormat sdf=new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
                vo.setDate(sdf.format(date));
                db.collection("review").add(vo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ReviewInsertActivity.this, "등록완료!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
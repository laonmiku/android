package com.example.ex08;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReviewReadActivity extends AppCompatActivity {
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    EditText contents;
    TextView email, date, rating;
    RatingBar ratingBar;
    ReviewVO vo=new ReviewVO();
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_read);
        getSupportActionBar().setTitle("리뷰정보");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contents=findViewById(R.id.contents);
        ratingBar=findViewById(R.id.ratingBar);
        rating=findViewById(R.id.rating);
        email=findViewById(R.id.email);
        date = findViewById(R.id.date);

        Intent intent = getIntent();
        id=intent.getStringExtra("id");
        getRead();

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder box=new AlertDialog.Builder(ReviewReadActivity.this);
                box.setTitle("질의");
                box.setMessage("리뷰정보를 수정하실래요?");
                box.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        vo.setContents(contents.getText().toString());
                        db.collection("review").document(id).set(vo)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
                    }
                });
                box.setNegativeButton("아니오", null);
                box.show();
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder box=new AlertDialog.Builder(ReviewReadActivity.this);
                box.setTitle("질의");
                box.setMessage("리뷰정보를 삭제하실래요?");
                box.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("review").document(id).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
                    }
                });
                box.setNegativeButton("아니오", null);
                box.show();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                vo.setRating(v);
                rating.setText(String.valueOf(vo.getRating()));
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

    public void getRead(){
        db.collection("review").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc=task.getResult();
                String strContents=doc.getData().get("contents").toString();
                String strDate = doc.getData().get("date").toString();
                String strEmail = doc.getData().get("email").toString();
                float fltRating = Float.parseFloat(doc.getData().get("rating").toString());
                int index = Integer.parseInt(doc.getData().get("index").toString());
                vo.setId(id);
                vo.setContents(strContents);
                vo.setDate(strDate);
                vo.setEmail(strEmail);
                vo.setRating(fltRating);
                vo.setIndex(index);
                if(!vo.getEmail().equals(user.getEmail())){
                    LinearLayout buttons=findViewById(R.id.buttons);
                    buttons.setVisibility(View.INVISIBLE);
                    ratingBar.setIsIndicator(true);
                    contents.setEnabled(false);
                }
                ratingBar.setRating(vo.getRating());
                contents.setText(vo.getContents());
                rating.setText(String.valueOf(fltRating));
                date.setText("Date: " + vo.getDate());
                email.setText("Email: " + vo.getEmail());
            }
        });
    }
}
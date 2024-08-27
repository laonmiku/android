package com.example.ex02;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InsertActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
       getSupportActionBar().setTitle("상품등록");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       TextView name = findViewById(R.id.name);
       TextView price = findViewById(R.id.price);

       helper = new DBHelper(this);
       db =helper.getWritableDatabase();

       findViewById(R.id.btnInsert).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String strName = name.getText().toString();
               String strPrice = price.getText().toString();
               String sql = "insert into product(name,price) values ( ";
               sql += "'"  + strName+ "',";
               sql += strPrice + ")";
                db.execSQL(sql);
                finish();

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
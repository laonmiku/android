package com.example.ex02;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class MainActivity3 extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;
    Cursor cursor;
    MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getSupportActionBar().setTitle("상품목록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helper = new DBHelper(this);
        db=helper.getReadableDatabase();

        //데이터생성
        cursor=db.rawQuery("select * from product", null);

        //어댑터 생성
        adapter=new MyAdapter(this, cursor);
        ListView list=findViewById(R.id.list);
        list.setAdapter(adapter);

        findViewById(R.id.btnInsert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity3.this, InsertActivity.class);
                startActivity(intent);
            }
        });
    }//onCreate

    class MyAdapter extends CursorAdapter{
        public MyAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view=getLayoutInflater().inflate(R.layout.item2, viewGroup, false);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            int id = cursor.getInt(0);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(MainActivity3.this, UpdateActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });

            TextView name= view.findViewById(R.id.name);
            name.setText(cursor.getString(1));
            int intPrice = cursor.getInt(2);
            DecimalFormat df=new DecimalFormat("#,###원");
            TextView price= view.findViewById(R.id.price);
            price.setText(df.format(intPrice));

            Button btnDelete = view.findViewById(R.id.delete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder box=new AlertDialog.Builder(MainActivity3.this);
                    box.setTitle("질의");
                    box.setMessage(id + "번 상품을 삭제하실래요?");
                    box.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String sql="delete from product where _id=" + id;
                            db.execSQL(sql);

                            sql ="select * from product";
                            Cursor cursor=db.rawQuery(sql, null);
                            adapter.changeCursor(cursor);
                        }
                    });
                    box.setNegativeButton("아니오", null);
                    box.show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String sql="";
        if(item.getItemId() == android.R.id.home){
            finish();
        }else if(item.getItemId() == R.id.item_name){
            sql="select * from product order by name";
        }else if(item.getItemId() == R.id.item_asc){
            sql="select * from product order by price";
        }else if(item.getItemId() == R.id.item_desc){
            sql="select * from product order by price desc";
        }else if(item.getItemId() == R.id.item_search){
            sql="select * from product";
        }
        Cursor cursor=db.rawQuery(sql, null);
        adapter.changeCursor(cursor);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        Cursor cursor = db.rawQuery("select * from product", null);
        adapter.changeCursor(cursor);
        super.onRestart();
    }
@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchView searchView=(SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String sql = "select * from product where name like ";
                sql += "'%" + s + "%'";
                Cursor cursor = db.rawQuery(sql, null);
                adapter.changeCursor(cursor);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }
}
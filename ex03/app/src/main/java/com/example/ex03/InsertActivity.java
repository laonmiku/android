package com.example.ex03;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import de.hdodenhof.circleimageview.CircleImageView;

public class InsertActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;
    EditText name, juso, phone;
    CircleImageView photo;
    String strPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        getSupportActionBar().setTitle("주소등록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name= findViewById(R.id.name);
        juso= findViewById(R.id.juso);
        phone = findViewById(R.id.phone);
        photo = findViewById(R.id.photo);

        helper=new DBHelper(this);
        db = helper.getWritableDatabase();
        findViewById(R.id.btnInsert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strName = name.getText().toString();
                String strJuso = juso.getText().toString();
                String strPhone = phone.getText().toString();
                if(strName.equals("") || strJuso.equals("") || strPhone ==""){
                    Toast.makeText(
                            InsertActivity.this,"모든내용을 입력하세요!",Toast.LENGTH_SHORT).show();
                }else{
                    String sql="insert into address(name,juso,phone, photo) values(";
                    sql += "'" + strName + "',";
                    sql += "'" + strJuso + "',";
                    sql += "'" + strPhone + "',";
                    sql += "'" + strPhoto + "')";
                    db.execSQL(sql);
                    finish();
                }
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityResult.launch(intent);
            }
        });
    }//onCreate


    //앨범에서 이미지를 하나 선택한 경우
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @SuppressLint("Range")
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == RESULT_OK){
                    //선택한 이미지의 Path와 파일명 구하기
                    Cursor cursor=getContentResolver().query(o.getData().getData(), null, null, null, null);
                    cursor.moveToFirst();
                    strPhoto = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                    Log.i("photo Name:", strPhoto);
                    photo.setImageBitmap(BitmapFactory.decodeFile(strPhoto));
                    cursor.close();
                }
            }
        }
    );  //startActivityResult
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
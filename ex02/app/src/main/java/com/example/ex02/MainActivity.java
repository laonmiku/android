package com.example.ex02;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView list;
    ArrayList<String> array=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("첫번째 예제");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = findViewById(R.id.list);

        //1.데이터생성
        array.add("강감찬");
        array.add("이순신");
        array.add("을지문덕");
        //2.Adapter 생성
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_multiple_choice, array);
        //3.Adapter -> ListView
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //추가버튼을 클릭한 경우
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name=findViewById(R.id.name);
                String strName = name.getText().toString();
                if(strName.equals("")){
                    Toast.makeText(
                            MainActivity.this, "이름을 입력하세요!", Toast.LENGTH_SHORT).show();
                }else{
                    array.add(strName);
                    adapter.notifyDataSetChanged();
                    name.setText("");
                }
            }
        });
    }//onCreate

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}//Activity
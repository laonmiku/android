package com.example.ex06;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    EditText contents;
    ImageView send;
    FirebaseDatabase db=FirebaseDatabase.getInstance();
    ArrayList<ChatVO> array=new ArrayList<>();
    ChatAdapter adapter = new ChatAdapter();
    FirebaseUser user;
    RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.i("token", s);
            }
        });


        user=mAuth.getCurrentUser();

        getList();

        getSupportActionBar().setTitle("채팅:" + user.getEmail());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contents=findViewById(R.id.contents);
        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strContents=contents.getText().toString();
                if(strContents.equals("")){
                    Toast.makeText(ChatActivity.this,"내용을 입력하세요.",Toast.LENGTH_SHORT).show();
                }else{
                    //내용을 보내기
                    ChatVO vo = new ChatVO();
                    vo.setContents(strContents);
                    vo.setEmail(user.getEmail());
                    Date now=new Date();
                    SimpleDateFormat sdf=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                    vo.setDate(sdf.format(now));
                    //Log.i("vo", vo.toString());
                    DatabaseReference ref=db.getReference("/chat").push();
                    vo.setKey(ref.getKey());
                    ref.setValue(vo);
                    contents.setText("");
                }
            }
        });

        list=findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));
    }//onCreate

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getList(){
        DatabaseReference ref=db.getReference("chat");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatVO vo=snapshot.getValue(ChatVO.class);
                array.add(vo);
                adapter.notifyDataSetChanged();
                list.scrollToPosition(array.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //getList();
                ChatVO vo=snapshot.getValue(ChatVO.class);
                for(ChatVO chat:array){
                    if(chat.getKey().equals(vo.getKey())){
                        array.remove(chat);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //채팅어댑터 정의
    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
        @NonNull
        @Override
        public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item=getLayoutInflater().inflate(R.layout.item_chat, parent, false);
            return new ViewHolder(item);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
            ChatVO vo = array.get(position);
            LinearLayout.LayoutParams paramsContents=
                    (LinearLayout.LayoutParams)holder.contents.getLayoutParams();
            LinearLayout.LayoutParams paramsDate=
                    (LinearLayout.LayoutParams)holder.date.getLayoutParams();

            if(vo.getEmail().equals(user.getEmail())){
                paramsContents.gravity= Gravity.RIGHT;
                paramsDate.gravity=Gravity.RIGHT;
                holder.email.setVisibility(View.INVISIBLE);
            }else{
                paramsContents.gravity=Gravity.LEFT;
                paramsDate.gravity=Gravity.LEFT;
                holder.email.setVisibility(View.VISIBLE);
            }
            holder.email.setText(vo.getEmail());
            holder.date.setText(vo.getDate());
            holder.contents.setText(vo.getContents());

            holder.contents.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(vo.getEmail().equals(user.getEmail())){
                        AlertDialog.Builder box=new AlertDialog.Builder(ChatActivity.this);
                        box.setTitle("질의");
                        box.setMessage("삭제하실래요?");
                        box.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.getReference("chat/" + vo.getKey()).removeValue();
                            }
                        });
                        box.setNegativeButton("아니오", null);
                        box.show();
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return array.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView email, date, contents;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                email=itemView.findViewById(R.id.email);
                date=itemView.findViewById(R.id.date);
                contents=itemView.findViewById(R.id.contents);
            }
        }
    }
}//Activity
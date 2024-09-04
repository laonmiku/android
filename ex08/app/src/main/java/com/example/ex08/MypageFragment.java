package com.example.ex08;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


public class MypageFragment extends Fragment {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user =  mAuth.getCurrentUser();
    EditText email,name,phone,address;
    CircleImageView photo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        email = view.findViewById(R.id.email);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        photo = view.findViewById(R.id.photo);

        email.setText(user.getEmail());
        readUserInfo();

        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder box = new AlertDialog.Builder(getActivity());//현재엑티비티를가져옴
                box.setTitle("질의");
                box.setMessage("수정된 정보로 저장하실래여?");
                box.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //정보저장
                        UserVO vo = new UserVO();
                        vo.setEmail(email.getText().toString());
                        vo.setName(name.getText().toString());
                        vo.setPhone(phone.getText().toString());
                        vo.setAddress(address.getText().toString());

                        db.collection("user")
                        .document(user.getUid())
                        .set(vo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(),"수정완료!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                box.setNegativeButton("no",null);
                box.show();
            }


        });
        return view;
    }//onCreateView

    //사용자 정보읽기
    public void readUserInfo(){
        db.collection("user")
        .document(user.getUid())
        .get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if (doc.getData() != null) {
                    name.setText(doc.getData().get("name").toString());
                    phone.setText(doc.getData().get("phone").toString());
                    address.setText(doc.getData().get("address").toString());
                }
            }
        });
    }
}//Fagment
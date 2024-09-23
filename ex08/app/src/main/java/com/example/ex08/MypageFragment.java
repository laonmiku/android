package com.example.ex08;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;


public class MypageFragment extends Fragment {
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    EditText email, name,phone,address;
    CircleImageView photo;
    String strFile="";
    String strPhoto="";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ProgressBar progress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_mypage, container, false);

        email = view.findViewById(R.id.email);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        photo = view.findViewById(R.id.photo);
        progress = view.findViewById(R.id.progress);

        email.setText(user.getEmail());
        readUserInfo();

        view.findViewById(R.id.photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityResult.launch(intent);
            }
        });

        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder box=new AlertDialog.Builder(getActivity());
                box.setTitle("질의");
                box.setMessage("수정된 정보를 저장하실래요?");
                box.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //정보저장
                        UserVO vo=new UserVO();
                        vo.setEmail(email.getText().toString());
                        vo.setName(name.getText().toString());
                        vo.setPhone(phone.getText().toString());
                        vo.setAddress(address.getText().toString());

                        if(!strFile.equals("")){
                            progress.setVisibility(View.VISIBLE);
                            String fileName=System.currentTimeMillis() + ".jpg";
                            Uri file = Uri.fromFile(new File(strFile));
                            StorageReference ref=storage.getReference("/photos/" + fileName);
                            ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            vo.setPhoto(uri.toString());
                                            updateUserInfo(vo);
                                        }
                                    });
                                }
                            });
                        }else{
                            vo.setPhoto(strPhoto);
                            updateUserInfo(vo);
                        }
                    }
                });
                box.setNegativeButton("아니오", null);
                box.show();
            }
        });
        return view;
    }//onCreateView

    //앨범에서 이미지 선택후
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == RESULT_OK) {
                        Cursor cursor=getActivity().getContentResolver().query(o.getData().getData(), null, null, null, null);
                        cursor.moveToFirst();
                        int index=cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        strFile = cursor.getString(index);
                        photo.setImageBitmap(BitmapFactory.decodeFile(strFile));
                        cursor.close();
                    }
                }
            }
    );  //startActivityResult

    //사용자 정보수정
    public void updateUserInfo(UserVO vo){
        db.collection("user")
            .document(user.getUid())
            .set(vo)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getActivity(),"수정완료!", Toast.LENGTH_SHORT).show();
                    readUserInfo();
                    progress.setVisibility(View.INVISIBLE);
                }
            });
    }

    //사용자 정보읽기
    public void  readUserInfo(){
        db.collection("user")
        .document(user.getUid())
        .get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc=task.getResult();
                if(doc.getData() != null){
                    name.setText(doc.getData().get("name").toString());
                    phone.setText(doc.getData().get("phone").toString());
                    address.setText(doc.getData().get("address").toString());
                    strPhoto = doc.getData().get("photo").toString();
                    if(!strPhoto.equals("")){
                        Picasso.with(getActivity()).load(strPhoto).into(photo);
                    }
                }
            }
        });
    }
}//Fragment
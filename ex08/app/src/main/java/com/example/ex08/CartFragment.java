package com.example.ex08;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CartFragment extends Fragment {
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    FirebaseDatabase db=FirebaseDatabase.getInstance();
    ArrayList<CartVO> array=new ArrayList<>();
    CartAdapter adapter=new CartAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_cart, container, false);
        if(user!=null) {
            getList();
            ListView list = view.findViewById(R.id.list);
            list.setAdapter(adapter);
        }
        return view;
    }

    public void getList(){
        DatabaseReference ref=db.getReference("cart/" + user.getUid());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CartVO vo=snapshot.getValue(CartVO.class);
                array.add(vo);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                CartVO vo=snapshot.getValue(CartVO.class);
                for(CartVO c:array){
                    if(c.getIndex().equals(vo.getIndex())){
                        array.remove(c);
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
    }//onCreateView

    class CartAdapter extends BaseAdapter{
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
            View item=getLayoutInflater().inflate(R.layout.item_cart,viewGroup,false);
            CartVO vo=array.get(i);
            ImageView image=item.findViewById(R.id.image);
            Picasso.with(getActivity()).load(vo.getImage()).into(image);
            TextView name=item.findViewById(R.id.name);
            float floatIndex = Float.parseFloat(vo.getIndex());
            int index = (int)floatIndex;
            name.setText(index + ":" + vo.getName());
            ImageView delete=item.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference ref=db.getReference("/cart/" + user.getUid() + "/" + index);
                    ref.removeValue();
                    Toast.makeText(getActivity(), "삭제성공", Toast.LENGTH_SHORT).show();
                }
            });

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getActivity(), ReadActivity.class);
                    intent.putExtra("index", index);
                    startActivity(intent);
                }
            });
            return item;
        }
    }
}//Fragment
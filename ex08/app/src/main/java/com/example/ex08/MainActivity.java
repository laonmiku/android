package com.example.ex08;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    ArrayList<Fragment> framents=new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(user==null) {
            getSupportActionBar().setTitle("와인샵");
        }else{
            getSupportActionBar().setTitle("와인샵:" + user.getEmail());
        }
        framents.add(new HomeFragment());
        framents.add(new CartFragment());
        framents.add(new MypageFragment());

        TabLayout tab=findViewById(R.id.tab);
        tab.addTab(tab.newTab().setText("홈"));
        tab.getTabAt(0).setIcon(R.drawable.home);
        tab.addTab(tab.newTab().setText("장바구니"));
        tab.getTabAt(1).setIcon(R.drawable.cart);
        tab.addTab(tab.newTab().setText("내정보"));
        tab.getTabAt(2).setIcon(R.drawable.mypage);

        PagerAdapter adapter=new PagerAdapter(getSupportFragmentManager());
        ViewPager pager=findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(user==null && tab.getPosition()>0){
                    finish();
                    Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    pager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
    }//onCreate

    class PagerAdapter extends FragmentPagerAdapter{
        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return framents.get(position);
        }

        @Override
        public int getCount() {
            return framents.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(user==null){
            menu.findItem(R.id.login).setVisible(true);
            menu.findItem(R.id.logout).setVisible(false);
        }else{
            menu.findItem(R.id.login).setVisible(false);
            menu.findItem(R.id.logout).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.login){
            finish();
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }else if(item.getItemId()==R.id.logout){
            mAuth.signOut();
            Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}//Activity
package com.example.ex08;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    ArrayList<Fragment> fragments=new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("액션가면");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragments.add(new HomeFragment());
        fragments.add(new CartFragment());
        fragments.add(new MypageFragment());

        TabLayout tab = findViewById(R.id.tab);

        tab.addTab(tab.newTab().setText("home"));
        tab.getTabAt(0).setIcon(R.drawable.home);
        tab.addTab(tab.newTab().setText("cart"));
        tab.getTabAt(1).setIcon(R.drawable.cart);
        tab.addTab(tab.newTab().setText("mypage"));
        tab.getTabAt(2).setIcon(R.drawable.mypage);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(user == null && tab.getPosition()>0){
                    finish();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    pager.setCurrentItem(tab.getPosition()); //아래에 아이콘크릭하면 바뀌게함
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

    class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        if(user == null){
            getSupportActionBar().setTitle("액션가면");
        }else{
            getSupportActionBar().setTitle("액션가면"+user.getEmail());
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) { //prepare 메뉴생기기전에 준비,조건
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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }else if(item.getItemId()==R.id.logout){
            mAuth.signOut();
            Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
        }else{

        }
        return super.onOptionsItemSelected(item);
    }
}//activity
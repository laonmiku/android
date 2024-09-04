package com.example.ex05;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    LinearLayout drawerView;
    TabLayout tab;
    ViewPager pager;
    ArrayList<Fragment> fragments = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    getSupportActionBar().setTitle("카카오검색");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
    fragments.add(new BlogFragment());
    fragments.add(new BookFragment());
    fragments.add(new LocalFragment());

    drawerLayout = findViewById(R.id.drawerLayout);
    drawerView = findViewById(R.id.drawerView);

    tab = findViewById(R.id.tab);
    pager = findViewById(R.id.pager);

    tab.addTab(tab.newTab().setText("블로그"));
    tab.getTabAt(0).setIcon(R.drawable.blog);
    tab.addTab(tab.newTab().setText("도서"));
    tab.getTabAt(1).setIcon(R.drawable.book);
    tab.addTab(tab.newTab().setText("지역"));
    tab.getTabAt(2).setIcon(R.drawable.local);//여기까진 그냥 만드는거 여픙로슬라이드하면 볼수잇음

    PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
    pager.setAdapter(adapter);

    tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            pager.setCurrentItem(tab.getPosition()); }
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }
        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    });
    pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
    //여기까지해주면 아래 메뉴바 터치하면 바뀌는 이벤트 걸어준거
    }//onCreate

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            if(drawerLayout.isDrawerOpen(drawerView)){
                drawerLayout.close();
            }else {
                drawerLayout.openDrawer(drawerView);
            }
        }
        return super.onOptionsItemSelected(item);

    }

    class PagerAdapter extends FragmentPagerAdapter{

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
}//activity
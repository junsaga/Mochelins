package com.musthave0145.mochelins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.musthave0145.mochelins.config.Config;
import com.musthave0145.mochelins.meeting.MeetingCreateActivity;
import com.musthave0145.mochelins.meeting.MeetingFragment;
import com.musthave0145.mochelins.review.ReviewFragment;
import com.musthave0145.mochelins.user.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment reviewFragment;
    Fragment meetingFragment;
    Fragment mapFragment;
    Fragment plannerFragment;
//    Toolbar toolbar;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);

//        // 액티비티 시작 시 첫 번째 프래그먼트를 표시
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.reviewFragment, new ReviewFragment())
//                    .commit();
//        }

        // 회원가입이나 로그인이 되어있는 유저인지 체크해야 한다.
        // 억세스토큰이 있는지를 확인하는 코드로 작성.
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.ACCESS_TOKEN, "");

        if(token.isEmpty()){
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);

            finish();
            return;
        }


        // 탭바 화면이랑 연결
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 프레그먼트 객체 생성
        reviewFragment = new ReviewFragment();
        meetingFragment = new MeetingFragment();
        mapFragment = new MapFragment();
        plannerFragment = new PlannerFragment();

        // 탭바가 눌렸을 때 프레그먼트 이동
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                Fragment fragment = null;

                if(itemId == R.id.reviewFragment) {

                    fragment = reviewFragment;


                } else if (itemId == R.id.meetingFragment ) {

                    fragment = meetingFragment;


                } else if (itemId == R.id.mapFragment) {

                    fragment = mapFragment;


                } else if (itemId == R.id.plannerFragment) {

                    fragment = plannerFragment;

                }
                return loadFragment(fragment);
            }
        });



    }

    boolean loadFragment(Fragment fragment){
        if(fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();// 화면 전환 코드
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 액션 바(ActionBar)의 메뉴가 나오도록 설정한다!!
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 유저가 선택 한 것이 + 아이콘인 경우, AddActivity를 실행
        // 누른 아이콘의 아이디를 가져온다.(안드로이드에서 res안에 있는 것들의 item은 정수형으로 관리한다.)
        int itemId = item.getItemId();

        // TODO: 모든 기능을 다 떄려박자, 모임, 지도, 가계부, 오늘의 추천 메뉴?

        if(itemId == R.id.menuSearch) {


        } else if (itemId == R.id.menuAdd) {

            Intent intent = new Intent(MainActivity.this, MeetingCreateActivity.class);
            startActivity(intent);

        } else if (itemId == R.id.menuRecommend) {

        }

        return super.onOptionsItemSelected(item);


    }
}
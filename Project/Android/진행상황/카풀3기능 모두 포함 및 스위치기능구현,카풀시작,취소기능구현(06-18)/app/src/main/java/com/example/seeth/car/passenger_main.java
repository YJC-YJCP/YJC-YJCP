package com.example.seeth.car;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.seeth.car.BOARDING_PASS;
import com.example.seeth.car.COMMUTING_TIME;
import com.example.seeth.car.ObjectTable;

import java.util.Queue;

public class passenger_main extends AppCompatActivity{
    Button right =null;
    Button left =null;
    String id=null; //내아이디
    String goback ="등교"; //카풀일련번호 등하교
    String position="월"; //카풀일련번호 요일
    int check_position;
    ClientSocket sk;
    Fragment fr = null;
    Queue<ObjectTable> queue3,queue4;
    double latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_passenger_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent2 = new Intent(this.getIntent());
        id = intent2.getStringExtra("id");
        latitude = intent2.getDoubleExtra("latitude",0.0);
        longitude = intent2.getDoubleExtra("longitude",0.0);

        select_db_state();

        right = (Button)findViewById(R.id.main_right);
        right.setBackgroundColor(Color.rgb(213,213,213));
        left = (Button)findViewById(R.id.main_left);
        left.setBackgroundColor(Color.rgb(178,235,244));

        right.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                left.setEnabled(true);
                right.setEnabled(false);
                right.setBackgroundColor(Color.rgb(178,235,244));
                left.setBackgroundColor(Color.rgb(213,213,213));
                goback="하교";
                select_db_state();
            }
        });

        left.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                left.setEnabled(false);
                right.setEnabled(true);
                goback="등교";
                left.setBackgroundColor(Color.rgb(178,235,244));
                right.setBackgroundColor(Color.rgb(213,213,213));
                select_db_state();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText("월"));
        tabLayout.addTab(tabLayout.newTab().setText("화"));
        tabLayout.addTab(tabLayout.newTab().setText("수"));
        tabLayout.addTab(tabLayout.newTab().setText("목"));
        tabLayout.addTab(tabLayout.newTab().setText("금"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                check_position = tab.getPosition();
                Fragment sel;
                if (check_position == 0) {
                    position="월";
                    select_db_state();
                }
                else if(check_position == 1){
                    position="화";
                    select_db_state();
                }
                else if(check_position == 2){
                    position="수";
                    select_db_state();
                }
                else if(check_position == 3){
                    position="목";
                    select_db_state();
                }
                else if(check_position == 4){
                    position="금";
                    select_db_state();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void select_db_state(){
        sk= new ClientSocket(this);
        BOARDING_PASS bp = new BOARDING_PASS();
        bp.setOrderOperation("SELECT");
        bp.setSelectRequest("select * from boarding_pass where CARPOOL_SERIAL_NUMBER like '%/"+position+"/"+goback+"' and OCCUPANT_ID like '"+ id+"';");
        sk.setObj((ObjectTable) bp);
        sk.start();
        while (sk.flag){
            Log.d("TAG3","여기가계속");
        }
        queue3 = sk.getQueue2();
        sk=null;
        bp= (BOARDING_PASS) queue3.peek();

        if(bp.getACCPTANCE_STATE()==null)
        {
            switchFragment(0);
        }else if(Integer.parseInt(bp.getACCPTANCE_STATE())==1){
            switchFragment(1);
        }
        else if(Integer.parseInt(bp.getACCPTANCE_STATE())==2){
            switchFragment(2);
        }
        sk = null;
    }

    public void switchFragment(int i) {
        sk= new ClientSocket(this);
        COMMUTING_TIME ct = new COMMUTING_TIME();
        ct.setOrderOperation("SELECT");
        ct.setSelectRequest("select * from commuting_time where id = '"+id+"' and day = '"+position+"';");
        sk.setObj((ObjectTable)ct);
        sk.start();
        while (sk.flag){
            Log.d("TAG3","시간표검색");
        }
        queue4 = sk.getQueue2();
        sk=null;
        ct = (COMMUTING_TIME)queue4.poll();
        if (i==0) {
            if(ct.getID()==null) {
                fr = new before_time(longitude, latitude, id, position, goback);
            }
            else {
                fr = new search_magic(longitude, latitude, id, position, goback);
            }
        }
        else if(i==1) {
            fr = new waiting(0.0,0.0,longitude,latitude,id,position,goback);
        }
        else if(i==2){
            fr = new progress(id,position,goback);
        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentBorC, fr);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.one:{
                Toast.makeText(this,"내 정보",Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.two:{
                Toast.makeText(this,"시간표",Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.three:{
                Toast.makeText(this,"PUSH 설정",Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.four:{
                Toast.makeText(this,"카풀 신청 목록",Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.five:{
                Toast.makeText(this,"메시지 목록",Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.six:{
                Toast.makeText(this,"고객센터",Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.seven:{
                Toast.makeText(this,"로그아웃",Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

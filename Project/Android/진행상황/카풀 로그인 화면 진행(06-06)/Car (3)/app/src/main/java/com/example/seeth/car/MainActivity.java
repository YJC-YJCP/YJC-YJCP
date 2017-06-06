package com.example.seeth.car;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private Button startButton, cancelButton;
    private Context mContext = null;
    private TabLayout tab;

    private double lat;
    private double lon;
    private int tabID;

    final static int CODE1=2;
    final static int TIME_CODE=3;

    private EditText GTH_text;  //등교시간
    private EditText GTS_text;  //하교시간

    private String[] day = {"월", "화", "수", "목", "금"};
    private String[] daycheck = {"", "", "", "", ""};

    private TMapData tmapdata;
    private TMapView tmapview;
    private String mapKey = "795385d9-f3d0-3d51-abe5-bbb0c6c82258";
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>(); //마커의 중복을 허용하기 위한 마커의 id

    private COMMUTING_TIME commuting_time;
    public Timetable_dialog Timetable_dialog;
    private RelativeLayout tmaplayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button)findViewById(R.id.CarpoolButtonStart);
        startButton.setOnClickListener(this);
        cancelButton = (Button)findViewById(R.id.CarpoolButtonCancel);
        cancelButton.setOnClickListener(this);

        TMapGpsManager gps = new TMapGpsManager(MainActivity.this);
        gps.setMinDistance(5);
        gps.setMinTime(1000);
        gps.setProvider(gps.NETWORK_PROVIDER);
        gps.OpenGps();


        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //day 이름을 가진 TabLayout을 addTabLayout이라는 메소드를 통해 생성한다
        tab = (TabLayout)findViewById(R.id.tabs);
        for(int i=0; i<day.length; i++)
            addTabLayout(tab, day[i]);

        //==여기
        mContext = this;
        tmaplayout = (RelativeLayout)findViewById(R.id.container);
        commuting_time = new COMMUTING_TIME();
        tmap_timetable_show();  //탭을 누르기 전 값 비교 후 티맵/시간표 출력 여부 판단
        //==========

        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                tmap_timetable_show();  //각 탭 클릭할때도 값 비교
                tabID = tab.getPosition();
                switch(tabID) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab){}

            @Override
            public void onTabReselected(TabLayout.Tab tab){}
        });
    }

    //등하교시간에서 id와 요일값을 비교 ? NULL = 시간표출력 : NULL != 티맵 출력
    public void tmap_timetable_show() {
        Log.i("전달유무", "시간표"+commuting_time.getDAY());
        Log.i("전달유무", "데이"+daycheck[tabID]);
        Log.i("전달유무", "탭아이디"+tabID);
        tmaplayout.removeAllViews();
//        if( commuting_time.getID()!=null && daycheck[tabID]!=day[tabID] ) {
        if( commuting_time.getDAY()==day[tabID] && daycheck[tabID]!=day[tabID] ) {
            //티맵 설정
            tmapview = new TMapView(mContext);
            tmapSet(tmapview);
        }
        else {
                SetDialog();

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.activity_timetable2, tmaplayout, true);
                GTS_text = (EditText) view.findViewById(R.id.GTS_text);
                GTH_text = (EditText) view.findViewById(R.id.GTH_text);
        }
    }

    //다이얼로그 생성
    public void SetDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("시간표를 설정해주세요");
        if ("취소" != null) {
            alertDialogBuilder
                    .setMessage("시간표를 입력하지 않으면 카풀 진행이 불가능합니다.")
                    .setCancelable(false)
                    .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void GTS_click(View v){  //시간입력 버튼 클릭( 시간표 )
        Timetable_dialog = new Timetable_dialog(this,GTS_text,GTH_text);
        Timetable_dialog.show();
    }

    public void saveEvent(View v) { //저장버튼 클릭 ( 시간표 )
        tmaplayout.removeAllViews();
        commuting_time.setDAY(day[tabID]);  //디비에 요일값 저장
//        commuting_time.setTIME_FOR_HOME(GTH_text);  //디비에 등교 저장   (sql.time) => 이걸 뭘로 형변환?
//        commuting_time.setTIME_FOR_HOME(GTS_text);  //디비에 하교 저장   **아이디값은 불러오는건가?
        tmapview = new TMapView(mContext);
        tmapSet(tmapview);
        daycheck[tabID] = day[tabID];   //저장에 성공할때마다 해당 요일이 배열에 추가됨.
    }

    @Override
    public void onClick(View v) {
        if( commuting_time.getID()!=null && commuting_time.getDAY()!=null ) {
            mContext = this;
            int id = v.getId();

            switch (id) {
                case R.id.CarpoolButtonStart:   //카풀 등록 버튼
                    Intent i = new Intent(mContext, CarpoolUploadMain.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("time", commuting_time);
                    bundle.putDouble("lat", lat);
                    bundle.putDouble("lon", lon);
                    i.putExtra("bundle", bundle);
                    startActivityForResult(i, CODE1);
            }
        } else {
            Toast.makeText(this, "시간표를 먼저 등록해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case CODE1:
                if(resultCode==RESULT_OK) {//서브Activity에서 보내온 resultCode와 비교
                    //서브액티비티에서 인텐트에 담아온 정보 꺼내기q
                    Bundle bundle = data.getBundleExtra("bundle");
                    Log.i("전달유무", "전달성공");
                    lat = bundle.getDouble("lat");
                    lon = bundle.getDouble("lon");
                    Log.i("전달유무", "전달성공");
                    tmapview.setCenterPoint(lon, lat);
                    tmapview.setLocationPoint(lon, lat);

                    cancelButton.setBackgroundColor(Color.parseColor("#AA1212"));
                }
                break;
            case TIME_CODE:
                if(resultCode==RESULT_OK){

                }
        }

        //end경로는 영진전문대학으로 고정
        TMapPoint end = new TMapPoint(35.894573, 128.621654);
        TMapPoint start = new TMapPoint(lat,lon);
        searchRoute(start, end);
    }

    //화면에 마커를 표시기 위한 메소드


    //경로 출력 메소드
    public void searchRoute(TMapPoint start, TMapPoint end){
        tmapdata = new TMapData();
        tmapview.setZoom(17);

        //철영이 경로 띄우기
        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end,
                new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        tmapview.addTMapPath(polyLine);
                    }
                });
    }

    public void onLocationChange(Location location){
        tmapview.setCenterPoint(location.getLongitude(),location.getLatitude());
        tmapview.setLocationPoint(location.getLongitude(),location.getLatitude());
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

    public void addTabLayout(TabLayout tab, String name) {
        tab.addTab(tab.newTab().setText(name));
    }

    public void tmapSet(TMapView tmapview) {
        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        tmaplayout.addView(tmapview);
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
                Toast.makeText(this,"메시지 목록",Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.five:{
                Toast.makeText(this,"고객센터",Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.six:{
                Toast.makeText(this,"로그아웃",Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.seeth.car;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private Button startButton, cancelButton;
    private Context mContext = null;
    private TabLayout tab;
    private int choiceDay=-1;   //월 = 1, 화 = 2 ... 금 = 5 //요일을 구분하기 위한 변수

    private Bundle bundle;
    private double lat;
    private double lon;
    private String address;
    final static int CODE1=2;
    private double[] makerLat = new double[5];
    private double[] makerLon = new double[5];

    private TMapData tmapdata;
    private TMapView tmapview;
    private String mapKey = "795385d9-f3d0-3d51-abe5-bbb0c6c82258";
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>(); //마커의 위치정보를 저장하는 배열(DB연동)
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>(); //마커의 중복을 허용하기 위한 마커의 id
    private static int mMarkerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button)findViewById(R.id.CarpoolButtonStart);
        startButton.setOnClickListener(this);
        cancelButton = (Button)findViewById(R.id.CarpoolButtonCancel);
        cancelButton.setOnClickListener(this);

        mContext = this;

        //티맵 설정
        tmapview = new TMapView(this);
        tmapSet(tmapview);

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
        String[] day = {"월", "화", "수", "목", "금"};
        tab = (TabLayout)findViewById(R.id.tabs);
        for(int i=0; i<day.length; i++)
            addTabLayout(tab, day[i]);

        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = 0;
                position = tab.getPosition();
                switch(position) {
                    case 0:
                        TMapGpsManager gps = new TMapGpsManager(MainActivity.this);
                        gps.setMinDistance(5);
                        gps.setMinTime(1000);
                        gps.setProvider(gps.NETWORK_PROVIDER);
                        gps.OpenGps();
                        break;
                    case 1:
                        tmapview.setLocationPoint(128.611319, 35.889904);
                        break;
                    case 2:
                        tmapview.setLocationPoint(129.159890,35.158796);
                        break;
                    case 3:
                        tmapview.setLocationPoint(129.159890, 35.158796);
                        break;
                    case 4:
                        tmapview.setLocationPoint(128.621654, 35.894573);
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab){}

            @Override
            public void onTabReselected(TabLayout.Tab tab){}
        });
    }

    @Override
    public void onClick(View v) {
        mContext = this;
        int id = v.getId();

        switch (id) {
            case R.id.CarpoolButtonStart:
                Intent i = new Intent(mContext, CarpoolUploadMain.class);
                i.putExtra("lat", lat);
                i.putExtra("lon", lon);
                startActivityForResult(i, CODE1);
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
        }

        //end경로는 영진전문대학으로 고정
        TMapPoint end = new TMapPoint(35.894573, 128.621654);
        TMapPoint start = new TMapPoint(lat,lon);
        searchRoute(start, end);
//
//        for (int i = 0; i<makerLat.length; i++) {
//            double lat = makerLat[i];
//            double lon = makerLon[i];
//            addMarker(lat, lon);
//        }
    }

    //화면에 마커를 표시기 위한 메소드
    public void addMarker(double Latitude, double Longitude) {//지도에 마커 추가
        TMapPoint poi = new TMapPoint(Latitude, Longitude);
        TMapMarkerItem APmarker = new TMapMarkerItem();
        Bitmap bitmap = null;
        mContext = this;

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int min = now.get(Calendar.MINUTE);
        int sec = now.get(Calendar.SECOND);
        String nowTime = hour + "시 " + min + "분 " + sec + "초";

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.add_marker);
        //add_marker는 지도에 추가할 마커 이미지입니다.
        APmarker.setTMapPoint(poi);
        APmarker.setName("테스트");
        APmarker.setVisible(APmarker.VISIBLE);
        APmarker.setIcon(bitmap);
        APmarker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        APmarker.setCalloutTitle("해당 장소의 이름");
        APmarker.setCalloutSubTitle(nowTime);       //풍선뷰 보조메세지
//        tItem.setCalloutLeftImage(bitmap);  //풍선뷰의 왼쪽 이미지 지정 //오른쪽은 RIGHT

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.car);
        APmarker.setCalloutRightButtonImage(bitmap);

        String strID = String.format("pmarker%d", mMarkerID++);
        tmapview.addMarkerItem(strID, APmarker);
        mArrayMarkerID.add(strID);

        //풍선뷰 선택할 때 나타나는 이벤트
        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {

                lat = markerItem.latitude;
                lon = markerItem.longitude;

                //위도, 경도로 주소 검색하기
                tmapdata.convertGpsToAddress(lat, lon, new TMapData.ConvertGPSToAddressListenerCallback() {
                    @Override
                    public void onConvertToGPSToAddress(String strAddress) {
                        address = strAddress;
                    }
                });
                Toast.makeText(mContext, "주소 : " + address, Toast.LENGTH_SHORT).show();
            }
        });
    }

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
        RelativeLayout tmaplayout = (RelativeLayout)findViewById(R.id.container);

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

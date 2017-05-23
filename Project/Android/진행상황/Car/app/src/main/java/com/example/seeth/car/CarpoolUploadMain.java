package com.example.seeth.car;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.Calendar;

import database.ACCESS_POINT_MANAGEMENT;
import database.CARPOOL_PATH_MANAGEMENT;
import database.USER;
import database.WAYPOINT;

public class CarpoolUploadMain extends AppCompatActivity implements TMapView.OnLongClickListenerCallback, View.OnClickListener {

    //데이터베이스 사용을 위한 클래스 선언
    USER user;
    ACCESS_POINT_MANAGEMENT access_point_management;
    WAYPOINT waypoint;
    CARPOOL_PATH_MANAGEMENT carpool_path_management;

    int cnt=0;  //경유지를 카운트하는 변수 최대 3개까지
    int mMarkerID=0;    //마커를 구별하기 위한 ID
    int carpoolJoinCount=0;     //AP지점을 최대 3개까지 카운트하기위한 변수

    private Context mContext;

    private ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();     //경유지 정보를 받아오는 어레이리스트
    private String name=null;   //출발지나 경유지에서 전달받은 위치의 이름을 저장
    private double lat;
    private double lon;

    //마커의 위도경도를 저장하는 배열
    private double[] makerLat = new double[5];
    private double[] makerLon = new double[5];
    ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>(); //마커의 위치정보를 저장하는 배열
    ArrayList<String> mArrayMarkerID = new ArrayList<String>(); //마커의 중복을 허용하기 위한 마커의 id

    private TMapView tmapview;
    private TMapData tmapdata;

    final static int CARPOOL_STARTING_SUCCECS=1;    //출발지 추가를 판단하는 코드
    final static int WAYPOINT_SUCCECS=2;    //경유지 추가를 판단하는 코드

    final TMapPoint YJpoint = new TMapPoint(35.894573, 128.621654);
    private TMapPoint start;
    private TMapPoint end;
    int school_home_flag = 1;   //등교와 하교를 구분한 변수를 저장
    final int school_flag = 1;  //등교 = 1
    final int home_flag = 2;    //하교 = 2

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carpool_upload_main);//XML 레이아웃에 정의된 뷰들을 메모리상에 객체화 하는 역할을 수행하는 메소드

        RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.uploadMainTmap);
        carpool_path_management = new CARPOOL_PATH_MANAGEMENT();

        Button goSchool = (Button)findViewById(R.id.goSchool);
        Button goHome = (Button)findViewById(R.id.goHome);
        goSchool.setOnClickListener(this);
        goHome.setOnClickListener(this);

        start_end_flag();

        //gps 설정
        TMapGpsManager gps = new TMapGpsManager(CarpoolUploadMain.this);
        gps.setMinDistance(0);
        gps.setMinTime(1000);
        gps.setProvider(gps.GPS_PROVIDER);
        gps.OpenGps();

        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //티맵 설정
        tmapview = new TMapView(this);
        tmapview.setSKPMapApiKey("b9c365d5-6d93-385f-bf50-0b423ece22c1");
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setCompassMode(true);
        tmapview.setTrackingMode(true);

        relativelayout.addView(tmapview);
    }

    //등교와 하교를 구분하는 메소드 // 등교일때는 end에 학교 위치, 하교일때는 start에 학교 위치
    public void start_end_flag() {
        end = null;
        start = null;
        TMapPoint t;
        if(school_home_flag == school_flag)
            end = YJpoint;
        else if(school_home_flag == home_flag)
            start = YJpoint;
    }

    //등교or 하교 / 카풀등록or카풀정보 버튼 이벤트
    @Override
    public void onClick(View v) {
        school_home_flag=0;
        int id = v.getId();
        //등교를 선택시 스쿨홈플래그에 1을 전달, 하교를 선택할시 2를 전달
        switch (id) {
            //등교일때는 엔드에 YJpoint, 하교일때는 start에 YJpoint
            case R.id.goSchool:
                v.setBackgroundColor(Color.parseColor("#039BE5"));
                school_home_flag = school_flag;
                Toast.makeText(this, "등교를 선택했습니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.goHome:
                v.setBackgroundColor(Color.parseColor("#039BE5"));
                school_home_flag = home_flag;
                Toast.makeText(this, "하교를 선택했습니다.", Toast.LENGTH_SHORT).show();
                break;
        }
        setEditTextName();
        start_end_flag();
    }

    //등하교 클릭시 출발지나 목적지에 텍스트 추가
    public void setEditTextName() {
        EditText start = (EditText)findViewById(R.id.sourceSearch);

        start.setText(null);

        if(school_home_flag == 1)
            start.setHint("출발지를 선택하세요");
        else
            start.setHint("목적지를 선택하세요");

    }

    //출발지 추가버튼 이벤트
    public void sourceClick(View v) {
        Bundle bundle = new Bundle();
        Intent in = new Intent(this, CarpoolLocationSearch.class);

        bundle.putInt("startFlag", CARPOOL_STARTING_SUCCECS);
        bundle.putInt("school_home_flag", school_home_flag);    //등교와 하교를 구분한 플래그값 전달
        in.putExtra("bundle", bundle);
        //호출한 액티비티에서 결과값을 받아오는 경우
        startActivityForResult(in, CARPOOL_STARTING_SUCCECS);
    }

    //경유지 추가버튼 이벤트
    public void transferAddClick(View v) {
        if(name == null)
            Toast.makeText(this, "출발지를 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
        else {
            //다음 화면에 전달할 객체 생성
            waypoint = new WAYPOINT();
            Bundle bundle = new Bundle();
            Intent in = new Intent(this, CarpoolLocationSearch.class);

            bundle.putSerializable("waypoint", waypoint);
            bundle.putInt("school_home_flag", school_home_flag);    //등교와 하교를 구분한 플래그값 전달
            bundle.putInt("startFlag", WAYPOINT_SUCCECS);
            in.putExtra("bundle", bundle);

            startActivityForResult(in, WAYPOINT_SUCCECS);
        }
    }

    //서브액티비티가 값을 보내오면 자동호출되는 메소드
    //requestCode에 메인 액티비티에서 보낸 CODE가 담겨온다.
    //onActivityResult( 메인액티비티에서 보낸 코드,서브액티비티에서 보내온 리절트코드,서브액티비티에서 데이터를 담아 보낸 인텐트 )
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {

            Bundle bundle = data.getBundleExtra("bundle");
            name = bundle.getString("name");

            switch (requestCode) {
                case CARPOOL_STARTING_SUCCECS:
                    if (resultCode == RESULT_OK) {
                        carpool_path_management = (CARPOOL_PATH_MANAGEMENT) bundle.getSerializable("C_P_M");


                        tmapview.setLocationPoint(lon, lat);
                        EditText e = (EditText) findViewById(R.id.sourceSearch);
                        e.setHint("출발지 : " + name);

                        Button successButton = (Button) findViewById(R.id.success);
                        successButton.setBackgroundColor(Color.parseColor("#AA1212"));
                        successButton.setTextColor(Color.WHITE);

                        if(school_home_flag == 1) {
                            lat = carpool_path_management.getSTARTING_POINT_LATITUDE();
                            lon = carpool_path_management.getSTARTING_POINT_LONGITUDE();
                            start = new TMapPoint(lat, lon);
                        }
                        else {
                            lat = carpool_path_management.getDESTINATION_LATITUDE();
                            lon = carpool_path_management.getDESTINATION_LONGITUDE();
                            end = new TMapPoint(lat, lon);
                        }
                        searchRoute();
                    }
                    break;

                case WAYPOINT_SUCCECS:
                    if (resultCode == RESULT_OK) {//서브Activity에서 보내온 resultCode와 비교
                        //서브액티비티에서 인텐트에 담아온 정보 꺼내기
                        waypoint = (WAYPOINT) bundle.getSerializable("waypoint");
                        waypoint.setWAYPOINT_ORDER(cnt);

                        lat = waypoint.getWAYPOINT_LATITUDE();
                        lon = waypoint.getWAYPOINT_LONGITUDE();
                        TMapPoint transfer = new TMapPoint(lat, lon);
                        passList.add(transfer);

                        addTransferLocation(name);
                        cnt++;
                        searchRoute();
                    }
                    break;
            }
        }

    }

    public void successResult() {
        Intent intent = new Intent(mContext, MainActivity.class);

        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("lon", lon);
        bundle.putSerializable("waypoint", waypoint);
        bundle.putSerializable("carpool_path_management", carpool_path_management);
        bundle.putSerializable("passList", passList);
        intent.putExtra("bundle", bundle);
        setResult(RESULT_OK, intent);
        CarpoolUploadMain.this.finish();
    }

    //등록완료 버튼 이벤트
    public void successButtonClicked(View v){
        final AlertDialog.Builder ratingdialog = new AlertDialog.Builder(this);

        mContext = this;
        ratingdialog.setIcon(getResources().getDrawable(R.drawable.per1));
        ratingdialog.setTitle("탑승인원지정");

        View linearlayout = getLayoutInflater().inflate(R.layout.rating_dialog, null);
        ratingdialog.setView(linearlayout);

        //탑승인원지정
        final RatingBar rating = (RatingBar)linearlayout.findViewById(R.id.ratingbar);

        ratingdialog.setPositiveButton("등록",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int peopleCnt = (int)rating.getRating();
                        carpool_path_management.setTHE_NUMBER_OF_OCCUPANT(peopleCnt);
                        successResult();
                        Log.i("전달유무", "마커 위치 배열 인텐트로 전달 성공");
                    }
                })
                .setNegativeButton("이전",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        ratingdialog.create();
        ratingdialog.show();

    }

    //화면에 마커를 표시하기 위한 메소드
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
        APmarker.setName("출발지");
        APmarker.setVisible(APmarker.VISIBLE);
        APmarker.setIcon(bitmap);
        APmarker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        APmarker.setCalloutTitle(name);
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
            }
        });
    }

    //길게 누를때 이벤트
    @Override
    public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint) {

        double lat = Math.round(tMapPoint.getLatitude()*10000)/10000.0;     //경도 4자리 반올림
        double lon = Math.round(tMapPoint.getLongitude()*10000)/10000.0;     //위도 4자리 반올림

        if(name == null)
            Toast.makeText(this, "카풀경로를 먼저 지정해주세요.", Toast.LENGTH_SHORT).show();
        else if(carpoolJoinCount<3) {
            m_mapPoint.add(new MapPoint(lat, lon));
            makerLat[carpoolJoinCount] = lat;
            makerLon[carpoolJoinCount] = lon;
            for (int i = 0; i < m_mapPoint.size(); i++) {
                addMarker(m_mapPoint.get(i).getLatitude(), m_mapPoint.get(i).getLongitude());
            }
        }
        else {
            Toast.makeText(this, "카풀 조인 지점은 최대 3개까지입니다.", Toast.LENGTH_SHORT).show();
        }
        carpoolJoinCount++;
        Log.i("전달유무", "맵포인트사이즈 : " + m_mapPoint.size());
    }

    //경로 출력 메소드
    public void searchRoute(){
        tmapdata = new TMapData();
        tmapview.setZoom(17);

        if(school_home_flag==1) {   //등교
            tmapview.setLocationPoint(start.getLongitude(), start.getLatitude());

            //철영이 경로 띄우기
            tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end, passList, 0,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            tmapview.addTMapPath(polyLine);
                        }
                    });
        }
        else if(school_home_flag == 2) {    //하교
            tmapview.setLocationPoint(end.getLongitude(), end.getLatitude());

            //철영이 경로 띄우기
            tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, end, start, passList, 0,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            tmapview.addTMapPath(polyLine);
                        }
                    });
        }
        Toast.makeText(this, calcDistance(lat, lon, end.getLatitude(), end.getLongitude()), Toast.LENGTH_SHORT).show();
    }

    public static String calcDistance(double start_lat, double start_lon, double end_lat, double end_lon){
        double EARTH_R, Rad, radLat1, radLat2, radDist;
        double distance, ret;

        EARTH_R = 6371000.0;
        Rad = Math.PI/180;
        radLat1 = Rad * start_lat;
        radLat2 = Rad * end_lat;
        radDist = Rad * (start_lon - end_lon);

        distance = Math.sin(radLat1) * Math.sin(radLat2);
        distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
        ret = EARTH_R * Math.acos(distance);

        double rslt = Math.round(Math.round(ret) / 1000);
        String result = rslt + " km";
        if(rslt == 0) result = Math.round(ret) +" m";

        return result;
    }


    //경유지 추가버튼을 누르면 추가할 경유지를 생성하기 위한 메소드
    public void addTransferLocation(String routeName) {
        final LinearLayout addRouteLayout = (LinearLayout) findViewById(R.id.addRouteLayout);

        //EditText 생성
        final EditText addLocation = new EditText(this);
        addLocation.setHint("경유지 : " + routeName);
        Log.i("전달유무", "해당 로그는 경유지의 이름을 보여주는 로그입니다.\n" + "로그는 : " + routeName);
        addLocation.setId(cnt);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("전달유무", "클릭 : " + cnt);
                int id = v.getId();
                switch (id) {
                    case 0:
                        dialogAdd(id, addRouteLayout, addLocation);
                        break;
                    case 1:
                        dialogAdd(id, addRouteLayout, addLocation);
                        break;
                    case 2:
                        dialogAdd(id, addRouteLayout, addLocation);
                        break;
                    case 3:
                        dialogAdd(id, addRouteLayout, addLocation);
                        break;
                    case 4:
                        dialogAdd(id, addRouteLayout, addLocation);
                        break;
                }
                searchRoute();
            }
        });

        //layout_width, layout_height, gravity 설정
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addLocation.setLayoutParams(lp);

        //부모 뷰에 추가
        addRouteLayout.addView(addLocation);
    }

    //추가한 경유지를 삭제하는 메소드
    public void deleteTransfer() {
    }

    //다이얼로그를 출력하는 메소드
    public void dialogAdd(final int index, final LinearLayout layout, final View ID) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // 제목셋팅
        alertDialogBuilder.setTitle("경유지 취소");
        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage("해당 경유지를 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        passList.remove(index);
                        layout.removeView(ID);
                    }
                })
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });

        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        // 다이얼로그 보여주기
        alertDialog.show();
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

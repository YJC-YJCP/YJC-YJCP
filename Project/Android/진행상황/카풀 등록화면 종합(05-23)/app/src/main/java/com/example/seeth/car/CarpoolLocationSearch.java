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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.Calendar;

import database.CARPOOL_PATH_MANAGEMENT;
import database.WAYPOINT;

/**
 * Created by seeth on 2017-05-13.
 */

public class CarpoolLocationSearch extends AppCompatActivity {
    WAYPOINT waypoint = null;
    CARPOOL_PATH_MANAGEMENT carpool_path_management = null;

    private int buttonID=0;
    private Context mContext;

    private double lat;
    private double lon;

    final TMapPoint YJpoint = new TMapPoint(35.894573, 128.621654);
    private TMapPoint start;
    private TMapPoint end;

    private ArrayList<TMapPoint> list = new ArrayList<TMapPoint>();
    private ArrayList<String> name = new ArrayList<String>();
    private String locationName=null;
    private int buttonCount=0;

    private LinearLayout keywordButtonLayout;
    private TMapView tmapview;
    private String mapKey = "795385d9-f3d0-3d51-abe5-bbb0c6c82258";
    private TMapData tmapdata;
    private TMapPoint point;

    int start_home_flag=0;
    int flag = 0;
    int START=0;
    int TRANSFER=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carpool_location_search);

        //플래그 변수에 등교와 하교를 구분할 값을 전달받고 플래그로 등교하교를 구분하자
        Bundle bundle = getIntent().getBundleExtra("bundle");
        flag = bundle.getInt("startFlag");
        start_home_flag = bundle.getInt("school_home_flag");
        Log.i("전달유무", "전달받은 등하교 플래그 : " + start_home_flag);

        start_end_flag();   //전 화면에서 선택한 등하교 값을 전달받아 출발지나 목적지에 학교위치값 전달

        //flag가 1이면 START가 1 이고 flag가 2면 TRANSFER가 2
        if(flag == 1)
            START = 1;
        else if(flag == 2)
            TRANSFER = 2;

        Log.i("전달유무","flag는 : " + flag);
        Log.i("전달유무","START와 TRANSFER : " + START + " " + TRANSFER);

        waypoint = (WAYPOINT) bundle.getSerializable("waypoint");
        carpool_path_management = new CARPOOL_PATH_MANAGEMENT();

        keywordButtonLayout = (LinearLayout)findViewById(R.id.keywordButtonLayout);


        tmapview = new TMapView(this);
        tmapSet(tmapview);
    }

    public void tmapSet(TMapView tmapview) {
        RelativeLayout tmap = (RelativeLayout)findViewById(R.id.mapview);
        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        tmap.addView(tmapview);
    }

    //등교와 하교를 구분하는 메소드 // 등교일때는 end에 학교 위치, 하교일때는 start에 학교 위치
    public void start_end_flag() {
        end = null;
        start = null;

        if(start_home_flag == 1)
            end=YJpoint;
        else if(start_home_flag == 2)
            start=YJpoint;
    }

    //출발지에 반환할 값
    public void startResult() {
        Intent intent = new Intent(mContext, CarpoolUploadMain.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("C_P_M", carpool_path_management);
        bundle.putString("name", locationName);
        intent.putExtra("bundle",bundle);
        Log.i("전달유무", "번들 : " + bundle);
        setResult(RESULT_OK, intent);
        CarpoolLocationSearch.this.finish();
    }

    //경유지에 반환할 값
    public void transferResult() {
        Intent intent = new Intent(mContext, CarpoolUploadMain.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("waypoint", waypoint);
        bundle.putString("name", locationName);
        intent.putExtra("bundle", bundle);
        setResult(RESULT_OK, intent);
        CarpoolLocationSearch.this.finish();
    }

    //디비에 저장할 경유지 값
    public void saveWAYPOINT(double lat, double lon) {
        waypoint.setWAYPOINT_LATITUDE(lat); //선택한 위치의 경도
        waypoint.setWAYPOINT_LONGITUDE(lon);    //선택한 위치의 위도
    }

    //디비에 저장할 출발지/목적지 값
    public void saveCARPOOL_PATH_MANAGEMENT(double lat, double lon) {
        //등교와 하교 구분해야 할것
//        if(START==1) {
//        }
//        else if(TRANSFER==2) {
//            carpool_path_management.setDESTINATION_LATITUDE(lat);
//            carpool_path_management.setDESTINATION_LONGITUDE(lon);
//        }
        carpool_path_management.setSTARTING_POINT_LATITUDE(lat);
        carpool_path_management.setSTARTING_POINT_LONGITUDE(lon);
        Log.i("전달유무" ,"준 값 : " + lat + lon);
    }

    //주소를 검색하기위한 메소드
    public void serachLocation() {
        tmapdata = new TMapData();

        EditText locationSearch = (EditText)findViewById(R.id.locationSearch);
        String str= locationSearch.getText().toString();

        tmapdata.findAllPOI(str,5, new TMapData.FindAllPOIListenerCallback(){ // POI검색수 1개로 제한
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                buttonCount=0;
                for (int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = poiItem.get(i);
                    lat = item.getPOIPoint().getLatitude(); // 검색한 poi 경도 값 저장
                    lon = item.getPOIPoint().getLongitude();// 검색한 poi 위도값 저장
                    point = new TMapPoint(lat, lon);
                    list.add(point);
                    name.add(item.getPOIName());

                    buttonCount++;
                }
            }
        });
    }

    //검색버튼을 누르면 지도 밑에 버튼 리스트가 나타나는 이벤트
    public void addTransferLocation(LinearLayout keywordButtonLayout, final ArrayList<TMapPoint> list) {
        keywordButtonLayout.removeAllViews();

        for(int i=0; i<buttonCount; i++) {
            Button addLocation = new Button(this);
            addLocation.setHint(name.get(i));
            addLocation.setTextColor(Color.BLACK);
            addLocation.setId(buttonID);
            buttonID++;
            addLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    double lat = list.get(v.getId()).getLatitude();
                    double lon = list.get(v.getId()).getLongitude();
                    addMarker(lat, lon, name.get(v.getId()));
                    locationName=name.get(v.getId());
                    buttonID=0;
                    switch (v.getId()) {
                        case 0:
                            searchRoute(list.get(v.getId()));
                            break;
                        case 1:
                            searchRoute(list.get(v.getId()));
                            break;
                        case 2:
                            searchRoute(list.get(v.getId()));
                            break;
                        case 3:
                            searchRoute(list.get(v.getId()));
                            break;
                        case 4:
                            searchRoute(list.get(v.getId()));
                            break;
                    }
                    buttonID=0;

                }
            });
            //layout_width, layout_height, gravity 설정
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 5;
            lp.gravity = Gravity.CENTER;
            addLocation.setLayoutParams(lp);

            //부모 뷰에 추가
            keywordButtonLayout.addView(addLocation);
        }
        list.clear();
        name.clear();
        buttonID=0;
    }

    //검색버튼 클릭 이벤트
    public void searchMapClick(View v){
        serachLocation();
        addTransferLocation(keywordButtonLayout, list);
    }

    //화면에 마커를 표시하기 위한 메소드
    public void addMarker(double Latitude, double Longitude, String name) {//지도에 마커 추가
        Log.i("전달유무", "성공");
        int mMarkerID=0;
        ArrayList<String> mArrayMarkerID = new ArrayList<String>(); //마커의 중복을 허용하기 위한 마커의 id

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
                dialogAdd();
            }
        });
    }

    //다이얼로그를 출력하는 메소드
    public void dialogAdd() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // 제목셋팅
        alertDialogBuilder.setTitle("카풀경로 등록");
        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage("이곳으로 설정하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        saveCARPOOL_PATH_MANAGEMENT(lat, lon);
                        // 프로그램을 종료한다
                        if (START == 1) {
                            startResult();
                        } else if (TRANSFER == 2) {
                            saveWAYPOINT(lat, lon);
                            transferResult();
                        }
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

    //경로 출력 메소드
    public void searchRoute(TMapPoint location) {
//        waypoint.setWAY
        tmapdata = new TMapData();
        tmapview.setZoom(15);

        if (start_home_flag == 1) { //등교일때
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());

            tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, location, end,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            tmapview.addTMapPath(polyLine);
                        }
                    });
        }
        else if(start_home_flag == 2) { //하교일때
            tmapview.setLocationPoint(start.getLongitude(), start.getLatitude());

            tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, location,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            tmapview.addTMapPath(polyLine);
                        }
                    });
        }
    }

}

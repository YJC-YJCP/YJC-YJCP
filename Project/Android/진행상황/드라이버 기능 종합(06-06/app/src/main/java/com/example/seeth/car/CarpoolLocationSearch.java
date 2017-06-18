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

import com.skp.Tmap.TMapAddressInfo;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

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
    //현구추가

    ArrayList<String> locationName2 = new ArrayList<>();
    TMapPoint start;
    TMapPoint end;
    ArrayList<TPoint> mp ;//경유지담는 곳
    String state;
    Button goSchool;
    Button goHome;
    EditText startText;
    EditText endText;
    Intent toStart;
    Intent topassStop;
    Intent toEnd;
    Context cp;
    String str;
    TPoint tt;
    ArrayList<TMapPoint> passList;
    Context cc;
    int nameindex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carpool_location_search);

        //플래그 변수에 등교와 하교를 구분할 값을 전달받고 플래그로 등교하교를 구분하자
        //flag가 1이면 START가 1 이고 flag가 2면 TRANSFER가 2

        keywordButtonLayout = (LinearLayout)findViewById(R.id.keywordButtonLayout);


        tmapview = new TMapView(this);
        tmapSet(tmapview);
        Intent it = this.getIntent();
        tt= (TPoint) it.getSerializableExtra("tpoint");
        if(tt.flag.equals("등교지"))
        {
            end= new TMapPoint(tt.latitude,tt.longitude);
            Log.d("test","등교지입니다.");

        }
        else if(tt.flag.equals("하교지")){
            start= new TMapPoint(tt.latitude,tt.longitude);

            Log.d("test","하교지입니다.");
        }
        else if(tt.flag.equals("경유지")){
            ArrayList<TMapPoint> temp = new ArrayList<>();
            tmapdata = new TMapData();
            mp=new ArrayList<>(tt.mp);
            TPoint tt1= (TPoint) it.getSerializableExtra("start");
            start= new TMapPoint(tt1.latitude,tt1.longitude);
            TPoint tt2= (TPoint) it.getSerializableExtra("end");
            end= new TMapPoint(tt2.latitude,tt2.longitude);
            tmapview.setLocationPoint(start.getLongitude(), start.getLatitude());
            if(mp.size()>0) {
                for (int i = 0; i < mp.size(); i++) {
                    TPoint tem = mp.get(i);
                    TMapPoint tmp = new TMapPoint(tem.latitude, tem.longitude);
                    temp.add(tmp);
                }
            }

            tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end,temp,0,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            tmapview.addTMapPath(polyLine);
                            tmapview.refreshMap();
                        }
                    });

        }
    }

    public void tmapSet(TMapView tmapview) {
        RelativeLayout tmap = (RelativeLayout)findViewById(R.id.mapview);
        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(8);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        tmap.addView(tmapview);
    }



    //주소를 검색하기위한 메소드
    public void serachLocation() {
        tmapdata = new TMapData();

        EditText locationSearch = (EditText)findViewById(R.id.locationSearch);
        String str= locationSearch.getText().toString();

        tmapdata.findAllPOI(str,1, new TMapData.FindAllPOIListenerCallback(){ // POI검색수 1개로 제한
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
                    locationName2.add(item.getPOIName());
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
                    TMapPoint tpon= list.get(v.getId());
                    addMarker(tpon.getLatitude(),tpon.getLongitude(), name.get(v.getId()));
                    locationName=name.get(v.getId());
                    buttonID=0;
                    switch (v.getId()) {
                        case 0:
                            searchRoute(list.get(v.getId()));

                            nameindex=0;
                            if(tt.flag.equals("등교지"))
                            {
                                tmapview.zoomToTMapPoint(list.get(v.getId()), end );
                            }
                            else if(tt.flag.equals("하교지"))
                            {
                                tmapview.zoomToTMapPoint(start,list.get(v.getId()) );
                            }
                            else
                                tmapview.zoomToTMapPoint(start,end);
                            break;
                        case 1:
                            searchRoute(list.get(v.getId()));
                            nameindex=1;
                            if(tt.flag.equals("등교지"))
                            {
                                tmapview.zoomToTMapPoint(list.get(v.getId()), end );
                            }
                            else if(tt.flag.equals("하교지"))
                            {
                                tmapview.zoomToTMapPoint(start,list.get(v.getId()) );
                            }
                            else
                                tmapview.zoomToTMapPoint(start,end);
                            break;
                        case 2:
                            searchRoute(list.get(v.getId()));
                            nameindex=2;
                            if(tt.flag.equals("등교지"))
                            {
                                tmapview.zoomToTMapPoint(list.get(v.getId()), end );
                            }
                            else if(tt.flag.equals("하교지"))
                            {
                                tmapview.zoomToTMapPoint(start,list.get(v.getId()) );
                            }
                            else
                                tmapview.zoomToTMapPoint(start,end);
                            break;
                        case 3:
                            searchRoute(list.get(v.getId()));
                            nameindex=3;
                            if(tt.flag.equals("등교지"))
                            {
                                tmapview.zoomToTMapPoint(list.get(v.getId()), end );
                            }
                            else if(tt.flag.equals("하교지"))
                            {
                                tmapview.zoomToTMapPoint(start,list.get(v.getId()) );
                            }
                            else
                                tmapview.zoomToTMapPoint(start,end);
                            break;
                        case 4:
                            searchRoute(list.get(v.getId()));
                            nameindex=4;
                            if(tt.flag.equals("등교지"))
                            {
                                tmapview.zoomToTMapPoint(list.get(v.getId()), end );
                            }
                            else if(tt.flag.equals("하교지"))
                            {
                                tmapview.zoomToTMapPoint(start,list.get(v.getId()) );
                            }
                            else
                                tmapview.zoomToTMapPoint(start,end);
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
        APmarker.setVisible(APmarker.VISIBLE);
        APmarker.setName(locationName2.get(nameindex));
        APmarker.setIcon(bitmap);
        APmarker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        APmarker.setCalloutTitle(name);
        APmarker.setCalloutLeftImage(bitmap);  //풍선뷰의 왼쪽 이미지 지정 //오른쪽은 RIGHT
        APmarker.setAutoCalloutVisible(true);

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
                dialogAdd(lat,lon,markerItem.getName());
            }
        });
    }
    //다이얼로그를 출력하는 메소드
    public void dialogAdd(final double lat, final double lon,final String name) {
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
                        Intent resultIntent = new Intent();
                            TPoint tpoint = new TPoint(lat, lon);
                            tpoint.name=null;
                            tpoint.name=name;

                            tpoint.flag="경유";
                            resultIntent.putExtra("tpoint", tpoint);
                            setResult(RESULT_OK, resultIntent);
                            finish();

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
        tmapdata = new TMapData();
        tmapview.setZoom(15);
        ArrayList<TMapPoint> temp = new ArrayList<>();
        if (tt.flag.equals("등교지")) { //등교일때
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());

            tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, location, end,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            tmapview.addTMapPath(polyLine);
                            tmapview.refreshMap();
                        }
                    });
        }
        else if(tt.flag.equals("하교지")) { //하교일때
            tmapview.setLocationPoint(start.getLongitude(), start.getLatitude());

            tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, location,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            tmapview.addTMapPath(polyLine);
                            tmapview.refreshMap();
                        }
                    });
        }
        else//클릭이 되었을때라...?
        {
            tmapview.setLocationPoint(start.getLongitude(), start.getLatitude());
           if(mp.size()>0) {
               for (int i = 0; i < mp.size(); i++) {
                   TPoint tem = mp.get(i);
                   TMapPoint tmp = new TMapPoint(tem.latitude, tem.longitude);
                   temp.add(tmp);
               }
           }
            temp.add(location);

            tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end,temp,0,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            tmapview.addTMapPath(polyLine);
                            tmapview.refreshMap();

                        }
                    });
        }
    }

}

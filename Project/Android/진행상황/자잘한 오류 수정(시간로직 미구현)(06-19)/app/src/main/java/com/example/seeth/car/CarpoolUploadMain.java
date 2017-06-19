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

import com.skp.Tmap.TMapAddressInfo;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.parsers.ParserConfigurationException;

public class CarpoolUploadMain extends AppCompatActivity {

    //데이터베이스 사용을 위한 클래스 선언
    USER user;
    ACCESS_POINT_MANAGEMENT access_point_management;
    WAYPOINT waypoint;
    CARPOOL_PATH_MANAGEMENT carpool_path_management;

    int mMarkerID=0;    //마커를 구별하기 위한 ID
    int carpoolJoinCount=0;     //AP지점을 최대 3개까지 카운트하기위한 변수
    String WAYPOINT_TOTAL; //user/월/등교
    private Context mContext;
    String[] str4;
    private ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();     //경유지 정보를 받아오는 어레이리스트
    private String name=null;   //출발지나 경유지에서 전달받은 위치의 이름을 저장
    private double lat;
    private double lon;
    ArrayList<TMapPoint> totalRoot =null;
    //마커의 위도경도를 저장하는 배열
    private double[] makerLat = new double[5];
    private double[] makerLon = new double[5];
    ArrayList<String> mArrayMarkerID = new ArrayList<String>(); //마커의 중복을 허용하기 위한 마커의 id
    ClientSocket sk =null;
    private TMapView tmapview;
    private TMapData tmapdata;
    int hour ;
    int second;
    int minute;
    final static int CARPOOL_STARTING_SUCCESS=1;    //출발지 추가를 판단하는 코드
    final static int WAYPOINT_SUCCESS=2;    //경유지 추가를 판단하는 코드
    final static int  CARPOOL_ENDING_SUCCESS= 3;
    final static int  CARPOOL_AREA_SUCCESS= 4;
    final TMapPoint YJpoint = new TMapPoint(35.894573, 128.621654);
    TMapPoint start;
    TMapPoint end;
    int school_home_flag = 1;   //등교와 하교를 구분한 변수를 저장
    final int school_flag = 1;  //등교 = 1
    final int home_flag = 2;    //하교 = 2
    int waycnt;
    ArrayList<TPoint> mp ;//경유지담는 곳
    String state;
    Button goSchool;
    TPoint Tstart;
    TPoint Tend;
    Button goHome;
    EditText startText;
    EditText endText;
    Intent toStart;
    Intent topassStop;
    Intent toEnd;
    Context cp;
    Button carpoolArea = null;
    COMMUTING_TIME CMT= null;
    RelativeLayout relativelayout;
    Date currentTime = new Date();
    Date tempcurrentTime1 = new Date();
    Date tempcurrentTime2 = new Date();
    String day ;
    String ID;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carpool_upload_main);//XML 레이아웃에 정의된 뷰들을 메모리상에 객체화 하는 역할을 수행하는 메소드

        /*인탠트 처리 로직*/
        Intent it= getIntent();
        CMT= (COMMUTING_TIME) it.getSerializableExtra("COMMUTING_TIME");//시간 넣고
        day= it.getStringExtra("day");//요일 넣고
        state=it.getStringExtra("state");//상태 넣고
        ID=it.getStringExtra("ID");//아이디넣고
        WAYPOINT_TOTAL=ID+"/"+day+"/"+state; //user/월/등교
        cp=this;
        relativelayout = (RelativeLayout) findViewById(R.id.uploadMainTmap);
        mp=new ArrayList<>();
        carpoolArea = (Button)findViewById(R.id.carpoolLocation);
        totalRoot=new ArrayList<>();
        carpool_path_management = new CARPOOL_PATH_MANAGEMENT();
        startText = (EditText)findViewById(R.id.sourceSearch);

        endText = (EditText)findViewById(R.id.destinationLocition);

        //========시간초기화로직
        //   goSchool.setOnClickListener(this);
        // goHome.setOnClickListener(this);
        //gps 설정
        TMapGpsManager gps = new TMapGpsManager(CarpoolUploadMain.this);
        gps.setMinDistance(0);
        gps.setMinTime(1000);
        gps.setProvider(gps.GPS_PROVIDER);
        gps.OpenGps();
        sk=new ClientSocket(this);
        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //티맵 설정
        tmapview = new TMapView(this);
        tmapview.setSKPMapApiKey("c2b1ca96-9e28-3cc7-8697-092c86b91e5d");
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setCompassMode(true);
        tmapview.setTrackingMode(true);

        relativelayout.addView(tmapview);
        if(state.equals("등교")){
            hour=CMT.getTIME_FOR_SCHOOL().getHours();
            minute=CMT.getTIME_FOR_SCHOOL().getMinutes();
            second=CMT.getTIME_FOR_SCHOOL().getSeconds();
            currentTime.setHours(CMT.getTIME_FOR_SCHOOL().getHours());
            currentTime.setMinutes(CMT.getTIME_FOR_SCHOOL().getMinutes());
            //======백그라운드 처리
            mp=null;
            mp= new ArrayList<TPoint>();
            end=null;
            end=new TMapPoint(35.89601207015857,128.62196445465088);
            //=====포그라운드 처리
            Tend=null;
            Tend =new TPoint(35.89601207015857,128.62196445465088);
            start=null;
            Tstart=null;
            Tend.hour= String.valueOf(currentTime.getHours());
            Tend.minute= String.valueOf(currentTime.getMinutes());
            endText.setText("영진전문대학");
            Tend.description="영진전문대학";
            startText.setText(null);
            startText.setSelected(true);
            startText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toStart= new Intent(CarpoolUploadMain.this,CarpoolLocationSearch.class);
                    TPoint tpoint = new TPoint(end.getLatitude(),end.getLongitude());
                    tpoint.flag="등교지";
                    toStart .putExtra("tpoint",tpoint);
                    startActivityForResult(toStart, CARPOOL_STARTING_SUCCESS);

                }
            });

            tmapview.removeAllMarkerItem();
            tmapview.removeTMapPath();
            tmapview.removeAllTMapPolyLine();
            tmapview.refreshMap();
            tmapview.setLocationPoint(128.62196445465088,35.89601207015857 );
            endText.setOnClickListener(null);
            endText.setSelected(false);
        }else if(state.equals("하교")){
            currentTime.setHours(CMT.getTIME_FOR_HOME().getHours());
            currentTime.setMinutes(CMT.getTIME_FOR_HOME().getMinutes());
            endText.setText(null);
            endText.setSelected(true);
            mp=null;
            mp= new ArrayList<TPoint>();
            start=null;
            start=new TMapPoint(35.89601207015857,128.62196445465088);
            Tstart =new TPoint(35.89601207015857,128.62196445465088);
            Tstart.description="영진전문대학";
            Tstart.hour= String.valueOf(currentTime.getHours());
            Tstart.minute= String.valueOf(currentTime.getMinutes());
            Tend=null;
            endText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toEnd = new Intent(CarpoolUploadMain.this,CarpoolLocationSearch.class);
                    TPoint tpoint = new TPoint(start.getLatitude(),start.getLongitude());
                    tpoint.flag="하교지";
                    toEnd.putExtra("tpoint",tpoint);
                    startActivityForResult(toEnd, CARPOOL_ENDING_SUCCESS);
                }
            });
            startText.setText("영진전문대학");
            startText.setSelected(false);

            tmapview.removeAllMarkerItem();
            tmapview.removeTMapPath();
            tmapview.removeAllTMapPolyLine();
            tmapview.refreshMap();
            tmapview.setLocationPoint(128.62196445465088,35.89601207015857 );
        }

    }



    //등하교 클릭시 출발지나 목적지에 텍스트 추가


    //AP 추가 버튼 이벤트
    public void carpoolArea(View v){
        if(start==null||end==null)
            Toast.makeText(this, "출발지 또는 목적지를 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
        else {
            Intent carpoolArea = new Intent(this,CarpoolAreaMain.class);
            TPoint tpr = new TPoint(0,0);
            tpr.mp=new ArrayList<>(mp);
            tpr.time=currentTime;
            Tstart.flag="출발";
            Tend.flag="도착";
            tpr.mp.add(0,Tstart);
            tpr.mp.add(tpr.mp.size(),Tend);
            carpoolArea.putExtra("locationArray",tpr);
            carpoolArea.putExtra("state",state);
            startActivityForResult(carpoolArea, CARPOOL_AREA_SUCCESS);
        }
    }

    //경유지 추가버튼 이벤트
    public void transferAddClick(View v) {
        if(start==null||end==null)
        Toast.makeText(this, "출발지 또는 목적지를 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
        else {
            if(state.equals("등교")){
                hour=CMT.getTIME_FOR_SCHOOL().getHours();
                minute=CMT.getTIME_FOR_SCHOOL().getMinutes();
                second=CMT.getTIME_FOR_SCHOOL().getSeconds();
            }
            //다음 화면에 전달할 객체 생성
            topassStop= new Intent(CarpoolUploadMain.this,CarpoolLocationSearch.class);

            //=========경유지 입력시 인탠트에 담을 번들
            TPoint tpoint = new TPoint(end.getLatitude(),end.getLongitude());
            tpoint.flag="경유지";
            tpoint.mp=new ArrayList<>(this.mp);
            topassStop.putExtra("tpoint",tpoint);
            //==출발지 정보를 인탠트에 담자.
            TPoint starttpoint = new TPoint(start.getLatitude(),start.getLongitude());
            topassStop.putExtra("start",starttpoint);
            //==목적지 정보를 인탠트에 담자.
            TPoint endtpoint = new TPoint(end.getLatitude(),end.getLongitude());
            topassStop.putExtra("end",tpoint);
            startActivityForResult(topassStop, WAYPOINT_SUCCESS);
        }
    }

    //서브액티비티가 값을 보내오면 자동호출되는 메소드
    //requestCode에 메인 액티비티에서 보낸 CODE가 담겨온다.
    //onActivityResult( 메인액티비티에서 보낸 코드,서브액티비티에서 보내온 리절트코드,서브액티비티에서 데이터를 담아 보낸 인텐트 )
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            switch (requestCode) {
                case CARPOOL_STARTING_SUCCESS://출발지로 왔다면?
                    if (resultCode == RESULT_OK) {
                        tmapdata=new TMapData();
                        TPoint td= (TPoint) data.getSerializableExtra("tpoint");
                        start= new TMapPoint(td.latitude,td.longitude);//받아온 출발지 위도 경도값 주고
                        Tstart= new TPoint(td.latitude,td.longitude);
                        tmapview.removeAllMarkerItem();
                        startText.setText(td.description);//이름까지 입력해주고
                        Tend= new TPoint(end.getLatitude(),end.getLongitude());
                        Tend.description=endText.getText().toString();
                        Tstart.description=td.description;
                        tmapview.zoomToTMapPoint(start, end );

                        timeSelect(Tstart,Tend,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 하교
                        tmapview.zoomToTMapPoint(start, end );
                        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end,
                                new TMapData.FindPathDataListenerCallback() {
                                    @Override
                                    public void onFindPathData(TMapPolyLine polyLine) {
                                        tmapview.addTMapPath(polyLine);
                                        tmapview.refreshMap();
                                    }
                                });
                        tmapview.refreshMap();

                    }

                    break;

                case WAYPOINT_SUCCESS:
                    if (resultCode == RESULT_OK) {//서브Activity에서 보내온 resultCode와 비교
                        //서브액티비티에서 인텐트에 담아온 정보 꺼내기
                        final LinearLayout addRouteLayout = (LinearLayout) findViewById(R.id.addRouteLayout);
                        //EditText 생성
                        final EditText addLocation = new EditText(this);
                        tmapdata=new TMapData();
                        TPoint tt= (TPoint) data.getSerializableExtra("tpoint");
                        tt.flag="경유";
                        TMapPoint Tway = new TMapPoint(tt.latitude,tt.longitude);

                        mp.add(tt);
                        //=========스타트랑 엔드 추가
                        TPoint sts = new TPoint(start.getLatitude(),start.getLongitude());
                        sts.description=startText.getText().toString();
                        TPoint ens = new TPoint(end.getLatitude(),end.getLongitude());
                        ens.description=endText.getText().toString();
                        mp.add(0,sts);
                        mp.add(mp.size(),ens);
                        if(state.equals("등교")){
                            hour=CMT.getTIME_FOR_SCHOOL().getHours();
                            minute=CMT.getTIME_FOR_SCHOOL().getMinutes();
                            second=CMT.getTIME_FOR_SCHOOL().getSeconds();
                        }
                        //============================================
                        addLocation.setText(""+tt.description);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.CENTER;
                        addLocation.setLayoutParams(lp);
                        tmapview.zoomToTMapPoint(start, end );
                        addLocation.setId(waycnt);
                        passList.add(Tway);
                        tmapview.removeAllMarkerItem();
                        final int flg= mp.size()-1;
                        mArrayMarkerID = new ArrayList<>();
                        if(state.equals("등교"))
                        {
                                    ArrayList<TMapPoint> arp = new ArrayList<>();
                                    for (int i = 1; i < mp.size() - 1; i++) {
                                        TMapPoint tmp = new TMapPoint(mp.get(i).latitude, mp.get(i).longitude);
                                        arp.add(tmp);
                                    }
                                    HashMap<String, String> pathInfo = new HashMap<String, String>();
                                    pathInfo.put("rStName", String.valueOf(mp.get(0).description));//비어있다.
                                    pathInfo.put("rStlat", String.valueOf(mp.get(0).latitude));
                                    pathInfo.put("rStlon", String.valueOf(mp.get(0).longitude));
                                    pathInfo.put("rGoName", String.valueOf(mp.get(mp.size() - 1).description));
                                    pathInfo.put("rGolat", String.valueOf(mp.get(mp.size() - 1).latitude));
                                    pathInfo.put("rGolon", String.valueOf(mp.get(mp.size() - 1).longitude));
                                    pathInfo.put("type", "arrival");
                                    tmapdata.findTimeMachineCarPath(pathInfo, currentTime, arp, "00",
                                            new TMapData.FindTimeMachineCarPathListenerCallback() {
                                                @Override
                                                public void onFindTimeMachineCarPath(Document doc) {

                                                }
                                            });

                            mp.remove(0);
                            mp.remove(mp.size()-1);
                            }else if(state.equals("하교")){
                            TPoint st = null;
                            TPoint ed = null;
                            if (mp.size() > 1) {
                            for(int i=0;i<mp.size();i++) {
                                    if (i == 0) {
                                        st = new TPoint(start.getLatitude(), start.getLongitude());
                                        st.description = startText.getText().toString();
                                        ed = mp.get(i);
                                       timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i > 0 && i < (mp.size() - 1)) {
                                        st = mp.get(i - 1);
                                        ed = mp.get(i);
                                       timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i == (mp.size() - 1)) {
                                        st = mp.get(i);
                                        ed = new TPoint(end.getLatitude(), end.getLongitude());
                                        ed.description = endText.getText().toString();
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    }
                                }
                            }
                            else
                            {
                                st = new TPoint(start.getLatitude(), start.getLongitude());
                                st.description = startText.getText().toString();
                                ed = mp.get(0);
                                timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                st=null;
                                ed=null;
                                st = mp.get(0);
                                ed = new TPoint(end.getLatitude(), end.getLongitude());
                                ed.description = endText.getText().toString();
                                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                            }
                        }
                        addLocation.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                final EditText test = (EditText)findViewById(v.getId());
                                final AlertDialog.Builder ratingdialog = new AlertDialog.Builder(CarpoolUploadMain.this);
                                ratingdialog.setTitle("경유지 삭제");
                                ratingdialog.setMessage("해당 경유지를 삭제하시겠습니까?");
                                ratingdialog.setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                addRouteLayout.removeView(test.findViewById(test.getId()));
                                                passList.remove(test.getId());
                                                mp.remove(test.getId());
                                                tmapview.removeAllMarkerItem();
                                                if(state.equals("등교"))
                                                {
                                                    for(int i=0;i<mp.size();i++) {
                                                        TPoint st = null;
                                                        TPoint ed = null;
                                                        if (mp.size() > 0) {
                                                            if (i == 0) {
                                                                st = new TPoint(start.getLatitude(), start.getLongitude());
                                                                st.description = startText.getText().toString();
                                                                ed = mp.get(i);
                                                                timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            } else if (i > 0 && i < mp.size() - 1) {
                                                                st = mp.get(i - 1);
                                                                ed = mp.get(i);
                                                                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            } else if (i == mp.size() - 1) {
                                                                st = mp.get(i);
                                                                ed = new TPoint(end.getLatitude(), end.getLongitude());
                                                                ed.description = endText.getText().toString();
                                                                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            }
                                                        }else
                                                        {
                                                            st = new TPoint(start.getLatitude(), start.getLongitude());
                                                            st.description = startText.getText().toString();
                                                            ed = mp.get(i);
                                                            timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            st = mp.get(i);
                                                            ed = new TPoint(end.getLatitude(), end.getLongitude());
                                                            ed.description = endText.getText().toString();
                                                            timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                        }
                                                    }
                                                }else if(state.equals("하교")){
                                                    TPoint st = null;
                                                    TPoint ed = null;
                                                    if (mp.size() > 1) {
                                                        for(int i=0;i<mp.size();i++) {
                                                            if (i == 0) {
                                                                st = new TPoint(start.getLatitude(), start.getLongitude());
                                                                st.description = startText.getText().toString();
                                                                ed = mp.get(i);
                                                                timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            } else if (i > 0 && i < (mp.size() - 1)) {
                                                                st = mp.get(i - 1);
                                                                ed = mp.get(i);
                                                                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            } else if (i == (mp.size() - 1)) {
                                                                st = mp.get(i);
                                                                ed = new TPoint(end.getLatitude(), end.getLongitude());
                                                                ed.description = endText.getText().toString();
                                                                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        st = new TPoint(start.getLatitude(), start.getLongitude());
                                                        st.description = startText.getText().toString();
                                                        ed = mp.get(0);
                                                        timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                        st=null;
                                                        ed=null;
                                                        st = mp.get(0);
                                                        ed = new TPoint(end.getLatitude(), end.getLongitude());
                                                        ed.description = endText.getText().toString();
                                                       timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                    }
                                                }

                                                tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end, passList, 0,
                                                        new TMapData.FindPathDataListenerCallback() {
                                                            @Override
                                                            public void onFindPathData(TMapPolyLine polyLine) {
                                                                tmapview.addTMapPath(polyLine);
                                                                tmapview.refreshMap();
                                                            }
                                                        });
                                            }
                                        })
                                        .setNegativeButton("취소",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                ratingdialog.create();
                                ratingdialog.show();


                                tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end, passList, 0,
                                        new TMapData.FindPathDataListenerCallback() {
                                            @Override
                                            public void onFindPathData(TMapPolyLine polyLine) {
                                                tmapview.addTMapPath(polyLine);
                                                tmapview.refreshMap();
                                            }
                                        });
                                return false;
                            }
                        });

                        addRouteLayout.addView(addLocation);
                        waycnt++;
                        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end, passList, 0,
                                new TMapData.FindPathDataListenerCallback() {
                                    @Override
                                    public void onFindPathData(TMapPolyLine polyLine) {
                                        tmapview.addTMapPath(polyLine);
                                        tmapview.refreshMap();
                                    }
                                });

                    }
                    break;
                case CARPOOL_ENDING_SUCCESS:
                    if(resultCode==RESULT_OK){
                        tmapdata=new TMapData();
                        TPoint td= (TPoint) data.getSerializableExtra("tpoint");
                        end= new TMapPoint(td.latitude,td.longitude);//받아온 출발지 위도 경도값 주고
                        Tend= new TPoint(td.latitude,td.longitude);
                            tmapview.removeAllMarkerItem();
                        endText.setText(td.description);//이름까지 입력해주고
                        TPoint tt= new TPoint(start.getLatitude(),start.getLongitude());
                        tt.description=startText.getText().toString();
                        Tend.description=td.description;

                        timeSelect(tt,td,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 하교
                        tmapview.zoomToTMapPoint(start, end );
                        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end,
                                new TMapData.FindPathDataListenerCallback() {
                                    @Override
                                    public void onFindPathData(TMapPolyLine polyLine) {
                                        tmapview.addTMapPath(polyLine);
                                        tmapview.refreshMap();
                                    }
                                });
                        tmapview.refreshMap();

                    }
                    break;
                case CARPOOL_AREA_SUCCESS:
                    if(resultCode==RESULT_OK){
                        TPoint tt= (TPoint) data.getSerializableExtra("locationArray");//CARPOOL_AREA_SUCCESS
                        mp= new ArrayList<>(tt.mp);
                        tt= mp.get(0);
                        Tstart=tt;
                        start = new TMapPoint(tt.latitude,tt.longitude);
                        tmapview.zoomToTMapPoint(start, end );
                        tt=mp.get(mp.size()-1);
                        end= new TMapPoint(tt.latitude,tt.longitude);
                        Tend =tt;
                        mp.remove(mp.size()-1);
                        mp.remove(0);

                        if(state.equals("등교"))
                        {
                            TPoint st = null;
                            TPoint ed = null;
                            if (mp.size() > 1) {
                                for(int i=mp.size()-1;i>=0;i--) {
                                    if (i == mp.size()-1) {
                                        st = mp.get(i);
                                        ed= new TPoint(end.getLatitude(),end.getLongitude());
                                        ed.description=endText.getText().toString();
                                        timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i > 0 && i < (mp.size() - 1)) {
                                        ed = mp.get(i-1);
                                        st = mp.get(i);
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i ==0) {
                                        st = new TPoint(start.getLatitude(), start.getLongitude());
                                        st.description=startText.getText().toString();
                                        ed = mp.get(i);
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    }
                                }
                            }
                            else
                            {

                                ed= new TPoint(end.getLatitude(),end.getLongitude());
                                ed.description=endText.getText().toString();
                                st=mp.get(0);
                                timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                st=null;
                                ed=null;
                                st = new TPoint(start.getLatitude(), start.getLongitude());
                                st.description = startText.getText().toString();
                                ed = mp.get(0);
                                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                            }
                        }else if(state.equals("하교")){
                            TPoint st = null;
                            TPoint ed = null;
                            if (mp.size() > 1) {
                                for(int i=0;i<mp.size();i++) {
                                    if (i == 0) {
                                        st = new TPoint(start.getLatitude(), start.getLongitude());
                                        st.description = startText.getText().toString();
                                        ed = mp.get(i);
                                        timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i > 0 && i < (mp.size() - 1)) {
                                        st = mp.get(i - 1);
                                        ed = mp.get(i);
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i == (mp.size() - 1)) {
                                        st = mp.get(i);
                                        ed = new TPoint(end.getLatitude(), end.getLongitude());
                                        ed.description = endText.getText().toString();
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    }
                                }
                            }
                            else
                            {
                                st = new TPoint(start.getLatitude(), start.getLongitude());
                                st.description = startText.getText().toString();
                                ed = mp.get(0);
                                timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                st=null;
                                ed=null;
                                st = mp.get(0);
                                ed = new TPoint(end.getLatitude(), end.getLongitude());
                                ed.description = endText.getText().toString();
                                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                            }
                        }
                    }
                    break;
            }
        }

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
            final RatingBar rating = (RatingBar) linearlayout.findViewById(R.id.ratingbar);

            ratingdialog.setPositiveButton("등록",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int carcnt=0;
                            int waycnt=0;
                            ClientSocket ck = new ClientSocket(null);
                            CARPOOL_PATH_MANAGEMENT cp = new CARPOOL_PATH_MANAGEMENT();
                            cp.setOrderOperation("INSERT");
                            cp.setInsertRequest("INSERT INTO CARPOOL_PATH_MANAGEMENT (CARPOOL_SERIAL_NUMBER,ID,STARTING_POINT_LATITUDE,STARTING_POINT_LONGITUDE,STARTING_POINT_DESCRIPTION,DESTINATION_LATITUDE,DESTINATION_LONGITUDE,DESTINATION_DESCRIPTION,DEPARTURE_TIME,ARRIVAL_TIME,THE_NUMBER_OF_OCCUPANT,CURRENT_DRIVER_LATITUDE,CURRENT_DRIVER_LONGITUDE)" +
                                    "VALUES ('"+WAYPOINT_TOTAL+"','"+ID+"',"+Tstart.latitude+","+Tstart.longitude+",'"+Tstart.description+"',"+Tend.latitude+","+Tend.longitude+",'"+Tend.description+"','"+Tstart.hour+":"+Tstart.minute+":"+Tstart.second+"','"+Tend.hour+":"+Tend.minute+":"+Tend.second+"',"+rating.getRating()+",0.0,0.0);");
                            Log.d("TAG3",""+cp.getInsertRequest());
                            ck.setObj(cp);
                            for(int i=0;i<mp.size();i++)
                            {
                                        TPoint dbtp = mp.get(i);
                                        if(dbtp.flag.equals("경유")){
                                            WAYPOINT wp = new WAYPOINT();
                                            wp.setOrderOperation("INSERT");
                                            wp.setInsertRequest("INSERT INTO WAYPOINT (CARPOOL_SERIAL_NUMBER,WAYPOINT_ORDER,WAYPOINT_LATITUDE,WAYPOINT_LONGITUDE,WAYPOINT_DESCRIPTION,WAYPOINT_TOTALINDEX) VALUES('"
                                                    +WAYPOINT_TOTAL+"',"+waycnt+","+dbtp.latitude+","+dbtp.longitude+",'"+dbtp.description+"',"+i+");");
                                            waycnt++;
                                            Log.d("TAG3",""+wp.getInsertRequest());
                                            ck.setObj(wp);
                                            }else if(dbtp.flag.equals("카풀")){
                                            ACCESS_POINT_MANAGEMENT ap = new ACCESS_POINT_MANAGEMENT();
                                            ap.setOrderOperation("INSERT");
                                            ap.setInsertRequest("INSERT INTO ACCESS_POINT_MANAGEMENT (KAPUL_ACCESS_POINT_SERIAL_NUMBER,CARPOOL_SERIAL_NUMBER,ACCESS_POINT_LATITUDE,ACCESS_POINT_LONGITUDE,ACCESS_POINT_TIME_OF_ARRIVAL,SEARCH_POSSIBILITY,CARPOOL_DESCRIPTION,ACCESS_POINT_TOTALINDEX) VALUES ('"
                                                    +WAYPOINT_TOTAL+"/"+carcnt+"','"+WAYPOINT_TOTAL+"',"+dbtp.latitude+","+dbtp.longitude+",'"+currentTime.getHours()+":"+currentTime.getMinutes()+":"+currentTime.getSeconds()+"',"+1+",'"+dbtp.description+"',"+i+");");
                                            carcnt++;
                                                    Log.d("TAG3",""+ap.getInsertRequest());
                                            ck.setObj(ap);
                                        }
                            }

                            Queue<ObjectTable> outQueue = new LinkedList<ObjectTable>(ck.queue);
                            ck.start();
                            while(ck.flag){

                            }
                            Intent outit = new Intent();
                            outit.putExtra("COMMUTING_TIME",CMT);//시간 넣고
                            outit.putExtra("day",day);//요일 넣고
                            outit.putExtra("state",state);//상태 넣고
                            outit.putExtra("ID",ID);//아이디넣고
                            setResult(RESULT_OK, outit);
                            finish();
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


    public void timeSelect(final TPoint start, final TPoint end, final Date date)
    {

        Log.d("TIMESELECT","출"+start.description);
        Log.d("TIMESELECT","출"+start.latitude);
        Log.d("TIMESELECT","출"+start.longitude);
        Log.d("TIMESELECT","목"+end.description);
        Log.d("TIMESELECT","목"+end.latitude);
        Log.d("TIMESELECT","목"+end.longitude);
        Log.d("TIMESELECT","도착시간"+date.toString());
        if(state.equals("등교")){
            HashMap<String, String> pathInfo = new HashMap<String, String>();
            pathInfo.put("rStName", start.description);//비어있다.
            pathInfo.put("rStlat", String.valueOf(start.latitude));
            pathInfo.put("rStlon", String.valueOf(start.longitude));
            pathInfo.put("rGoName",end.description);
            pathInfo.put("rGolat", String.valueOf(end.latitude));
            pathInfo.put("rGolon", String.valueOf(end.longitude));
            pathInfo.put("type", "departure");
            tmapdata.findTimeMachineCarPath(pathInfo,  currentTime, null, "00",
            new TMapData.FindTimeMachineCarPathListenerCallback() {
                @Override
                public void onFindTimeMachineCarPath(Document doc){
                    NodeList ns = doc.getElementsByTagName("tmap:totalTime");//출발 예상 시간이 출력됨
                    Node node = ns.item(0);
                    int totalTime = Integer.parseInt(node.getTextContent());
                    Log.d("totalTime",""+totalTime);//총 소요시간

                    int hourss = date.getHours();//출발시간
                    int minutesss = date.getMinutes();
                    int secondss = date.getSeconds();
                    hourss= hourss*3600;
                    minutesss=minutesss*60;
                    int startTime = hourss+minutesss+secondss;


                    hour = (startTime+totalTime)/ 3600;
                    minute=(startTime+totalTime) % 3600 / 60;
                    second=(startTime+totalTime) % 3600 % 60;


                    addMarker(start,""+hour,""+minute,""+second);//start에 출발예
                }
            });
        }
        else if(state.equals("하교"))
        {
            HashMap<String, String> pathInfo = new HashMap<String, String>();
            pathInfo.put("rStName", start.description);//비어있다.
            pathInfo.put("rStlat", String.valueOf(start.latitude));
            pathInfo.put("rStlon", String.valueOf(start.longitude));
            pathInfo.put("rGoName",end.description);//,
            pathInfo.put("rGolat", String.valueOf(end.latitude));
            pathInfo.put("rGolon", String.valueOf(end.longitude));
            pathInfo.put("type", "arrival");
            tmapdata.findTimeMachineCarPath(pathInfo,  date, null,"00",
                    new TMapData.FindTimeMachineCarPathListenerCallback() {
                        @Override
                        public void onFindTimeMachineCarPath(Document doc) {
                            NodeList ns = doc.getElementsByTagName("tmap:totalTime");
                            Node node = ns.item(0);
                            int totalTime = Integer.valueOf(node.getTextContent());
                            int hour = date.getHours();//출발시간
                            int minutes = date.getMinutes();
                            int second = date.getSeconds();
                            start.hour=String.valueOf(hour);
                            start.minute=String.valueOf(minutes);
                            start.second=String.valueOf(second);
                            addMarker(start,String.valueOf(hour),String.valueOf(minutes),String.valueOf(second));
                            hour= hour*3600;
                            minutes=minutes*60;
                            int startTime = hour+minutes+second;
                            tempcurrentTime1.setHours((startTime+totalTime)/ 3600);
                            tempcurrentTime1.setMinutes((startTime+totalTime) % 3600 / 60);
                            tempcurrentTime1.setSeconds((startTime+totalTime) % 3600 % 60);


                            String strHour=String.valueOf((startTime+totalTime)/ 3600);//도착시간
                            String strMin= String.valueOf((startTime+totalTime) % 3600 / 60);
                            String strSec= String.valueOf((startTime+totalTime) % 3600 % 60);
                            end.hour=strHour;
                            end.minute=strMin;
                            end.second=strSec;
                            addMarker(end,strHour,strMin,strSec);

                        }
                    });
        }
    }
    public void addMarker(TPoint tp,String hour, String minutes, String seconds) {//지도에 마커 추가
        TMapPoint poi = new TMapPoint(tp.latitude, tp.longitude);
        TMapMarkerItem APmarker = new TMapMarkerItem();
        Bitmap bitmap = null;
        mContext = this;

        String nowTime = hour + "시 " + minutes + "분 ";

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.add_marker);
        //add_marker는 지도에 추가할 마커 이미지입니다.
        APmarker.setTMapPoint(poi);
        APmarker.setName("테스트");
        APmarker.setVisible(APmarker.VISIBLE);
        APmarker.setIcon(bitmap);
        APmarker.setID(tp.description);
        APmarker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        APmarker.setCalloutTitle(tp.description);
        APmarker.setCalloutSubTitle(nowTime);       //풍선뷰 보조메세지
//      tItem.setCalloutLeftImage(bitmap);  //풍선뷰의 왼쪽 이미지 지정 //오른쪽은 RIGHT

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.car);
        APmarker.setCalloutRightButtonImage(bitmap);

        String strID = tp.description;
        tmapview.addMarkerItem(strID, APmarker);
        mArrayMarkerID.add(strID);
        //풍선뷰 선택할 때 나타나는 이벤트
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

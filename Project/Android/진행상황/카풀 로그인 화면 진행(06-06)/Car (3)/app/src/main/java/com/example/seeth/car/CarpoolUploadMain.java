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
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CarpoolUploadMain extends AppCompatActivity implements  View.OnClickListener {

    //데이터베이스 사용을 위한 클래스 선언
    USER user;
    ACCESS_POINT_MANAGEMENT access_point_management;
    WAYPOINT waypoint;
    CARPOOL_PATH_MANAGEMENT carpool_path_management;
    COMMUTING_TIME commuting_time;

    int mMarkerID=0;    //마커를 구별하기 위한 ID
    int carpoolJoinCount=0;     //AP지점을 최대 3개까지 카운트하기위한 변수

    private Context mContext;

    private ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();     //경유지 정보를 받아오는 어레이리스트
    private String name=null;   //출발지나 경유지에서 전달받은 위치의 이름을 저장
    private double lat;
    private double lon;
    ArrayList<TMapPoint> totalRoot =null;
    //마커의 위도경도를 저장하는 배열
    private double[] makerLat = new double[5];
    private double[] makerLon = new double[5];
    ArrayList<String> mArrayMarkerID = new ArrayList<String>(); //마커의 중복을 허용하기 위한 마커의 id

    private TMapView tmapview;
    private TMapData tmapdata;

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

    Bundle bundle;

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
    RelativeLayout relativelayout;
    Date currentTime = new Date();
    Date tempcurrentTime1 = new Date();
    Date tempcurrentTime2 = new Date();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carpool_upload_main);//XML 레이아웃에 정의된 뷰들을 메모리상에 객체화 하는 역할을 수행하는 메소드
        cp=this;
          relativelayout = (RelativeLayout) findViewById(R.id.uploadMainTmap);

        //==============여기는 시간표가 들어오게 될 경우 시간표 활성화 비활성화 처리할 로직
        carpoolArea = (Button)findViewById(R.id.carpoolLocation);
        bundle = getIntent().getBundleExtra("bundle");
        commuting_time = (COMMUTING_TIME)bundle.getSerializable("time");    //메인에서 전달한 시간표 받음
        if( commuting_time.getID()==null && commuting_time.getDAY()==null ) {
            //시간표 유도 화면
        }
        //=================================================================================
        mp=new ArrayList<>();
        totalRoot=new ArrayList<>();
        goHome=(Button)findViewById(R.id.goHome);
        goSchool=(Button)findViewById(R.id.goSchool);
        goHome.setOnClickListener(this);
        goSchool.setOnClickListener(this);
        carpool_path_management = new CARPOOL_PATH_MANAGEMENT();
        startText = (EditText)findViewById(R.id.sourceSearch);
        startText.setSelected(false);
        endText = (EditText)findViewById(R.id.destinationLocition);
        endText.setSelected(false);
        currentTime.setHours(9);
        currentTime.setMinutes(00);
        currentTime.setSeconds(00);
     //   goSchool.setOnClickListener(this);
       // goHome.setOnClickListener(this);
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


    //시간표 비교 다이얼로그 생성
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

    //등교or 하교 / 카풀등록or카풀정보 버튼 이벤트
    @Override
    public void onClick(View v) {
        school_home_flag=0;
        int id = v.getId();
        //등교를 선택시 스쿨홈플래그에 1을 전달, 하교를 선택할시 2를 전달
        switch (id) {
            //등교일때는 엔드에 YJpoint, 하교일때는 start에 YJpoint
            case R.id.goSchool:
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

                v.setBackgroundColor(Color.parseColor("#039BE5"));
                goHome.setBackgroundColor(Color.parseColor("#ffffff"));
                state="등교";
                endText.setText("영진전문대학");
                Tend.name="영진전문대학";
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
               // school_home_flag = school_flag;
              //  Toast.makeText(this, "등교를 선택했습니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.goHome:
                v.setBackgroundColor(Color.parseColor("#039BE5"));
                goSchool.setBackgroundColor(Color.parseColor("#ffffff"));
                state="하교";
                endText.setText(null);
                endText.setSelected(true);
                mp=null;
                mp= new ArrayList<TPoint>();
                start=null;
                start=new TMapPoint(35.89601207015857,128.62196445465088);
                Tstart =new TPoint(35.89601207015857,128.62196445465088);
                Tstart.name="영진전문대학";
                Tend=null;
                endText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toEnd = new Intent(CarpoolUploadMain.this,CarpoolLocationSearch.class);
                        TPoint tpoint = new TPoint(start.getLatitude(),start.getLongitude());
                        tpoint.flag="하교지";
                        toEnd .putExtra("tpoint",tpoint);
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
                //school_home_flag = home_flag;
               // Toast.makeText(this, "하교를 선택했습니다.", Toast.LENGTH_SHORT).show();
                break;
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
                        startText.setText(td.name);//이름까지 입력해주고
                        Tend= new TPoint(end.getLatitude(),end.getLongitude());
                        Tend.name=endText.getText().toString();
                        Tstart.name=td.name;


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
                        addLocation.setText(""+tt.name);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.CENTER;
                        addLocation.setLayoutParams(lp);
                        tmapview.zoomToTMapPoint(start, end );
                        addLocation.setId(waycnt);
                        passList.add(Tway);
                        tmapview.removeAllMarkerItem();
                        final int flg= mp.size()-1;
                        if(state.equals("등교"))
                        {
                            TPoint st = null;
                            TPoint ed = null;
                            if (mp.size() > 1) {
                                for(int i=mp.size()-1;i>=0;i--) {
                                    if (i == mp.size()-1) {
                                        st = mp.get(i);
                                        ed= new TPoint(end.getLatitude(),end.getLongitude());
                                        ed.name=endText.getText().toString();
                                        timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i > 0 && i < (mp.size() - 1)) {
                                        ed = mp.get(mp.size()-i);
                                        st = mp.get(i);
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i ==0) {
                                        st = new TPoint(start.getLatitude(), start.getLongitude());
                                        st.name=startText.getText().toString();

                                        ed = mp.get(i);
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    }
                                }
                            }
                            else
                            {

                                ed= new TPoint(end.getLatitude(),end.getLongitude());
                                ed.name=endText.getText().toString();
                                st=mp.get(0);
                                timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                st=null;
                                ed=null;
                                st = new TPoint(start.getLatitude(), start.getLongitude());
                                st.name = startText.getText().toString();
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
                                        st.name = startText.getText().toString();
                                        ed = mp.get(i);
                                        timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i > 0 && i < (mp.size() - 1)) {
                                        st = mp.get(i - 1);
                                        ed = mp.get(i);
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i == (mp.size() - 1)) {
                                        st = mp.get(i);
                                        ed = new TPoint(end.getLatitude(), end.getLongitude());
                                        ed.name = endText.getText().toString();
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    }
                                }
                            }
                            else
                            {
                                st = new TPoint(start.getLatitude(), start.getLongitude());
                                st.name = startText.getText().toString();
                                ed = mp.get(0);
                                timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                st=null;
                                ed=null;
                                st = mp.get(0);
                                ed = new TPoint(end.getLatitude(), end.getLongitude());
                                ed.name = endText.getText().toString();
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
                                                                st.name = startText.getText().toString();
                                                                ed = mp.get(i);
                                                                timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            } else if (i > 0 && i < mp.size() - 1) {
                                                                st = mp.get(i - 1);
                                                                ed = mp.get(i);
                                                                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            } else if (i == mp.size() - 1) {
                                                                st = mp.get(i);
                                                                ed = new TPoint(end.getLatitude(), end.getLongitude());
                                                                ed.name = endText.getText().toString();
                                                                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            }
                                                        }else
                                                        {
                                                            st = new TPoint(start.getLatitude(), start.getLongitude());
                                                            st.name = startText.getText().toString();
                                                            ed = mp.get(i);
                                                            timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            st = mp.get(i);
                                                            ed = new TPoint(end.getLatitude(), end.getLongitude());
                                                            ed.name = endText.getText().toString();
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
                                                                st.name = startText.getText().toString();
                                                                ed = mp.get(i);
                                                                timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            } else if (i > 0 && i < (mp.size() - 1)) {
                                                                st = mp.get(i - 1);
                                                                ed = mp.get(i);
                                                                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            } else if (i == (mp.size() - 1)) {
                                                                st = mp.get(i);
                                                                ed = new TPoint(end.getLatitude(), end.getLongitude());
                                                                ed.name = endText.getText().toString();
                                                                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        st = new TPoint(start.getLatitude(), start.getLongitude());
                                                        st.name = startText.getText().toString();
                                                        ed = mp.get(0);
                                                        timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                                        st=null;
                                                        ed=null;
                                                        st = mp.get(0);
                                                        ed = new TPoint(end.getLatitude(), end.getLongitude());
                                                        ed.name = endText.getText().toString();
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


                                tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end, passList, 2,
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
                        endText.setText(td.name);//이름까지 입력해주고
                        TPoint tt= new TPoint(start.getLatitude(),start.getLongitude());
                        tt.name=startText.getText().toString();
                        Tend.name=td.name;
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
                                        ed.name=endText.getText().toString();
                                        timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i > 0 && i < (mp.size() - 1)) {
                                        ed = mp.get(mp.size()-i);
                                        st = mp.get(i);
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i ==0) {
                                        st = new TPoint(start.getLatitude(), start.getLongitude());
                                        st.name=startText.getText().toString();

                                        ed = mp.get(i);
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    }
                                }
                            }
                            else
                            {

                                ed= new TPoint(end.getLatitude(),end.getLongitude());
                                ed.name=endText.getText().toString();
                                st=mp.get(0);
                                timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                st=null;
                                ed=null;
                                st = new TPoint(start.getLatitude(), start.getLongitude());
                                st.name = startText.getText().toString();
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
                                        st.name = startText.getText().toString();
                                        ed = mp.get(i);
                                        timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i > 0 && i < (mp.size() - 1)) {
                                        st = mp.get(i - 1);
                                        ed = mp.get(i);
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    } else if (i == (mp.size() - 1)) {
                                        st = mp.get(i);
                                        ed = new TPoint(end.getLatitude(), end.getLongitude());
                                        ed.name = endText.getText().toString();
                                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                    }
                                }
                            }
                            else
                            {
                                st = new TPoint(start.getLatitude(), start.getLongitude());
                                st.name = startText.getText().toString();
                                ed = mp.get(0);
                                timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                                st=null;
                                ed=null;
                                st = mp.get(0);
                                ed = new TPoint(end.getLatitude(), end.getLongitude());
                                ed.name = endText.getText().toString();
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
                            Log.d("TAG3",""+rating.getNumStars());
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
        if(state.equals("등교")){
            HashMap<String, String> pathInfo = new HashMap<String, String>();
            pathInfo.put("rStName", start.name);//비어있다.
            pathInfo.put("rStlat", String.valueOf(start.latitude));
            pathInfo.put("rStlon", String.valueOf(start.longitude));
            pathInfo.put("rGoName",end.name);
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

                            addMarker(end,String.valueOf(hour),String.valueOf(minutes),String.valueOf(second));

                            hour= hour*3600;
                            minutes=minutes*60;
                            int startTime = hour+minutes+second;
                            tempcurrentTime1.setHours((startTime-totalTime)/ 3600);
                            tempcurrentTime1.setMinutes((startTime-totalTime) % 3600 / 60);
                            tempcurrentTime1.setSeconds((startTime-totalTime) % 3600 % 60);


                            String strHour=String.valueOf((startTime-totalTime)/ 3600);//도착시간
                            String strMin= String.valueOf((startTime-totalTime) % 3600 / 60);
                            String strSec= String.valueOf((startTime-totalTime) % 3600 % 60);

                            addMarker(start,strHour,strMin,strSec);


                        }
                    });
        }
        else if(state.equals("하교"))
        {
            HashMap<String, String> pathInfo = new HashMap<String, String>();
            pathInfo.put("rStName", start.name);//비어있다.
            pathInfo.put("rStlat", String.valueOf(start.latitude));
            pathInfo.put("rStlon", String.valueOf(start.longitude));
            pathInfo.put("rGoName",end.name);//,
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

        String nowTime = hour + "시 " + minutes + "분 " + seconds + "초";

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.add_marker);
        //add_marker는 지도에 추가할 마커 이미지입니다.
        APmarker.setTMapPoint(poi);
        APmarker.setName("테스트");
        APmarker.setVisible(APmarker.VISIBLE);
        APmarker.setIcon(bitmap);
        APmarker.setID(tp.name);
        APmarker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        APmarker.setCalloutTitle(tp.name);
        APmarker.setCalloutSubTitle(nowTime);       //풍선뷰 보조메세지
//      tItem.setCalloutLeftImage(bitmap);  //풍선뷰의 왼쪽 이미지 지정 //오른쪽은 RIGHT

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.car);
        APmarker.setCalloutRightButtonImage(bitmap);

        String strID = tp.name;
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

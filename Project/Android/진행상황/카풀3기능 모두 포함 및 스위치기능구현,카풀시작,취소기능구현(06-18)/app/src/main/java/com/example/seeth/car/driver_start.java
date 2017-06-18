package com.example.seeth.car;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapTapi;
import com.skp.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class driver_start extends AppCompatActivity
        implements TMapGpsManager.onLocationChangedCallback {

    private TMapView tmapview;
    private String mapKey = "b9c365d5-6d93-385f-bf50-0b423ece22c1";
    private TMapData tmapdata = new TMapData(); //POI검색, 경로검색 등의 지도데이터를 관리하는 클래스
    private Context mContext = null;
    /* 경로 이름을 저장하는 필드 */
    private String routeName = null;

    private String address;
    private Double lat = null;
    private Double lon = null;

    /**  마커의 위치정보를 저장하는 배열(DB연동) **/
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>();

    //마커의 중복을 허용하기 위한 마커의 id
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();
    private static int mMarkerID;
    CARPOOL_PATH_MANAGEMENT CPM ;
    private static final int FONT_SIZE = 25;
    /*
   현구 수정
     */
    USER user = null;
    Date date;
    ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();
    ArrayList<TPoint> Atp =new ArrayList<>();
    ArrayList<WAYPOINT> Awp = new ArrayList<>();
    ArrayList<CARPOOL_PATH_MANAGEMENT> Acp = new ArrayList<>();
    ArrayList<ACCESS_POINT_MANAGEMENT> Aap = new ArrayList<>();
    ArrayList<BOARDING_PASS> Abp =new ArrayList<>();
    ArrayList<ImageView> personArray =new ArrayList<>();
    ArrayList<SeekBar> skb = new ArrayList<>();
    ArrayList<String> DistanceArray = new ArrayList<>();
    ClientSocket sk;
    TMapPoint Start;
    TMapPoint End;
    int currentIndex;
    String currentTimeText;
    TextView tvp;
    double current_latitude=0.0;
    double current_longitude=0.0;
    TimeHandler tm ;
    int currentSkbar;
    SeekHandler skm;
    String totalDistance;
    String state;
    TextView Totaltv;
    String totalcurrentTimeText;
    Date currentTime1 = new Date();
    Date currentTime2 = new Date();
    int startTime=0;
    int tempTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //가로
        setContentView(R.layout.driver_start);
        sk=new ClientSocket(this);
        currentIndex=0;
        date=new Date();
        //17-06-12 작업 인탠트에서 값 넘겨받기
        Intent it = this.getIntent();
        Awp = (ArrayList<WAYPOINT>) it.getSerializableExtra("Awp");// WAYPOINT 담김
        Acp= (ArrayList<CARPOOL_PATH_MANAGEMENT>) it.getSerializableExtra("Acp");//CARPOOL_PATH_MANAGEMENT;
        Aap= (ArrayList<ACCESS_POINT_MANAGEMENT>) it.getSerializableExtra("Aap");//카풀 지점이 담김
        Atp= (ArrayList<TPoint>) it.getSerializableExtra("Atp");//정렬된 TPoint가 담김
        user= (USER) it.getSerializableExtra("user");

        //====================인탠트 값 넘겨받기 종료
        //====================동적인 인텐트 뷰 생성 시작

        //=====================동적인 이미지 뷰 생성 종료
        //=====================카풀 경로 출력 시작
        LinearLayout ratingLayout = (LinearLayout) findViewById(R.id.seekLayout);
        LinearLayout AtpLayout= (LinearLayout) findViewById(R.id.AtpLayout);
        LinearLayout personLayout = (LinearLayout) findViewById(R.id.personImgLay);

        tvp= (TextView) findViewById(R.id.driver_currentTime);
        tm=new TimeHandler();
        skm= new SeekHandler();



        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        final int width2 = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
        final int height2 = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(width,height);
        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(width2,height2);
        //lp3.leftMargin=300;이거는 텍스트 쪽으로 붙인다.
        lp3.topMargin=30;
        lp4.bottomMargin=60;
        lp4.topMargin=20;

        for(int i=0;i<Atp.size()-1;i++){
            TPoint t1 = new TPoint(Atp.get(i).latitude,Atp.get(i).longitude);
            TPoint t2 = new TPoint(Atp.get(i+1).latitude,Atp.get(i+1).longitude);
            distanceMaxCount(t1,t2);

        }
        for(int i=0;i<Atp.size()-1;i++)
        {
            SeekBar skbar = new SeekBar(this);
            skbar.setLayoutParams(lp3);
            skbar.setRotation(90);
            skbar.setVisibility(View.VISIBLE);
            skbar.setMax(100);
            skbar.setId(i);
            skb.add(skbar);
            ratingLayout.addView(skbar);
        }
        for(int i=0;i<Atp.size();i++){
            TextView tvp = new TextView(this);
            tvp.setText(""+Atp.get(i).description);
            tvp.setTextSize(15);
            tvp.setLayoutParams(lp3);
            AtpLayout.addView(tvp);
        }
        for(int i=0;i<Atp.size();i++){
            if(Atp.get(i).flag.equals("카풀")){
                ImageView iv = new ImageView(this);
                iv.setBackgroundResource(R.drawable.car_full);
                iv.setLayoutParams(lp4);
                personArray.add(iv);
                personLayout.addView(iv);
            }else{
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                iv.setBackgroundResource(R.drawable.car_empty);
                iv.setLayoutParams(lp4);
                personArray.add(iv);
                personLayout.addView(iv);
            }
        }

        //=====================카풀 경로 출력 종료

        LinearLayout person = (LinearLayout) findViewById(R.id.person_count);



        /* 릴레이티브 레이아웃에 지도를 출력 */
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.Tmap);
        // 텍스트뷰를 동적으로 생성하기 위한 레이아웃


        tmapview = new TMapView(this);
        tmapview.setTrackingMode(true);
        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        TMapGpsManager gps = new TMapGpsManager(this);
        gps.setMinTime(5000);
        gps.setMinDistance(5);
        gps.setProvider(gps.NETWORK_PROVIDER);
        gps.OpenGps();
        layout.addView(tmapview);

        Start = new TMapPoint(Atp.get(0).latitude, Atp.get(0).longitude);
        End = new TMapPoint(Atp.get(Atp.size() - 1).latitude, Atp.get(Atp.size() - 1).longitude);

        for(int i=1;i<Atp.size()-1;i++)
        {
            Log.d("TIC",""+Atp.get(i).latitude);
            TMapPoint tempTmap = new TMapPoint(Atp.get(i).latitude,Atp.get(i).longitude);
            passList.add(tempTmap);
        }
        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, Start, End,passList,0,
                new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        tmapview.addTMapPath(polyLine);
                        tmapview.refreshMap();
                    }
                });

        tmapview.zoomToTMapPoint(Start,End);
    }
    public void naviButtonOnClick(View view){
        TMapTapi tmaptapi = new TMapTapi(this);

        HashMap<String, String> pathInfo = new HashMap<String, String>();
        pathInfo.put("rGoName",""+Atp.get(currentIndex).description);
        pathInfo.put("rGoY",String.valueOf(Atp.get(currentIndex).latitude) );
        pathInfo.put("rGoX", String.valueOf(Atp.get(currentIndex).longitude));
        pathInfo.put("rStName","현재위치");
        pathInfo.put("rStY", String.valueOf(current_latitude));
        pathInfo.put("rStX", String.valueOf(current_longitude));
        tmaptapi.invokeRoute(pathInfo);//rV1X(옵션) 경도 long

    }
    @Override
    public void onLocationChange(Location location) {
        current_latitude=location.getLatitude();
        current_longitude=location.getLongitude();
        final Thread currentTimeThread = new Thread(new Runnable(){
            public void run() {
                date = new Date();
                HashMap<String, String> pathInfo = new HashMap<String, String>();
                pathInfo.put("rStName", "여기");//비어있다.
                pathInfo.put("rStlat", String.valueOf(current_latitude));
                pathInfo.put("rStlon", String.valueOf(current_longitude));
                pathInfo.put("rGoName", "여기기");
                pathInfo.put("rGolat", String.valueOf(Atp.get(currentIndex).latitude));
                pathInfo.put("rGolon", String.valueOf(Atp.get(currentIndex).longitude));
                pathInfo.put("type", "arrival");
                tmapdata.findTimeMachineCarPath(pathInfo, date, null, "00",
                        new TMapData.FindTimeMachineCarPathListenerCallback() {
                            @Override
                            public void onFindTimeMachineCarPath(Document doc) {
                                NodeList ns = doc.getElementsByTagName("tmap:totalTime");//현재위치에서 도착하는  시간이 산출?
                                Node node = ns.item(0);
                                int totalTime = Integer.valueOf(node.getTextContent());//예상도착시간


                                String strHour = String.valueOf((totalTime) / 3600);//도착시간
                                String strMin = String.valueOf((totalTime) % 3600 / 60);
                                String strSec = String.valueOf((totalTime) % 3600 % 60);
                                if(((int)(totalTime)/3600)==0) {
                                    currentTimeText = "다음 지점 도착" + strMin + "분 전";
                                    Message msg1 = tm.obtainMessage();
                                    tm.sendMessage(msg1);
                                }
                                else{
                                    currentTimeText="다음 지점 도착"+strHour+"시"+strMin+"분 전";
                                    Message msg2 = tm.obtainMessage();
                                    tm.sendMessage(msg2);
                                }

                            }
                        });
            }
        });
        currentTimeThread.start();
        Thread seekThread = new Thread(new Runnable(){
            public void run() {
                HashMap<String, String> pathInfo = new HashMap<String, String>();
                pathInfo.put("rStName", "여기");//비어있다.
                pathInfo.put("rStlat", String.valueOf(current_latitude));
                pathInfo.put("rStlon", String.valueOf(current_longitude));
                pathInfo.put("rGoName", "여기기");
                pathInfo.put("rGolat", String.valueOf(Atp.get(currentIndex).latitude));
                pathInfo.put("rGolon", String.valueOf(Atp.get(currentIndex).longitude));
                pathInfo.put("type", "arrival");
                tmapdata.findTimeMachineCarPath(pathInfo, date, null, "00",
                        new TMapData.FindTimeMachineCarPathListenerCallback() {
                            @Override
                            public void onFindTimeMachineCarPath(Document doc) {
                                NodeList ns = doc.getElementsByTagName("tmap:totalDistance"); //총 거리가 반환되잖아 (드라이버현재위치~목적지까지 총거리)
                                Node node = ns.item(0);
                                int tempTotal = Integer.parseInt(node.getTextContent());//드라이버현재위치~목적지까지
                                int tempTotal2 = Integer.parseInt(DistanceArray.get(currentIndex));//총 거리
                                Log.d("seekBar1",""+tempTotal);
                                Log.d("seekBar2",""+tempTotal2);
                                currentSkbar= (tempTotal2-tempTotal)/tempTotal2*100;
                                Log.d("seekBar3",""+currentSkbar);
                                Message msg1 = skm.obtainMessage();
                                skm.sendMessage(msg1);

                            }
                        });
            }

        });

        seekThread.start();
        //==============================시간계산과 핸들러 처리 끝
        tmapview.setLocationPoint(location.getLongitude(),location.getLatitude()); //현재 내 위치
        tmapview.setCenterPoint(location.getLongitude(),location.getLatitude());   //화면 시작지점
        CARPOOL_PATH_MANAGEMENT CPM  = new CARPOOL_PATH_MANAGEMENT();
        CPM.setOrderOperation("UPDATE");
        CPM.setUpdateRequest("UPDATE CARPOOL_PATH_MANAGEMNET set CURRENT_DRIVER_LATITUDE ="+location.getLatitude()+", CURRENT_DRIVER_LONGITUDE ="+location.getLongitude()+"where CARPOOL_SERIAL_NUMBER = '"+Acp.get(0).getCARPOOL_SERIAL_NUMBER()+"';");
        BOARDING_PASS BP= new BOARDING_PASS();
        BP.setOrderOperation("SELECT");
        BP.setSelectRequest("select * from BOARDING_PASS where CARPOOL_SERIAL_NUMBER LIKE '"+Acp.get(0).getCARPOOL_SERIAL_NUMBER()+"';");
        ClientSocket csk = new ClientSocket(null);
        csk.setObj(BP);
        csk.setObj(CPM);
        csk.start();
    }
    public void distanceMaxCount(TPoint t1,TPoint t2){
        HashMap<String, String> pathInfo = new HashMap<String, String>();
        pathInfo.put("rStName", "여기");//비어있다.
        pathInfo.put("rStlat", String.valueOf(t1.latitude));
        pathInfo.put("rStlon", String.valueOf(t1.longitude));
        pathInfo.put("rGoName", "여기기");
        pathInfo.put("rGolat", String.valueOf(t2.latitude));
        pathInfo.put("rGolon", String.valueOf(t2.longitude));
        pathInfo.put("type", "arrival");
        tmapdata.findTimeMachineCarPath(pathInfo, date, null, "00",
                new TMapData.FindTimeMachineCarPathListenerCallback() {
                    @Override
                    public void onFindTimeMachineCarPath(Document doc) {
                        NodeList ns = doc.getElementsByTagName("tmap:totalDistance");//t1~t2까지 총거리
                        Node node = ns.item(0);

                        DistanceArray.add(""+node.getTextContent());

                    }
                });
    }
    public class SeekHandler extends Handler {//핸들러 제어
        public void handleMessage(Message msg){
            int seekindex = currentIndex;
            SeekBar skbar= skb.get(seekindex);
            skbar.incrementProgressBy(currentSkbar);
            if(skbar.getMax()-skbar.getProgress()<=5)//확률적으로 같다면
            {
                if(currentIndex-Atp.size()-1==0) {//현재 인덱스가 size가 같다는 것이면
                    final AlertDialog.Builder ratingdialog = new AlertDialog.Builder(driver_start.this);

                    ratingdialog.setIcon(getResources().getDrawable(R.drawable.car_full));
                    ratingdialog.setTitle("카풀 종료");
                    ratingdialog.setMessage("카풀이 완료되었습니다. 다음 주도 동일한 경로의 카풀을 진행하시겠습니까?");


                    ratingdialog.setPositiveButton("등록",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent outit = new Intent();
                                    setResult(RESULT_OK, outit);
                                    finish();
                                }
                            })
                            .setNegativeButton("수정",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    ratingdialog.create();
                    ratingdialog.show();
                }else{
                    currentIndex++;
                }
            }
        }
    }
    public class TimeHandler extends Handler {//부분 시간 핸들러 제어 tvp
        public void handleMessage(Message msg){
            tvp.setText(currentTimeText);

        }
    }
}

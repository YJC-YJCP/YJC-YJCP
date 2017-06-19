package com.example.seeth.car;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import static com.example.seeth.car.R.drawable.start;

public class driver_start extends AppCompatActivity
        implements TMapGpsManager.onLocationChangedCallback {

    private TMapView tmapview;
    private String mapKey = "c2b1ca96-9e28-3cc7-8697-092c86b91e5d";
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
    String result;
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
    Intent i;
    SpeechRecognizer mRecognizer;

    String totalDistance;
    String state;
    TextView Totaltv;
    String totalcurrentTimeText;
    Date currentTime1 = new Date();
    Date currentTime2 = new Date();
    int startTime=0;
    int tempTime=0;
    LayoutInflater inflater;
    TextView micText;
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
        micText= (TextView) findViewById(R.id.micText);

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(driver_start.this);
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(i);
        //====================인탠트 값 넘겨받기 종료
        //====================동적인 인텐트 뷰 생성 시작

        //=====================동적인 이미지 뷰 생성 종료
        //=====================카풀 경로 출력 시작
        RelativeLayout infoLayout = (RelativeLayout)findViewById(R.id.infoLayout);
        inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        tvp= (TextView) findViewById(R.id.driver_currentTime);

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
        VoiceHandler vm = new VoiceHandler();

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
        TPoint tp = Atp.get(0);
        if(tp.flag.equals("출발")){
            inflater.inflate(R.layout.way_activity,infoLayout,true);
            ImageView img = (ImageView) findViewById(R.id.desImg);
            img.setImageResource(R.drawable.start);
            TextView textView= (TextView) findViewById(R.id.description);
            textView.setText(tp.description);
            TextView textView2 = (TextView) findViewById(R.id.wayTime);

        }

    }
    public void inflaterChange(int i,String hour,String minute,String description){
        if(i==0){//출발

        }else if(i==1){//경유

        }else if(i==2){//카풀

        }else if(i==3){//도착

        }

    }
    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {

            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            Log.d("TAG320",""+rs[0]);
            result= rs[0];
            mRecognizer.startListening(i);

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };
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
    public class TimeHandler extends Handler {//부분 시간 핸들러 제어 tvp
        public void handleMessage(Message msg){
            tvp.setText(currentTimeText);

        }
    }
    public class VoiceHandler extends Handler {//부분 시간 핸들러 제어 tvp
        public void handleMessage(Message msg){
            micText.setText("" + result);

        }
    }
}

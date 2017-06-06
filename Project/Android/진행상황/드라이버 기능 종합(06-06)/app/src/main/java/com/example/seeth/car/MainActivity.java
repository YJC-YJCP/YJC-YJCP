package com.example.seeth.car;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skp.Tmap.TMapAddressInfo;
import com.skp.Tmap.TMapData;
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
import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import javax.xml.parsers.ParserConfigurationException;

/*
            CARPOOL_PATH_MANAGEMENT에서 출발지랑 목적지 빼내고
            ~ 출발지위도,경도

            ~ 경로 그리기 용도
               Tpoint ArrayList 정렬 우선하고,
               passList에 TPoint내용  복사()

            ~ 시간계산용도
               Tpoint ArrayList에 0값이랑, ArrayList.get(mp.size()-1)에  TPoint 삽입

                */
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
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>(); //마커의 중복을 허용하기 위한 마커의 id
    private static int mMarkerID;
    COMMUTING_TIME CMTT = null;
//현구추가======================
    ArrayList<TPoint> mp=null;
    Date tempcurrentTime1 =new Date();
    Queue<ObjectTable> time_queue2=null;
    COMMUTING_TIME mon_time= null;
    COMMUTING_TIME thus_time=null;
    COMMUTING_TIME wedn_time=null;
    COMMUTING_TIME thurs_time=null;
    COMMUTING_TIME fri_time=null;
    LinkedList<ObjectTable> time=null;
    USER user=null;
    String state;
    RelativeLayout relativeLayout;
    LayoutInflater inflater;
    ArrayList<WAYPOINT> Awp = new ArrayList<>();
    ArrayList<CARPOOL_PATH_MANAGEMENT> Acp = new ArrayList<>();
    ArrayList<ACCESS_POINT_MANAGEMENT> Aap = new ArrayList<>();
    ArrayList<BOARDING_PASS> Abp =new ArrayList<>();
    Queue<ObjectTable> queue2;
    Button goHome;
    Button goSchool;
    String day;
    TMapPoint start;
    TMapPoint end;
    TPoint Tstart;
    TPoint Tend;
    ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();
    ArrayList<TPoint> Atp =new ArrayList<>();
    int cnt=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //=============이전 화면에서 받아 올 것들
        Intent it = getIntent();
        user = (USER) it.getSerializableExtra("USER");
        //================네트워크 처리 로직
        ClientSocket sk = new ClientSocket(null);
        COMMUTING_TIME CMT = new COMMUTING_TIME();
        CMT.setOrderOperation("SELECT");
        CMT.setSelectRequest("SELECT * FROM COMMUTING_TIME WHERE ID LIKE '"+user.getID()+"';");
        ObjectTable obj = CMT;
        sk.setObj(obj);
        sk.start();



        setContentView(R.layout.activity_main);

        while(sk.flag){
            Log.d("TAG3","다시또?");
        }
        ArrayList<ObjectTable> tTime =new ArrayList<>(sk.getQueue2());

        for(int i=0;i<tTime.size();i++)
        {
            COMMUTING_TIME ct= (COMMUTING_TIME) tTime.get(i);
            if(ct.getDAY().equals("월")) mon_time =ct;
            else if(ct.getDAY().equals("화")) thus_time=ct;
            else if(ct.getDAY().equals("수")) wedn_time=ct;
            else if(ct.getDAY().equals("목")) thurs_time=ct;
            else if(ct.getDAY().equals("금")) fri_time=ct;
        }
        //===================네트워크 처리 로직 종료
        Log.d("TAG3", "네트워크 로직 종료");
        inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        relativeLayout = (RelativeLayout) findViewById(R.id.container);
        startButton = (Button)findViewById(R.id.CarpoolButtonStart);
        cancelButton = (Button)findViewById(R.id.CarpoolButtonCancel);

        mContext = this;
        tmapdata=new TMapData();
        //티맵 설정
         tmapview = new TMapView(this);
        // tmapSet(tmapview);


        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //day 이름을 가진 TabLayout을 addTabLayout이라는 메소드를 통해 생성한다
        String[] day = {"월", "화", "수", "목", "금"};
        tab = (TabLayout)findViewById(R.id.tabs);
        for(int i=0; i<day.length; i++)
            addTabLayout(tab, day[i]);
        //==여기
        //==========
        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = 0;
                position = tab.getPosition();
                if(!state.equals(null)) {//등교또는 하교를 유도
                    switch (position) {
                        case 0:
                            if (timeCheck(mon_time, state)) {
                                inflaterChange(0, mon_time, "월");
                            }else{//시간표가 있다는 것이니
                                inflaterChange(1,mon_time,"월");
                            }

                            break;
                        case 1:
                            if (timeCheck(thus_time, state)) {
                                inflaterChange(0, thus_time, "화");
                            }else{
                                inflaterChange(1,thus_time,"화");
                            }
                            break;
                        case 2:
                            if (timeCheck(wedn_time, state)) {
                                inflaterChange(0, wedn_time, "수");
                            }else{
                                inflaterChange(1,wedn_time,"수");
                            }
                            break;
                        case 3:
                            if (timeCheck(thurs_time, state)) {
                                inflaterChange(0, thurs_time, "목");
                            }else{
                                inflaterChange(1,thurs_time,"목");
                            }

                            break;
                        case 4:
                            if (timeCheck(fri_time, state)) {
                                inflaterChange(0, fri_time, "금");
                            }else{
                                inflaterChange(1,fri_time,"금");
                            }
                            break;
                    }
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab){}

            @Override
            public void onTabReselected(TabLayout.Tab tab){}
        });
        goHome= (Button)findViewById(R.id.gotoHome);
        goSchool=(Button)findViewById(R.id.gotoSchool);
        goHome.setEnabled(true);
        goSchool.setEnabled(true);
        goHome.setOnClickListener(this);

        goSchool.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        relativeLayout.removeAllViews();
        int id = v.getId();
        //등교를 선택시 스쿨홈플래그에 1을 전달, 하교를 선택할시 2를 전달
        switch (id) {
            //등교일때는 엔드에 YJpoint, 하교일때는 start에 YJpoint
            case R.id.gotoSchool:
                v.setBackgroundColor(Color.parseColor("#039BE5"));
                goHome.setBackgroundColor(Color.parseColor("#ffffff"));
                state="등교";
                break;
            case R.id.gotoHome:
                v.setBackgroundColor(Color.parseColor("#039BE5"));
                goSchool.setBackgroundColor(Color.parseColor("#ffffff"));
                state="하교";

                break;
        }
    }

    public void divideArray(Queue<ObjectTable> Qobj){
        Awp= new ArrayList<>();
        Aap= new ArrayList<>();
        Abp= new ArrayList<>();
        Acp= new ArrayList<>();
        Atp =new ArrayList<>();
        mp =new ArrayList<>();
        passList = new ArrayList<TMapPoint>();
        int size=Qobj.size();
        for(int i=0;i<size;i++)
        {
            ObjectTable obj = Qobj.poll();
            if(obj.getResultResponse())
            {
                if(obj.getClass().getSimpleName().equals("WAYPOINT"))
                {
                    Awp.add((WAYPOINT) obj);
                    WAYPOINT tempWP = (WAYPOINT) obj;
                    int tindex=tempWP.getWAYPOINT_TOTALINDEX();
                    TPoint tp = new TPoint(tempWP.getWAYPOINT_LATITUDE(),tempWP.getWAYPOINT_LONGITUDE(),"name",tindex);
                    tp.flag="경유";
                    Atp.add(tp);
                }
                else if(obj.getClass().getSimpleName().equals("ACCESS_POINT_MANAGEMENT"))
                {
                    Aap.add((ACCESS_POINT_MANAGEMENT) obj);
                    ACCESS_POINT_MANAGEMENT tempAP = (ACCESS_POINT_MANAGEMENT) obj;
                    int tindex=tempAP.getACCESS_POINT_TOTALINDEX();
                    TPoint tp = new TPoint(tempAP.getACCESS_POINT_LATITUDE(),tempAP.getACCESS_POINT_LONGITUDE(),tempAP.getCARPOOL_DESCRIPTION(),tindex);
                    tp.flag="카풀";
                    Atp.add(tp);

                }
                else if(obj.getClass().getSimpleName().equals("CARPOOL_PATH_MANAGEMENT"))
                {
                    Acp.add((CARPOOL_PATH_MANAGEMENT) obj);
                }
                else if(obj.getClass().getSimpleName().equals("BOARDING_PASS"))
                {
                    Abp.add((BOARDING_PASS) obj);
                }
            }
        }

    }
    public void inflaterChange(int i,COMMUTING_TIME CT,String day){
        relativeLayout.removeAllViews();
        inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(i==0)//시간표가없다면
        {
            setInflater(CT, day);
        }else if(i==1){//카풀정보 체크
            queue2 =new LinkedList<>(selectCarpool_PATH_MANAGEMENT(day,state));
            boolean flg = queue2.peek().getResultResponse();
            if (flg==false)//false면 없다는 뜻이니 등록화면으로 유도
            {
                checkCar(CT, day, state,user.getID());
            }else
            {//true면 성공
                divideArray(queue2);
                inflaterChange(3,CT,day);
            }
        }else if(i==2){//카풀 정보가 모두 등록되었을 때
            successRegister(CT,day);
        }else if(i==3){//카풀 정보를 띄울 때
            carpoolInfo(CT,day);
        }else if(i==4){//카풀 시작을 눌렀을 때

        }

    }

    public void carpoolInfo(final COMMUTING_TIME CT, final String day){
        inflater.inflate(R.layout.activity_carpoolinfo,relativeLayout,true);
        tmapview=new TMapView(this);
        tmapdata=new TMapData();
        RelativeLayout relativeLayout2 = (RelativeLayout)findViewById(R.id.container);
        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        relativeLayout2.addView(tmapview);

        Collections.sort(Atp);
        start=new TMapPoint(Acp.get(0).getSTARTING_POINT_LATITUDE(),Acp.get(0).getSTARTING_POINT_LONGITUDE());
        end =new TMapPoint(Acp.get(0).getDESTINATION_LATITUDE(),Acp.get(0).getDESTINATION_LONGITUDE());
        for(int i=0;i<Atp.size();i++)
        {
            TMapPoint tempPoint = new TMapPoint(Atp.get(i).latitude,Atp.get(i).longitude);
            passList.add(tempPoint);
        }


        //TPointStartEnd는 시간계산할 때 추가하는 쪽으로

        Tstart=new TPoint(Acp.get(0).getDESTINATION_LATITUDE(),Acp.get(0).getDESTINATION_LONGITUDE(),"출발");
        Tend = new TPoint(Acp.get(0).getSTARTING_POINT_LATITUDE(),Acp.get(0).getSTARTING_POINT_LONGITUDE(),"도착");
        Atp.add(0,Tstart);
        Atp.add(Atp.size()-1,Tend);
        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end, passList, 0,
                new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        tmapview.addTMapPath(polyLine);
                        tmapview.zoomToTMapPoint(start,end);
                        tmapview.refreshMap();
                    }
                });
           //public TPoint(double latitude, double longitude,String description,int index)

        if(state.equals("등교"))
        {
            TPoint st = null;
            TPoint ed = null;
            if (Atp.size() > 1) {
                for(int i=Atp.size()-1;i>=0;i--) {
                    if (i == Atp.size()-1) {
                        st = Atp.get(i);
                        ed= new TPoint(end.getLatitude(),end.getLongitude());
                        Date date= new Date(0,0,0);
                        date.setHours(CT.getTIME_FOR_SCHOOL().getHours());
                        date.setMinutes(CT.getTIME_FOR_SCHOOL().getMinutes());
                        timeSelect(st, ed,date);//timeSelect(final TPoint start, final TPoint end);이게 등교
                    } else if (i > 0 && i < (Atp.size() - 1)) {
                        ed = Atp.get(Atp.size()-i);
                        st = Atp.get(i);
                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                    } else if (i ==0) {
                        st = new TPoint(start.getLatitude(), start.getLongitude());
                        ed = Atp.get(i);
                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                    }
                }
            }
            else
            {
                ed= new TPoint(end.getLatitude(),end.getLongitude());
                st=Atp.get(0);
                timeSelect(st, ed,CT.getTIME_FOR_SCHOOL());//timeSelect(final TPoint start, final TPoint end);이게 등교
                st=null;
                ed=null;
                st = new TPoint(start.getLatitude(), start.getLongitude());
                ed = Atp.get(0);
                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
            }
        }else if(state.equals("하교")){
            TPoint st = null;
            TPoint ed = null;
            if (Atp.size() > 1) {
                for(int i=0;i<Atp.size();i++) {
                    if (i == 0) {
                        st = new TPoint(start.getLatitude(), start.getLongitude());
                        ed = Atp.get(i);
                        Date date= new Date(0,0,0);
                        date.setHours(CT.getTIME_FOR_HOME().getHours());
                        date.setMinutes(CT.getTIME_FOR_HOME().getMinutes());
                        timeSelect(st, ed,CT.getTIME_FOR_HOME());//timeSelect(final TPoint start, final TPoint end);이게 등교
                    } else if (i > 0 && i < (Atp.size() - 1)) {
                        st = Atp.get(i - 1);
                        ed = Atp.get(i);
                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                    } else if (i == (Atp.size() - 1)) {
                        st = Atp.get(i);
                        ed = new TPoint(end.getLatitude(), end.getLongitude());
                        timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                    }
                }
            }
            else
            {
                st = new TPoint(start.getLatitude(), start.getLongitude());
                ed = Atp.get(0);
                timeSelect(st, ed,CT.getTIME_FOR_HOME());//timeSelect(final TPoint start, final TPoint end);이게 등교
                st=null;
                ed=null;
                st = Atp.get(0);
                ed = new TPoint(end.getLatitude(), end.getLongitude());
                timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
            }
        }

    }






    public void successRegister(final COMMUTING_TIME CT, final String day){
        inflater.inflate(R.layout.register_success,relativeLayout,true);
        Button sucbutton = (Button)findViewById(R.id.Regsuccess);
        sucbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflaterChange(1,CT,day);
            }
        });
    }
    public void checkCar(final COMMUTING_TIME CT, final String day, final String state, final String ID){
        tmapview =null;
        tmapview=new TMapView(this);
        inflater.inflate(R.layout.car_not,relativeLayout,true);
        RelativeLayout relativeLayout2 = (RelativeLayout)findViewById(R.id.one_tmap);
        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        relativeLayout2.addView(tmapview);

        Button button =(Button)findViewById(R.id.register_car);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, CarpoolUploadMain.class);
                it.putExtra("COMMUTING_TIME",CT);//시간 넣고
                it.putExtra("day",day);//요일 넣고
                it.putExtra("state",state);//상태 넣고
                it.putExtra("ID",ID);//아이디넣고
                startActivityForResult(it, CODE1);
            }
        });
    }



    public void setInflater(final COMMUTING_TIME CT,final String day){
        inflater.inflate(R.layout.activity_timetable,relativeLayout,true);

        final EditText GTH = (EditText) findViewById(R.id.GTH);
        final EditText GTS = (EditText) findViewById(R.id.GTS);
        final ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] str= GTH.getText().toString().split(" : ");
                Time TFH = new Time(0,0,0);
                Log.d("TAG3",str[0]);
                Log.d("TAG3",str[1]);
                TFH.setHours(Integer.parseInt(str[0]));
                TFH.setMinutes(Integer.parseInt(str[1]));

                Log.d("TAG3","GTS"+GTS.getText());

                String[] str2= GTS.getText().toString().split(" : ");
                Time TFS = new Time(0,0,0);
                TFS.setHours(Integer.parseInt(str2[0]));
                TFS.setMinutes(Integer.parseInt(str2[1]));
                CT.setTIME_FOR_HOME(TFH);
                CT.setTIME_FOR_SCHOOL(TFS);

                ClientSocket sk = new ClientSocket(null);
                CT.setOrderOperation("UPDATE");
                CT.setUpdateRequest("update COMMUTING_TIME set TIME_FOR_HOME='"+str[0]+":"+str[1]+":00"+"' , TIME_FOR_SCHOOL ='"+str2[0]+":"+str2[1]+":00"+
                        "' where ID='"+user.getID()+"' and DAY='"+day+"';");
                ObjectTable obj = CT;
                sk.setObj(obj);
                sk.start();
                inflaterChange(1,CT,day);
            }
        });
        imageButton.setEnabled(false);
        Button GTB = (Button) findViewById(R.id.GT_button);
        GTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timetable_dialog td = new Timetable_dialog(MainActivity.this, GTH, GTS);
                td.show();


                imageButton.setEnabled(true);

            }
        });

    }
    public void setInflater_Carpool(){
        inflater.inflate(R.layout.car_not,relativeLayout,true);

    }



    public boolean timeCheck(COMMUTING_TIME CT, String state){
        boolean check=false;
        if(state.equals("등교")){//TIME_FOR_SCHOOL
            Log.d("TAG3","");
            if(CT.getTIME_FOR_SCHOOL()==null){
                check= true;
            }else{
                check= false;
            }
        }else if(state.equals("하교")){
            if(CT.getTIME_FOR_HOME()==null){
                check= true;
            }else{
                check= false;
            }
        }
        return check; //true 일 경우 실행시키자
    }

    public Queue<ObjectTable> selectCarpool_PATH_MANAGEMENT(String day,String state){//day는 요일, state는 등교 또는 하교
        ClientSocket sk = new ClientSocket(null);
        CARPOOL_PATH_MANAGEMENT cp = new CARPOOL_PATH_MANAGEMENT();
        cp.setOrderOperation("SELECT");
        cp.setSelectRequest("SELECT * FROM CARPOOL_PATH_MANAGEMENT WHERE CARPOOL_SERIAL_NUMBER LIKE '"+user.getID()+"/"+day+"/"+state+"';");
        ObjectTable obj = cp;
        sk.setObj(obj);
        ACCESS_POINT_MANAGEMENT ap =new ACCESS_POINT_MANAGEMENT();
        ap.setOrderOperation("SELECT");
        ap.setSelectRequest("SELECT * FROM ACCESS_POINT_MANAGEMENT WHERE CARPOOL_SERIAL_NUMBER LIKE '"+user.getID()+"/"+day+"/"+state+"';");
        obj=ap;
        sk.setObj(obj);
        WAYPOINT wp = new WAYPOINT();
        wp.setOrderOperation("SELECT");
        wp.setSelectRequest("SELECT * FROM WAYPOINT WHERE CARPOOL_SERIAL_NUMBER LIKE '"+user.getID()+"/"+day+"/"+state+"';");
        obj=wp;
        sk.setObj(obj);
        BOARDING_PASS bp = new BOARDING_PASS();
        bp.setOrderOperation("SELECT");
        bp.setSelectRequest("SELECT * FROM BOARDING_PASS WHERE CARPOOL_SERIAL_NUMBER LIKE '"+user.getID()+"/"+day+"/"+state+"';");
        sk.start();
        while(sk.flag){

        }
        Queue<ObjectTable> queue2 = new LinkedList<>(sk.getQueue2());
        return queue2;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch(requestCode){
            case CODE1:
                if(resultCode==RESULT_OK) {//서브Activity에서 보내온 resultCode와 비교
                    //서브액티비티에서 인텐트에 담아온 정보 꺼내기q
                    day= data.getStringExtra("day");//요일 넣고
                    state=data.getStringExtra("state");//상태 넣고
                    CMTT= (COMMUTING_TIME) data.getSerializableExtra("COMMUTING_TIME");//시간 넣고
                    inflaterChange(2,CMTT,day);

                }
                break;
        }

    }



    public void addTabLayout(TabLayout tab, String name) {
        tab.addTab(tab.newTab().setText(name));
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
    public static String getAddress(Context mContext,double lat, double lng) {
        String nowAddress ="현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List <Address> address;
        try {
            if (geocoder != null) {
                //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
                //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
                address = geocoder.getFromLocation(lat, lng, 1);
                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    if(currentLocationAddress.contains("군 "))
                    {
                        String[] str= currentLocationAddress.split("군 ");
                        nowAddress=str[1];
                    }
                    else if(currentLocationAddress.contains("구 ")) {
                        String[] str= currentLocationAddress.split("구 ");
                        nowAddress=str[1];
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return nowAddress;
    }




    public void timeSelect(final TPoint start, final TPoint end, final Date date)
    {
        start.name= getAddress(this, start.latitude, start.longitude);
        end.name=getAddress(this, end.latitude, end.longitude);

        if(state.equals("등교")){

            HashMap<String, String> pathInfo = new HashMap<String, String>();
            pathInfo.put("rStName",start.name);//비어있다.
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
                            end.hour=String.valueOf(hour);
                            end.minute=String.valueOf(minutes);
                            end.second=String.valueOf(second);
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
                            start.hour=strHour;
                            start.minute=strMin;
                            start.second=strSec;

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
        APmarker.setID(tp.name);//tp.name
        APmarker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        APmarker.setCalloutTitle(tp.name);//tp.name
        APmarker.setCalloutSubTitle(nowTime);       //풍선뷰 보조메세지
//      tItem.setCalloutLeftImage(bitmap);  //풍선뷰의 왼쪽 이미지 지정 //오른쪽은 RIGHT
//동현동현
        if(tp.flag.equals("경유")){

        }else if(tp.flag.equals("카풀")){

        }
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.car);
        APmarker.setCalloutRightButtonImage(bitmap);

        String strID = tp.name+cnt++;
        tmapview.addMarkerItem(strID, APmarker);
        mArrayMarkerID.add(strID);
        //풍선뷰 선택할 때 나타나는 이벤트
    }

}
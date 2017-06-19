package com.example.seeth.car;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class progress extends Fragment {
    RelativeLayout Layout = null;
    TMapView tmapview = null;
    String mapKey = "c2b1ca96-9e28-3cc7-8697-092c86b91e5d";
    double latitude;
    double longitude;
    private static int markerid = 0;
    TMapData tMapData = new TMapData();
    ClientSocket sk,sk2,sk3,sk5;
    String id,position,goback,serial_number,ap_serial_number,driver_id;
    Queue<ObjectTable> queue3,queue4,queue6;
    Queue<ObjectTable> queue2;
    ArrayList<WAYPOINT> Awp;
    ArrayList<CARPOOL_PATH_MANAGEMENT> Acp;
    ArrayList<ACCESS_POINT_MANAGEMENT> Aap;
    ArrayList<BOARDING_PASS> Abp;
    ArrayList<TMapPoint> passList;
    ArrayList<TPoint> Atp;
    ArrayList<TPoint> mp;
    TMapPoint start;
    TMapPoint end;
    String day,state,currentTimeText;
    TextView tv_location,tv_time;
    ExampleThread et;
    Boolean isrunning = true;
    BitmapDrawable drawable = null;
    TMapMarkerItem APmarker;
    Bitmap bitmap;
    Button arrival_btn;
    public progress(String id, String position, String goback) {
        this.id = id;
        this.position = position;
        this.goback = goback;
        day=position;
        state=goback;
        ClientSocket cs = new ClientSocket(null);
        BOARDING_PASS bp = new BOARDING_PASS();
        bp.setOrderOperation("SELECT");
        bp.setSelectRequest("select * from boarding_pass where carpool_serial_number like '%/"+position+"/"+goback+"';");
        cs.setObj((ObjectTable) bp);
        cs.start();
        while (cs.flag){}
        bp=(BOARDING_PASS)cs.getQueue2().poll();
        String a = bp.getCARPOOL_SERIAL_NUMBER();
        String[] b = a.split("/");
        driver_id = b[0];
        queue2 =new LinkedList<>(selectCarpool_PATH_MANAGEMENT(position,goback));
        divideArray(queue2);
        Collections.sort(Atp);
        start=new TMapPoint(Acp.get(0).getSTARTING_POINT_LATITUDE(),Acp.get(0).getSTARTING_POINT_LONGITUDE());
        end =new TMapPoint(Acp.get(0).getDESTINATION_LATITUDE(),Acp.get(0).getDESTINATION_LONGITUDE());
        for(int i=0;i<Atp.size();i++)
        {
            TMapPoint tempPoint = new TMapPoint(Atp.get(i).latitude,Atp.get(i).longitude);
            passList.add(tempPoint);
            Log.v("abcdefg",""+passList.get(i).getLongitude());
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = (View)inflater.inflate(R.layout.activity_progress,container,false);
        tv_location = (TextView)rootview.findViewById(R.id.kcy2);
        tv_time=(TextView)rootview.findViewById(R.id.kcy1);
        arrival_btn = (Button)rootview.findViewById(R.id.arrival_button);
        arrival_btn.setBackgroundColor(Color.rgb(213,213,213));
        arrival_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrival_check();
            }
        });
        sk = new ClientSocket(null);
        BOARDING_PASS bp = new BOARDING_PASS();
        bp.setOrderOperation("SELECT");
        bp.setSelectRequest("select * from boarding_pass where carpool_serial_number like '%/"+position+"/"+goback+"' and occupant_id = '"+id+"';");
        Log.v("ababab",""+bp.getSelectRequest());
        sk.setObj((ObjectTable)bp);
        sk.start();
        while (sk.flag){
            Log.v("abcd","1");
        }
        queue3 = sk.getQueue2();
        bp = (BOARDING_PASS) queue3.poll();
        if(Integer.parseInt(bp.getACCPTANCE_STATE())==3){
            arrival_btn.setClickable(false);
            arrival_btn.setBackgroundColor(Color.rgb(178,235,244));
            arrival_btn.setText("탑승완료");
        }
        ap_serial_number = bp.getKAPUL_ACCESS_POINT_SERIAL_NUMBER();
        serial_number = bp.getCARPOOL_SERIAL_NUMBER();
        Log.v("abcdabcd",""+serial_number);
        sk3 = new ClientSocket(null);
        ACCESS_POINT_MANAGEMENT apm = new ACCESS_POINT_MANAGEMENT();
        apm.setOrderOperation("SELECT");
        apm.setSelectRequest("select * from access_point_management where kapul_access_point_serial_number = '"+ap_serial_number+"';");
        Log.v("aabbccddeeff",""+apm.getSelectRequest());
        sk3.setObj((ObjectTable)apm);
        sk3.start();
        while (sk3.flag){
            Log.v("aabbccdd","여기가거긴가");
        }
        queue4 = sk3.getQueue2();
        apm = (ACCESS_POINT_MANAGEMENT)queue4.poll();
        latitude = apm.getACCESS_POINT_LATITUDE();
        longitude = apm.getACCESS_POINT_LONGITUDE();

        sk2 = new ClientSocket(null);
        CARPOOL_PATH_MANAGEMENT cpm = new CARPOOL_PATH_MANAGEMENT();
        cpm.setOrderOperation("SELECT");
        cpm.setSelectRequest("select * from carpool_path_management where carpool_serial_number = '"+serial_number+"';");
        Log.v("qweqweqwe",cpm.getSelectRequest());
        sk2.setObj((ObjectTable)cpm);
        sk2.start();
        while (sk2.flag){
            Log.v("abcd","2");
        }
        queue3 = sk2.getQueue2();

        cpm = (CARPOOL_PATH_MANAGEMENT)queue3.poll();
        Log.v("asdasdasd",""+cpm.getDESTINATION_LATITUDE());

        Layout = (RelativeLayout)rootview.findViewById(R.id.container);
        tmapview = new TMapView(getActivity());
        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);
        tmapview.setIconVisibility(false);
        tmapview.setCenterPoint(longitude, latitude);
        tmapview.setLocationPoint(longitude, latitude);
        tMapData.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end, passList, 0,
                new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine tMapPolyLine) {
                        tmapview.addTMapPath(tMapPolyLine);
                    }
                });
        Bitmap bitmap_s = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.start);
        Bitmap bitmap_f = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.finish);
        Bitmap bitmap_p = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.noback);
        tmapview.setTMapPathIcon(bitmap_s,bitmap_f,bitmap_p);
        ClientSocket cs= new ClientSocket(null);
        CARPOOL_PATH_MANAGEMENT CPM = new CARPOOL_PATH_MANAGEMENT();
        CPM.setOrderOperation("SELECT");
        CPM.setSelectRequest("select * from carpool_path_management where carpool_serial_number = '"+serial_number+"';");
        cs.setObj((ObjectTable)CPM);
        cs.start();
        while (cs.flag){}
        CPM = (CARPOOL_PATH_MANAGEMENT)cs.getQueue2().poll();
        String location = CPM.getSTARTING_POINT_DESCRIPTION()+" - ";
        sk5 = new ClientSocket(null);
        apm.setSelectRequest("select * from access_point_management where carpool_serial_number = '"+serial_number+"' order by access_point_totalindex asc;");
        Log.v("asddzxcqwe",""+apm.getSelectRequest());
        sk5.setObj((ObjectTable)apm);
        sk5.start();
        while (sk5.flag){
            Log.v("asdqwezxc","ap검색");
        }
        queue6 = sk5.getQueue2();
        int size = queue6.size();
        for(int i=0;i<size;i++){
            apm = (ACCESS_POINT_MANAGEMENT)queue6.poll();
            addMarker(apm.getACCESS_POINT_LONGITUDE(), apm.getACCESS_POINT_LATITUDE(), apm.getCARPOOL_DESCRIPTION(),apm.getACCESS_POINT_TIME_OF_ARRIVAL().toString());
            location += apm.getCARPOOL_DESCRIPTION() + " - ";
        }
        Layout.addView(tmapview);
        location += CPM.getDESTINATION_DESCRIPTION();
        tv_location.setText(location);
        et = new ExampleThread();
        et.start();
        return rootview;
    }
    public void arrival_check(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.create();
        builder.setTitle("탑승확인");
        builder.setMessage("카풀 차량에 탑승하셨습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClientSocket cs = new ClientSocket(null);
                BOARDING_PASS bp = new BOARDING_PASS();
                bp.setOrderOperation("UPDATE");
                bp.setUpdateRequest("update boarding_pass set accptance_state = 3 where carpool_serial_number like '%/"+position+"/"+goback+"' and occupant_id = '"+id+"';");
                Log.v("222222",""+bp.getUpdateRequest());
                cs.setObj((ObjectTable)bp);
                cs.start();
                cs=null;
                arrival_btn.setBackgroundColor(Color.rgb(178,235,244));
                arrival_btn.setClickable(false);
                arrival_btn.setText("탑승완료");
                Toast.makeText(getActivity(),"탑승확인 되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    public void onDestroy(){
        handler.sendMessage(Message.obtain(handler,3,bitmap));
        isrunning=false;
        et=null;
        APmarker.setIcon(null);
        bitmap=null;
        super.onDestroy();
    }
    @Override
    public void onStart(){
        super.onStart();
        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.mipmap.carpool_car);
    }

    //    private static void recycleBitmap(BitmapDrawable d) {
//        Bitmap b = d.getBitmap();
//        Log.v("aaabbb","됨?");
//        b.recycle();
//        d.setCallback(null);
//    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:     // 메시지로 넘겨받은 파라미터, 이 값으로 어떤 처리를 할지 결정
                    String str = (String)msg.obj;
                    tv_time.setText(str);
                    break;
                case 2:
                    TMapMarkerItem carmarker = (TMapMarkerItem)msg.obj;
                    tmapview.addMarkerItem("car", carmarker);
                    break;
                case 3:
                    Log.v("qweqwe",""+msg.obj.getClass().getName());
                    Bitmap b = (Bitmap)msg.obj;
                    b.recycle();
                    b=null;
                    break;
            }
        }
    };
    private class ExampleThread extends Thread {
        private static final String TAG = "ExampleThread";
        public ExampleThread() {
        }
        public void run() {
            while (isrunning) {
                try { //스레드에게 수행시킬 동작들 구현
                    scan();
                    Thread.sleep(60000); //
                }
                catch (InterruptedException i) {
                    i.printStackTrace();
                }
            }
        }
    }
    public void scan(){
        ClientSocket cs;
        CARPOOL_PATH_MANAGEMENT cpm;
        BOARDING_PASS bp;
        ACCESS_POINT_MANAGEMENT apm;
        TPoint s,e;
        Date d;
        Log.v("aaaa","여기 드러옴?");
        cs = new ClientSocket(null);
        cpm = new CARPOOL_PATH_MANAGEMENT();
        cpm.setOrderOperation("SELECT");
        cpm.setSelectRequest("select * from carpool_path_management where carpool_serial_number = '"+driver_id+"/"+position+"/"+goback+"';");
        cs.setObj((ObjectTable)cpm);
        cs.start();
        while (cs.flag){Log.v("111111","11");}
        cpm = (CARPOOL_PATH_MANAGEMENT)cs.getQueue2().poll();
        cs=null;
        s = new TPoint(cpm.getCURRENT_DRIVER_LATITUDE(),cpm.getCURRENT_DRIVER_LONGITUDE());
        car_lcation(cpm.getCURRENT_DRIVER_LONGITUDE(), cpm.getCURRENT_DRIVER_LATITUDE());
        Log.v("bbbb","여기 드러옴?");
        cs = new ClientSocket(null);
        bp = new BOARDING_PASS();
        bp.setOrderOperation("SELECT");
        bp.setSelectRequest("select * from boarding_pass where carpool_serial_number like '%/"+position+"/"+goback+"' and occupant_id = '"+id+"'");
        cs.setObj((ObjectTable)bp);
        cs.start();
        while (cs.flag){Log.v("222222","22");}
        bp = (BOARDING_PASS)cs.getQueue2().poll();
        cs=null;
        cs = new ClientSocket(null);
        apm = new ACCESS_POINT_MANAGEMENT();
        apm.setOrderOperation("SELECT");
        apm.setSelectRequest("select *  from access_point_management where kapul_access_point_serial_number = '"+bp.getKAPUL_ACCESS_POINT_SERIAL_NUMBER()+"';");
        cs.setObj((ObjectTable)apm);
        cs.start();
        while (cs.flag){Log.v("333333","33");}
        apm=(ACCESS_POINT_MANAGEMENT)cs.getQueue2().poll();
        e = new TPoint(apm.getACCESS_POINT_LATITUDE(),apm.getACCESS_POINT_LONGITUDE());
        long now = System.currentTimeMillis(); // 현재시간을 date 변수에 저장한다.
        d = new Date(now);
        if(cpm.getCURRENT_DRIVER_LATITUDE()!=0) {
            timeSelect(s,e,d);
        }
        Log.v("cccc","여기 드러옴?");
    }
    public void timeSelect(final TPoint start, final TPoint end, final Date date) {
        HashMap<String, String> pathInfo = new HashMap<String, String>();
        pathInfo.put("rStName", "여기");//비어있다.
        pathInfo.put("rStlat", String.valueOf(start.latitude));
        pathInfo.put("rStlon", String.valueOf(start.longitude));
        pathInfo.put("rGoName", "여기기");
        pathInfo.put("rGolat", String.valueOf(end.latitude));
        pathInfo.put("rGolon", String.valueOf(end.longitude));
        pathInfo.put("type", "arrival");
        tMapData.findTimeMachineCarPath(pathInfo, date, null, "00",
                new TMapData.FindTimeMachineCarPathListenerCallback() {
                    @Override
                    public void onFindTimeMachineCarPath(Document doc) {
                        NodeList ns = doc.getElementsByTagName("tmap:totalTime");//현재위치에서 도착하는  시간이 산출?
                        Node node = ns.item(0);
                        int totalTime = Integer.valueOf(node.getTextContent());//예상도착시간
                        String strMin = String.valueOf((totalTime) / 60);//도착시간
                        currentTimeText = "탑승 " + strMin + "분 전  ";
                        Log.v("nowtime",""+currentTimeText);
                        handler.sendMessage(Message.obtain(handler, 1, currentTimeText));
                    }
                });
    }

    public void addMarker(double Longitude, double Latitude, String location_name,String arrival) {//지도에 마커 추가
        TMapPoint poi = new TMapPoint(Latitude, Longitude);
        TMapMarkerItem marker = new TMapMarkerItem();
        marker.setTMapPoint(poi);
        marker.setName("테스트");
        marker.setVisible(marker.VISIBLE);
        marker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        marker.setCalloutTitle(location_name);
        String stm = "도착 "+arrival;
        marker.setCalloutSubTitle(stm);

        markerid++;
        tmapview.addMarkerItem(String.format("id"+markerid), marker);
    }
    public void car_lcation(double Longitude, double Latitude){
        TMapPoint poi = new TMapPoint(Latitude, Longitude);
        APmarker = new TMapMarkerItem();
        APmarker.setTMapPoint(poi);
        APmarker.setName("테스트");
        APmarker.setVisible(APmarker.VISIBLE);
        APmarker.setCanShowCallout(false); //AP에 풍선뷰 사용 여부
        APmarker.setIcon(bitmap);
        handler.sendMessage(Message.obtain(handler, 2, APmarker));
    }
    public Queue<ObjectTable> selectCarpool_PATH_MANAGEMENT(String day,String state){//day는 요일, state는 등교 또는 하교
        ClientSocket sk = new ClientSocket(null);
        CARPOOL_PATH_MANAGEMENT cp = new CARPOOL_PATH_MANAGEMENT();
        cp.setOrderOperation("SELECT");
        cp.setSelectRequest("SELECT * FROM CARPOOL_PATH_MANAGEMENT WHERE CARPOOL_SERIAL_NUMBER LIKE '"+driver_id+"/"+day+"/"+state+"';");
        ObjectTable obj = cp;
        sk.setObj(obj);
        ACCESS_POINT_MANAGEMENT ap =new ACCESS_POINT_MANAGEMENT();
        ap.setOrderOperation("SELECT");
        ap.setSelectRequest("SELECT * FROM ACCESS_POINT_MANAGEMENT WHERE CARPOOL_SERIAL_NUMBER LIKE '"+driver_id+"/"+day+"/"+state+"';");
        obj=ap;
        sk.setObj(obj);
        WAYPOINT wp = new WAYPOINT();
        wp.setOrderOperation("SELECT");
        wp.setSelectRequest("SELECT * FROM WAYPOINT WHERE CARPOOL_SERIAL_NUMBER LIKE '"+driver_id+"/"+day+"/"+state+"';");
        obj=wp;
        sk.setObj(obj);
        sk.start();
        while(sk.flag){

        }
        Queue<ObjectTable> queue2 = new LinkedList<>(sk.getQueue2());
        return queue2;
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
                    tp.AP_SERIAL=tempAP.getKAPUL_ACCESS_POINT_SERIAL_NUMBER();
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
}
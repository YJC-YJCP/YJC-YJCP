package com.example.seeth.car;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.seeth.car.ACCESS_POINT_MANAGEMENT;
import com.example.seeth.car.BOARDING_PASS;
import com.example.seeth.car.CARPOOL_PATH_MANAGEMENT;
import com.example.seeth.car.ObjectTable;
import com.example.seeth.car.WAYPOINT;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapMarkerItem2;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.xml.parsers.ParserConfigurationException;

public class progress extends Fragment {
    RelativeLayout Layout = null;
    TMapView tmapview = null;
    String mapKey = "391d6fad-39f4-33f9-9add-de8fca4240b2";
    double latitude;
    double longitude;
    private static int markerid = 0;
    TMapPolyLine tMapPolyLine;
    TMapData tMapData = new TMapData();
    TMapPoint startpoint;
    TMapPoint endpoint;
    ArrayList<TMapPoint> passlist = new ArrayList<TMapPoint>();
    ClientSocket sk,sk2,sk3,sk4,sk5;
    String id,position,goback,serial_number,ap_serial_number;
    Queue<ObjectTable> queue3,queue4,queue5,queue6;
    public progress(String id, String position, String goback) {
        this.id = id;
        this.position = position;
        this.goback = goback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = (View)inflater.inflate(R.layout.activity_progress,container,false);
        sk = new ClientSocket(getActivity());
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
        ap_serial_number = bp.getKAPUL_ACCESS_POINT_SERIAL_NUMBER();
        serial_number = bp.getCARPOOL_SERIAL_NUMBER();
        Log.v("abcdabcd",""+serial_number);
        sk3 = new ClientSocket(getActivity());
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

        sk2 = new ClientSocket(getActivity());
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
//        tmapview.setCenterPoint(128.624070,35.891695);
//        tmapview.setLocationPoint(128.624070,35.891695);
        tmapview.setCenterPoint(longitude, latitude);
        tmapview.setLocationPoint(longitude, latitude);
        endpoint = new TMapPoint(cpm.getDESTINATION_LATITUDE(),cpm.getDESTINATION_LONGITUDE());
        startpoint = new TMapPoint(cpm.getSTARTING_POINT_LATITUDE(),cpm.getSTARTING_POINT_LONGITUDE());
        tmapview.setTrafficInfo(false);
        sk4 = new ClientSocket(getActivity());
        WAYPOINT wp = new WAYPOINT();
        wp.setOrderOperation("SELECT");
        wp.setSelectRequest("select * from waypoint where carpool_serial_number = '"+serial_number+"' order by waypoint_order asc;");
        sk4.setObj((ObjectTable)wp);
        sk4.start();
        while (sk4.flag){
            Log.v("asdqwe","경로검색");
        }
        queue5 = sk4.getQueue2();
        int size = queue5.size();
        for (int i=0;i<size;i++){
            wp = (WAYPOINT)queue5.poll();
            passlist.add(new TMapPoint(wp.getWAYPOINT_LATITUDE(),wp.getWAYPOINT_LONGITUDE()));
        }
        tMapData.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, startpoint, endpoint, passlist, 10,
                new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine tMapPolyLine) {
                        tmapview.addTMapPath(tMapPolyLine);
                    }
                });
        sk5 = new ClientSocket(getActivity());
        apm.setSelectRequest("select * from access_point_management where carpool_serial_number = '"+serial_number+"';");
        Log.v("asddzxcqwe",""+apm.getSelectRequest());
        sk5.setObj((ObjectTable)apm);
        sk5.start();
        while (sk5.flag){
            Log.v("asdqwezxc","ap검색");
        }
        queue6 = sk5.getQueue2();
        size = queue6.size();
        for(int i=0;i<size;i++){
            apm = (ACCESS_POINT_MANAGEMENT)queue6.poll();
            addMarker(apm.getACCESS_POINT_LONGITUDE(), apm.getACCESS_POINT_LATITUDE(), apm.getCARPOOL_DESCRIPTION(),apm.getACCESS_POINT_TIME_OF_ARRIVAL().toString());
        }
        Layout.addView(tmapview);
        car_lcation(128.625400, 35.886718);
        return rootview;
    }
    public void addMarker(double Longitude, double Latitude, String location_name,String arrival) {//지도에 마커 추가
        TMapPoint poi = new TMapPoint(Latitude, Longitude);
        TMapMarkerItem APmarker = new TMapMarkerItem();
        APmarker.setTMapPoint(poi);
        APmarker.setName("테스트");
        APmarker.setVisible(APmarker.VISIBLE);
        APmarker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        APmarker.setCalloutTitle(location_name);
        String stm = "도착 "+arrival;
        APmarker.setCalloutSubTitle(stm);

        markerid++;
        tmapview.addMarkerItem(String.format("id"+markerid), APmarker);
    }
    public void car_lcation(double Longitude, double Latitude){
        TMapPoint poi = new TMapPoint(Latitude, Longitude);
        TMapMarkerItem APmarker = new TMapMarkerItem();
        APmarker.setTMapPoint(poi);
        APmarker.setName("테스트");
        APmarker.setVisible(APmarker.VISIBLE);
        APmarker.setCanShowCallout(false); //AP에 풍선뷰 사용 여부
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.mipmap.carpool_car);
        APmarker.setIcon(bitmap);
        tmapview.addMarkerItem("car", APmarker);
    }
}
package com.example.seeth.car;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seeth.car.ACCESS_POINT_MANAGEMENT;
import com.example.seeth.car.BOARDING_PASS;
import com.example.seeth.car.ObjectTable;
import com.example.seeth.car.USER;
import com.skp.Tmap.TMapCircle;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Queue;

public class search extends Fragment{
    RelativeLayout Layout = null;
    TMapView tmapview = null;
    Button up_km=null,down_km=null,km=null,search_button=null,search_location=null,textok;
    EditText location=null;
    TMapCircle tcircle;
    int KM=1;
    private static int markerid = 0;
    double latitude,search_latitude;     //경도
    double longitude,search_longitude;    //위도
    TMapPoint tmp=null;
    View rootview=null;
    String mapKey = "c2b1ca96-9e28-3cc7-8697-092c86b91e5d";
    ClientSocket sk,sk2,sk3,sk4,sk5;
    Queue<ObjectTable> queue3;
    TMapPoint poi;
    TMapMarkerItem APmarker;
    String id,position,goback;
    int flag=0;
    public search(double longitude, double latitude,String id,String position,String goback){
        search_latitude = latitude;
        search_longitude=longitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.position = position;
        this.goback = goback;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = (View)inflater.inflate(R.layout.activity_search,container,false);
        Layout = (RelativeLayout)rootview.findViewById(R.id.tmap);
        tcircle = new TMapCircle();
        tmapview = new TMapView(getActivity());
        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);
        tmapview.setCenterPoint(longitude,latitude);
        tmapview.setLocationPoint(longitude,latitude);
        Layout.addView(tmapview);

        km = (Button)rootview.findViewById(R.id.km);
        up_km = (Button)rootview.findViewById(R.id.up);
        up_km.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(KM<3){
                    KM++;
                }
                km.setText(String.valueOf(KM)+"km");
            }
        });
        down_km = (Button)rootview.findViewById(R.id.down);
        down_km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(KM>1){
                    KM--;
                }
                km.setText(String.valueOf(KM)+"km");
            }
        });
        search_button = (Button)rootview.findViewById(R.id.start_search) ;
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=0;
                tmapview.removeAllMarkerItem(); //마커들 다지우기
                tmp = new TMapPoint(latitude,longitude);
                tcircle.setCenterPoint(tmp); //서클 중심점
                if(KM==1){                       //km수마다 반경다르게 주기
                    tcircle.setRadius(1000);
                }
                else if(KM==2){                 //km수마다 반경다르게 주기
                    tcircle.setRadius(2000);
                }
                else if(KM==3){                 //km수마다 반경다르게 주기
                    tcircle.setRadius(3000);
                }
                tcircle.setAreaColor(Color.rgb(255,193,158)); //서클색깔
                tcircle.setAreaAlpha(100); //투명도
                tcircle.setLineAlpha(0); //라인투명도
                tcircle.setCircleWidth(2); // 테두리 선 크기
                tmapview.addTMapCircle("tc",tcircle); //맵에 서클 추가 왼쪽이 서클id
                sk = new ClientSocket(getActivity());
                ACCESS_POINT_MANAGEMENT apm = new ACCESS_POINT_MANAGEMENT();
                apm.setOrderOperation("SELECT");
                apm.setSelectRequest("select * from access_point_management where (6371*acos(cos(radians("+String.valueOf(latitude)+"))*cos(radians(access_point_latitude))*cos(radians(access_point_longitude)-radians("+String.valueOf(longitude)+"))+sin(radians("+String.valueOf(latitude)+"))*sin(radians(access_point_latitude))))<"+String .valueOf(KM)+";");
                Log.v("aaabbbccc",""+apm.getSelectRequest());
                sk.setObj((ObjectTable) apm);
                sk.start();
                while (sk.flag){
                    Log.d("TAG3","범위탐색이계속");
                }
                int j;
                queue3 = sk.getQueue2();
                j = queue3.size();
                Log.v("eeedddfff",""+j);
                for (int i = 0; i < j; i++) {
                    apm = (ACCESS_POINT_MANAGEMENT) queue3.poll();
                    if(apm.getCARPOOL_SERIAL_NUMBER()==null)
                        break;
                    String check = apm.getCARPOOL_SERIAL_NUMBER();
                    Log.v("aaabbbccc",""+check);
                    String[] check_position = check.split("/");
                    if (apm.getSEARCH_POSSIBILITY() == 1) {
                        if (position.equals(check_position[1])) {
                            if (goback.equals(check_position[2])) {
                                addMarker(apm.getACCESS_POINT_LONGITUDE(), apm.getACCESS_POINT_LATITUDE(), apm.getCARPOOL_DESCRIPTION(), apm.getACCESS_POINT_TIME_OF_ARRIVAL(), apm.getKAPUL_ACCESS_POINT_SERIAL_NUMBER());
                                flag=1;
                            }
                        }
                    }
                }
                if(flag==0)
                    Toast.makeText(getActivity(),"검색된 카풀이 없습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        search_location = (Button)rootview.findViewById(R.id.location_search_button);
        search_location.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                location = (EditText)rootview.findViewById(R.id.search_location);
                final TMapData tmapdata = new TMapData();
                tmapdata.findTitlePOI(location.getText().toString(), new TMapData.FindTitlePOIListenerCallback() {
                    @Override
                    public void onFindTitlePOI(ArrayList<TMapPOIItem> arrayList) {
                        for(int i=0;i<arrayList.size();i++){
                            tmapview.removeAllMarkerItem();
                            tmapview.removeAllTMapCircle();
                            TMapPOIItem item = arrayList.get(i);
                            tmapview.setCenterPoint(item.getPOIPoint().getLongitude(),item.getPOIPoint().getLatitude());
                            tmapview.setLocationPoint(item.getPOIPoint().getLongitude(),item.getPOIPoint().getLatitude());
                            latitude=item.getPOIPoint().getLatitude();
                            longitude=item.getPOIPoint().getLongitude();
                        }
                    }
                });
            }
        });
        return rootview;
    }

    public void addMarker(double Longitude, double Latitude, final String description, final Time time,String ap_serial) {//지도에 마커 추가
        poi = new TMapPoint(Latitude, Longitude);
        sk = new ClientSocket(getActivity());
        APmarker = new TMapMarkerItem();
        APmarker.setTMapPoint(poi);
        APmarker.setName(ap_serial);
        APmarker.setVisible(APmarker.VISIBLE);
        APmarker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        APmarker.setCalloutTitle(description);
        final String stm = time.toString();
        APmarker.setCalloutSubTitle(stm);
        Bitmap bitmap_i = BitmapFactory.decodeResource(getActivity().getResources(),R.mipmap.carpool_car);
        APmarker.setCalloutRightButtonImage(bitmap_i);
        markerid++;
        tmapview.addMarkerItem(String.format("id"+markerid), APmarker);
        //풍선뷰 선택할 때 나타나는 이벤트
        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                mOnClick(markerItem.getTMapPoint(),markerItem.getCalloutTitle(),markerItem.getCalloutSubTitle(),markerItem.getName());
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void mOnClick(TMapPoint poi, String lo, String ti, final String serial){
        longitude = poi.getLongitude();
        latitude = poi.getLatitude();
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.passenger_info,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final String serial_bunhal[] = serial.split("/");
        sk5 = new ClientSocket(getActivity());
        USER user = new USER();
        user.setOrderOperation("SELECT");
        user.setSelectRequest("select * from user where id = '"+serial_bunhal[0]+"';");
        sk5.setObj((ObjectTable)user);
        sk5.start();
        while (sk5.flag){
            Log.v("aaa","이름검색");
        }
        queue3 = sk5.getQueue2();
        user = (USER)queue3.poll();
        TextView name = (TextView)dialog.findViewById(R.id.info_name);
        name.setText(user.getNAME());
        sk5 = null;
        queue3=null;
        TextView time = (TextView)dialog.findViewById(R.id.arrival_time);
        time.setText(ti);
        TextView ap_location= (TextView)dialog.findViewById(R.id.info_location);
        ap_location.setText(lo);
        TextView day = (TextView)dialog.findViewById(R.id.day);
        day.setText(serial_bunhal[1]);
        TextView gyo = (TextView)dialog.findViewById(R.id.gyo);
        gyo.setText(serial_bunhal[2]);
        TextView car = (TextView)dialog.findViewById(R.id.info_cnumber);
        Log.v("asdasdasd",""+user.getVEHICLE_NUMBER());
        car.setText(user.getVEHICLE_NUMBER());

        Button yes = (Button)dialogView.findViewById(R.id.info_yes);
        yes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sk2 = new ClientSocket(getActivity());
                BOARDING_PASS bp = new BOARDING_PASS();
                bp.setOrderOperation("INSERT");
                bp.setInsertRequest("insert into boarding_pass values('"+serial+"','"+serial_bunhal[0]+"/"+serial_bunhal[1]+"/"+serial_bunhal[2]+"','"+id+"',"+"1);");
                Log.v("code1",""+bp.getInsertRequest());
                sk2.setObj((ObjectTable)bp);
                sk2.start();
                while (sk2.flag){
                    Log.d("TAG3","범위탐색이계속");
                }
                Toast.makeText(getActivity(),"카풀 신청을 요청하였습니다.",Toast.LENGTH_SHORT).show();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentBorC, new waiting_magic(search_longitude,search_latitude,longitude,latitude,id,position,goback));
                fragmentTransaction.commit();
                dialog.dismiss();
            }
        });
        Button no = (Button)dialogView.findViewById(R.id.info_no);
        no.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
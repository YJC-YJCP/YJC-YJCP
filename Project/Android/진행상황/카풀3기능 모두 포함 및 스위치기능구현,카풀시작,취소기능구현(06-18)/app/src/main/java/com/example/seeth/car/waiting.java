package com.example.seeth.car;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.seeth.car.ACCESS_POINT_MANAGEMENT;
import com.example.seeth.car.BOARDING_PASS;
import com.example.seeth.car.ObjectTable;
import com.example.seeth.car.USER;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.sql.Time;
import java.util.Queue;

public class waiting extends Fragment {
    RelativeLayout Layout = null;
    TMapView tmapview = null;
    String mapKey = "391d6fad-39f4-33f9-9add-de8fca4240b2";
    double latitude;
    double longitude;
    double search_latitude,search_longitude;
    String id;
    Button submit=null;
    EditText et=null;
    ClientSocket sk1,sk2,sk3,sk4;
    Queue<ObjectTable> queue3;
    String serial_number,position,goback;
    TMapPoint poi;
    public waiting(){}
    public waiting(double search_longitude,double search_latitude,double longitude,double latitude,String id,String position,String goback) {
        if(search_latitude==0.0){
            this.search_latitude=latitude;
            this.search_longitude=longitude;
        }
        else{
            this.search_latitude=search_latitude;
            this.search_longitude=search_longitude;
        }

        this.id=id;
        this.position=position;
        this.goback=goback;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = (View)inflater.inflate(R.layout.activity_waiting,container,false);
        ImageView v = (ImageView)rootview.findViewById(R.id.loading);
        GlideDrawableImageViewTarget ImageViewTarget = new GlideDrawableImageViewTarget(v);
        Glide.with(getActivity()).load(R.raw.loding).into(v);
        sk1 = new ClientSocket(getActivity());
        BOARDING_PASS bp = new BOARDING_PASS();
        bp.setOrderOperation("SELECT");
        bp.setSelectRequest("select * from boarding_pass where carpool_serial_number like '%/"+position+"/"+goback+"' and OCCUPANT_ID = '"+id+"';");
        Log.v("eeeeeee",bp.getSelectRequest());
        sk1.setObj((ObjectTable)bp);
        sk1.start();
        while (sk1.flag){
            Log.d("TAG3","답장대기가계속");
        }
        queue3 = sk1.getQueue2();
        sk1=null;
        bp = (BOARDING_PASS)queue3.poll();
        Log.d("ddddddd",""+bp.getKAPUL_ACCESS_POINT_SERIAL_NUMBER());
        serial_number = bp.getKAPUL_ACCESS_POINT_SERIAL_NUMBER();
        sk2 = new ClientSocket(getActivity());
        ACCESS_POINT_MANAGEMENT apm = new ACCESS_POINT_MANAGEMENT();
        apm.setOrderOperation("SELECT");
        apm.setSelectRequest("select * from access_point_management where kapul_access_point_serial_number = '"+serial_number+"';");
        Log.v("aaaaaaa",""+apm.getSelectRequest());
        sk2.setObj((ObjectTable)apm);
        sk2.start();
        while (sk2.flag){
            Log.d("TAG3","탐색계속");
        }
        queue3 = sk2.getQueue2();
        sk2=null;
        apm = (ACCESS_POINT_MANAGEMENT)queue3.poll();
        poi = new TMapPoint(apm.getACCESS_POINT_LATITUDE(),apm.getACCESS_POINT_LONGITUDE());
        this.latitude=apm.getACCESS_POINT_LATITUDE();
        this.longitude=apm.getACCESS_POINT_LONGITUDE();
        Layout = (RelativeLayout)rootview.findViewById(R.id.container);
        tmapview = new TMapView(getActivity());
        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);
        tmapview.setCenterPoint(longitude,latitude);
        tmapview.setLocationPoint(longitude,latitude);
        tmapview.setIconVisibility(false);
        Layout.addView(tmapview);
        et = (EditText)rootview.findViewById(R.id.sendToDriver);
        submit = (Button)rootview.findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                et.setText("");
            }
        });
        TMapMarkerItem APmarker = new TMapMarkerItem();
        APmarker.setTMapPoint(poi);
        APmarker.setVisible(APmarker.VISIBLE);
        APmarker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        APmarker.setCalloutTitle(apm.getCARPOOL_DESCRIPTION());
        Time time = apm.getACCESS_POINT_TIME_OF_ARRIVAL();
        String stm = time.toString();
        APmarker.setCalloutSubTitle(stm);
        Bitmap bitmap_i = BitmapFactory.decodeResource(getActivity().getResources(),R.mipmap.carpool_car);
        APmarker.setCalloutRightButtonImage(bitmap_i);
        String serial = apm.getCARPOOL_SERIAL_NUMBER();
        String[] driver_name = serial.split("/");
        TextView tv = (TextView)rootview.findViewById(R.id.driverName);
        tv.setText(driver_name[0]+" 님에게");
        final String ap_serial = apm.getKAPUL_ACCESS_POINT_SERIAL_NUMBER();
        tmapview.addMarkerItem(String.format("id"), APmarker);
        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                mOnClick(markerItem.getCalloutSubTitle(),markerItem.getCalloutTitle(),ap_serial);
            }
        });
        return rootview;
    };
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void mOnClick(String ti, String lo, final String serial){
        String[] serial_bunhal = serial.split("/");
        sk4 = new ClientSocket(getActivity());
        USER user = new USER();
        user.setOrderOperation("SELECT");
        user.setSelectRequest("select * from user where id = '"+serial_bunhal[0]+"';");
        sk4.setObj((ObjectTable)user);
        sk4.start();
        while (sk4.flag){
            Log.v("aaa","이름검색");
        }
        queue3 = sk4.getQueue2();
        sk4=null;
        user = (USER)queue3.poll();
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.waiting_info,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final String[] serial_number = serial.split("/");
        TextView name = (TextView)dialog.findViewById(R.id.info_name);
        name.setText(user.getNAME());
        TextView time = (TextView)dialog.findViewById(R.id.arrival_time);
        time.setText(ti);
        TextView ap_location= (TextView)dialog.findViewById(R.id.info_location);
        ap_location.setText(lo);
        Button ok = (Button)dialog.findViewById(R.id.info_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button cancel = (Button)dialog.findViewById(R.id.info_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sk3 = new ClientSocket(getActivity());
                BOARDING_PASS bp = new BOARDING_PASS();
                bp.setOrderOperation("DELETE");
                bp.setDeleteRequest("delete from boarding_pass where kapul_access_point_serial_number ='"+serial+"';");
                Log.v("code1",""+bp.getDeleteRequest());
                sk3.setObj((ObjectTable)bp);
                sk3.start();
                while (sk3.flag){
                    Log.d("TAG3","범위탐색이계속");
                }
                sk3=null;
                Toast.makeText(getActivity(),"카풀 신청을 취소하였습니다.",Toast.LENGTH_SHORT).show();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentBorC, new search(search_longitude,search_latitude,id,position,goback));
                fragmentTransaction.commit();
                dialog.dismiss();
            }
        });
    }
}
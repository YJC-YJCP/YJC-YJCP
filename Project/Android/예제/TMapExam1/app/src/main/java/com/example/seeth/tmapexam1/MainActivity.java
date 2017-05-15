package com.example.seeth.tmapexam1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements TMapView.OnLongClickListenerCallback {

    private LinearLayout Layout;
    private TMapView tmapview;
    private String mapKey = "795385d9-f3d0-3d51-abe5-bbb0c6c82258";
    private TMapData tmapdata = new TMapData();

    private Context mContext = null;

    private double latitude;     //경도
    private double longitude;     //위도

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        Layout = new LinearLayout(this);
        tmapview = new TMapView(this);

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.my_location);

        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        tmapview.setLocationPoint(128.49894, 35.87436); //현재 내 위치
        tmapview.setCenterPoint(128.49894, 35.87436);   //화면 시작지점
        tmapview.setIcon(bitmap);

        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback()
        {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) { //AP 선택 시 오른쪽 이미지를 클릭할 때 이벤트
                Toast.makeText(MainActivity.this, "클릭", Toast.LENGTH_SHORT).show();
            }
        });

        Layout.addView(tmapview);
        setContentView(Layout);
    }

    public void addMarker(double Latitude, double Longitude) {//지도에 마커 추가

        TMapPoint poi = new TMapPoint(Latitude, Longitude);
        TMapMarkerItem APmarker = new TMapMarkerItem();
        Bitmap bitmap = null;

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int min = now.get(Calendar.MINUTE);
        int sec = now.get(Calendar.SECOND);
        String nowTime = "현시간 : " + hour + "시 " + min + "분 " + sec + "초";

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.add_marker);
        //add_marker는 지도에 추가할 마커 이미지입니다.
        APmarker.setTMapPoint(poi);
        APmarker.setName("테스트");
        APmarker.setVisible(APmarker.VISIBLE);
        APmarker.setIcon(bitmap);
        APmarker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        APmarker.setCalloutTitle("해당 장소의 이름");
        APmarker.setCalloutSubTitle(nowTime);       //풍선뷰 보조메세지
//        tItem.setCalloutLeftImage(bitmap);  //풍선뷰의 왼쪽 이미지 지정 //오른쪽은 RIGHT

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.carpool_car);
        APmarker.setCalloutRightButtonImage(bitmap);

        tmapview.addMarkerItem("테스트", APmarker);
    }

    @Override
    public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint) {
        latitude = Math.round(tMapPoint.getLatitude()*10000)/10000.0;     //경도 4자리 반올림
        longitude = Math.round(tMapPoint.getLongitude()*10000)/10000.0;     //위도 4자리 반올림
        String add = "경도 : " + latitude + "\n위도 : " + longitude;

        addMarker(latitude, longitude);
        Toast.makeText(MainActivity.this, add, Toast.LENGTH_SHORT).show();
    }
}

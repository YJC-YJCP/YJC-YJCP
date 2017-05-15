package com.example.seeth.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

public class MapSearch extends AppCompatActivity implements View.OnClickListener {
    static final String TAG = "hoit";
    private TMapPoint point;
    private ArrayList<TMapPoint> passList; // 경유지 좌표를 저장할 리스트 선언
    private double lat;
    private double lon;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_search);//XML 레이아웃에 정의된 뷰들을 메모리상에 객체화 하는 역할을 수행하는 메소드

        Button locationSearch = (Button)findViewById(R.id.locationSearch);
        locationSearch.setOnClickListener(this);

        RelativeLayout relativelayout= (RelativeLayout) findViewById(R.id.mapSearchTMap);
        TMapData tmapdata = new TMapData();

        TMapView tmapview = new TMapView(this);
        tmapview.setSKPMapApiKey("b9c365d5-6d93-385f-bf50-0b423ece22c1");
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(16);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);


        Intent i = getIntent();
        // 검색값으로 넘어온 위도, 경도값 리스트에 저장
        lat = i.getExtras().getDouble("lat");
        lon = i.getExtras().getDouble("lon");
        tmapview.setCenterPoint( lon, lat);
        tmapview.setLocationPoint( lon, lat);

//        HashMap<String, String> pathInfo = new HashMap<String, String>();
//        pathInfo.put("rStName", "T Tower");
//        pathInfo.put("rStlat", Double.toString(37.566474));
//        pathInfo.put("rStlon", Double.toString(126.985022));
//        pathInfo.put("rGoName", "신도림");
//        pathInfo.put("rGolat", "37.50861147");
//        pathInfo.put("rGolon", "126.8911457");
//        pathInfo.put("type", "arrival");
        relativelayout.addView(tmapview);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.locationSearch:
                Intent i = new Intent(this, CarpoolUploadMain.class);
                i.putExtra("lat", lat);
                i.putExtra("lon", lon);
                setResult(RESULT_OK, i);

                finish();
            default:
                break;
        }
    }
}

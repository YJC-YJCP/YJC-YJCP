package com.example.seeth.car;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

/**
 * Created by seeth on 2017-05-13.
 */

public class CarpoolLocationSearch extends AppCompatActivity {
    private TMapView tmapview;
    private String mapKey = "795385d9-f3d0-3d51-abe5-bbb0c6c82258";
    private TMapData tmapdata;
    private TMapPoint point;
    private ArrayList<TMapPoint> passList; // 경유지 좌표를 저장할 리스트 선언
    private Context mContext;
    private double lat;
    private double lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carpool_location_search);

        passList = new ArrayList<TMapPoint>(); // 리스트 객체 생성

    }

    public void serachLocation(View v) {
        tmapdata = new TMapData();
        EditText locationSearch = (EditText)findViewById(R.id.locationSearch);
        String str= String.valueOf(locationSearch.getText());

        tmapdata.findAllPOI(str,1, new TMapData.FindAllPOIListenerCallback(){ // POI검색수 1개로 제한
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                for (int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = poiItem.get(i);
                    lat = item.getPOIPoint().getLatitude(); // 검색한 poi 경도 값 저장
                    lon = item.getPOIPoint().getLongitude(); // 검색한 poi 위도값 저장
                    point = new TMapPoint(lat, lon);
                }
            }
        });
    }

    public void searchMapClick(View v){
        mContext = this;
        Intent i = new Intent(mContext, MapSearch.class);
        i.putExtra("lat", lat);
        i.putExtra("lon", lon);
        i.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(i);
        finish();
    }
}

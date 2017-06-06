package com.example.seeth.car;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class driver_start extends AppCompatActivity
        implements TMapGpsManager.onLocationChangedCallback {

    private TMapView tmapview;
    private String mapKey = "795385d9-f3d0-3d51-abe5-bbb0c6c82258";
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

    private static final int FONT_SIZE = 25;
    /*
   현구 수정
     */
    USER user = null;

    ClientSocket sk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_start);
        sk=new ClientSocket(this);
        mContext = this;
        //현재위치를 탐색하는 메소드


        /* 릴레이티브 레이아웃에 지도를 출력 */
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.Tmap);
        // 텍스트뷰를 동적으로 생성하기 위한 레이아웃
        LinearLayout container = (LinearLayout) findViewById(R.id.textContainer);

        //경로를 생성하는 메소드
        addRoute("경로A", FONT_SIZE, container);
        addRoute("경로B", FONT_SIZE, container);

        tmapview = new TMapView(this);
        tmapview.setTrackingMode(true);
        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        TMapGpsManager gps = new TMapGpsManager(this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.NETWORK_PROVIDER);
        Location location = new Location("My Location");
        location.setLatitude(gps.getLocation().getLatitude());
        location.setLongitude(gps.getLocation().getLongitude());
        onLocationChange(location);
        layout.addView(tmapview);
    }

    //동적으로 텍스트뷰를 생성하기 위한 메소드
    public void addRoute(String routeName, int FONT_SIZE, LinearLayout container) {
        //TextView 생성
        TextView view1 = new TextView(this);
        view1.setText(routeName);
        view1.setTextSize(FONT_SIZE);
        view1.setTextColor(Color.BLACK);
        view1.setPadding(10,5,10,5);

        //layout_width, layout_height, gravity 설정
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        view1.setLayoutParams(lp);

        //부모 뷰에 추가
        container.addView(view1);

    }

    @Override
    public void onLocationChange(Location location) {
        tmapview.setLocationPoint(location.getLongitude(),location.getLatitude()); //현재 내 위치
        tmapview.setCenterPoint(location.getLongitude(),location.getLatitude());   //화면 시작지점

    }

}

class driverState extends Thread{
    CARPOOL_PATH_MANAGEMENT cp;
    ClientSocket sk;
    Context co;
    TMapView tView;
    TextView tv;
    String totalString;
    LinearLayout container ;
    public driverState(Context con,TMapView tView,LinearLayout container,TextView tv,String totalString){
        cp= new CARPOOL_PATH_MANAGEMENT();
        this.tView=tView;
        this.container=container;
        this.co=con;
        this.tv=tv;
        this.totalString=totalString;
        cp.setOrderOperation("UPDATE");
        cp.setUpdateRequest("");
        //update ABCDE set column1='xyz' where no='3'
        //'ABCDE' 테이블의
        //'column1' 컬럼 값을 'xyz' 으로 수정한다.
       // 수정대상은 'no' 컬럼값이 '3' 인 레코드 전부이다.

    }
}
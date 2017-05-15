package com.example.seeth.car;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

import java.util.ArrayList;
import java.util.Calendar;

public class CarpoolUploadMain extends AppCompatActivity implements TMapView.OnLongClickListenerCallback{
    private int transferID = 0;
    private int countTransfer=0;
    private int carpoolJoinCount=0;

    private double lat;
    private double lon;
    private String address;
    private Context mContext;
    private Bundle bundle;
    private double[] makerLat = new double[5];
    private double[] makerLon = new double[5];

    private TMapView tmapview;
    private TMapData tmapdata;
    private EditText keywordView;
    ArrayAdapter<POI> mAdapter;     //대량의 POI저장 가능한 ArrayAdapter  mAdapter.add(POI) = 리스트
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>(); //마커의 위치정보를 저장하는 배열(DB연동)
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>(); //마커의 중복을 허용하기 위한 마커의 id

    private static int mMarkerID;

    final static int CODE=1;
    final static int CODE1=2;

    public class POI {
        TMapPOIItem item;   // 4.8 POI정보 관리하는 클래스 (POI의 ID,이름,전화번호 위도 경도)
        public POI(TMapPOIItem item){
            this.item = item;
        }   //생성자
        @Override
        public String toString() {
            return item.getPOIName();
        }   //POI의 이름
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carpool_upload_main);//XML 레이아웃에 정의된 뷰들을 메모리상에 객체화 하는 역할을 수행하는 메소드

        RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.uploadMainTmap);

        TMapGpsManager gps = new TMapGpsManager(CarpoolUploadMain.this);
        gps.setMinDistance(0);
        gps.setMinTime(1000);
        gps.setProvider(gps.GPS_PROVIDER);
        gps.OpenGps();

        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TMapPoint startpoint = new TMapPoint(37.5248, 126.93); // 출발지 좌표
        TMapPoint endpoint = new TMapPoint(37.4601, 128.0428); // 목적지 좌표

        tmapview = new TMapView(this);
        tmapview.setSKPMapApiKey("b9c365d5-6d93-385f-bf50-0b423ece22c1");
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setCompassMode(true);
        tmapview.setTrackingMode(true);

        relativelayout.addView(tmapview);

        //클릭한 좌표값 출력
//        tmapview.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
//            @Override
//            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
//                return false;
//            }
//
//            @Override
//            public boolean onPressEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
//
//                Toast toast = Toast.makeText(getApplicationContext(), "클릭한 좌표: " + pointf.toString(), Toast.LENGTH_LONG);
//                toast.show();
//                // - Markerlist : 클릭된 마커들
//                // - Poilist : 클릭된 POI 들
//                // - Point : 화면좌표값을 위도, 경도로 반환한 값
//                // - Pointf : 화면좌표값
//                return true;
//            }
//        });
//        Log.i("전달유무", lat + " // "+ lon);
    }

    //경유지 추가버튼 이벤트
    public void transferAddClick(View v) {
        LinearLayout addLocation = (LinearLayout)findViewById(R.id.addRouteLayout);
        addTransferLocation("테스트", addLocation);
    }

    //출발지 추가버튼 이벤트
    public void sourceClick(View v) {
        Intent i = new Intent(this, CarpoolLocationSearch.class);
        //호출한 액티비티에서 결과값을 받아오는 경우
        startActivityForResult(i, CODE);
    }

    //서브액티비티가 값을 보내오면 자동호출되는 메소드
    //requestCode에 메인 액티비티에서 보낸 CODE가 담겨온다.
    //onActivityResult( 메인액티비티에서 보낸 코드,서브액티비티에서 보내온 리절트코드,서브액티비티에서 데이터를 담아 보낸 인텐트 )
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case CODE:
                if(resultCode==RESULT_OK) {//서브Activity에서 보내온 resultCode와 비교
                    //서브액티비티에서 인텐트에 담아온 정보 꺼내기
                    lat = data.getExtras().getDouble("lat");
                    lon = data.getExtras().getDouble("lon");
                    tmapview.setCenterPoint(lon, lat);
                    tmapview.setLocationPoint(lon, lat);
                    Log.i("전달유무", "1 전달받은 lat : " +  lat + " // lon : " + lon);
                    //
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
                    alt_bld.setMessage("탑승자와의 카풀탑승 지점을\n선택해주세요!\n(원하는 지점을 꾹 누르면 위치가 등록됩니다)")
                            .setCancelable(
                            false).setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Action for 'Yes' Button   @Override
                                }
                            }).setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alt_bld.create();
                    // Title for AlertDialog
                    alert.setTitle("카풀지점 등록");
                    alert.show();
                    //글자 크기 및 위치 조절
                    TextView text = (TextView)alert.findViewById(android.R.id.message);
                    text.setTextSize(22);
                    text.setGravity(Gravity.CENTER);
                    //

                    //경로를 불러올 때 버튼색 변경
                    Button successButton = (Button)findViewById(R.id.success);
                    successButton.setBackgroundColor(Color.parseColor("#AA1212"));
                    successButton.setTextColor(Color.WHITE);
                }
                break;
        }

        //end경로는 영진전문대학으로 고정
        TMapPoint end = new TMapPoint(35.894573, 128.621654);
        TMapPoint start = new TMapPoint( lat,lon);
        searchRoute(start, end);
    }

    //등록완료 버튼 이벤트
    public void successButtonClicked(View v){
        final AlertDialog.Builder ratingdialog = new AlertDialog.Builder(this);

        final Context mContext = this;
        ratingdialog.setIcon(getResources().getDrawable(R.drawable.per1));
        ratingdialog.setTitle("탑승인원지정");

        View linearlayout = getLayoutInflater().inflate(R.layout.rating_dialog, null);
        ratingdialog.setView(linearlayout);

        //탑승인원지정
        final RatingBar rating = (RatingBar)linearlayout.findViewById(R.id.ratingbar);
        ratingdialog.setPositiveButton("등록",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.putExtra("lat", lat);
                        intent.putExtra("lon", lon);
                        intent.putExtra("makerLat", makerLat);
                        intent.putExtra("makerLon", makerLon);
                        setResult(RESULT_OK, intent);
                        finish();
                        Log.i("전달유무", "마커 위치 배열 인텐트로 전달 성공");
                    }
                })
                .setNegativeButton("이전",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        ratingdialog.create();
        ratingdialog.show();

    }

    //화면에 마커를 표시기 위한 메소드
    public void addMarker(double Latitude, double Longitude) {//지도에 마커 추가
        TMapPoint poi = new TMapPoint(Latitude, Longitude);
        TMapMarkerItem APmarker = new TMapMarkerItem();
        Bitmap bitmap = null;
        mContext = this;

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int min = now.get(Calendar.MINUTE);
        int sec = now.get(Calendar.SECOND);
        String nowTime = hour + "시 " + min + "분 " + sec + "초";

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

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.car);
        APmarker.setCalloutRightButtonImage(bitmap);

        String strID = String.format("pmarker%d", mMarkerID++);
        tmapview.addMarkerItem(strID, APmarker);
        mArrayMarkerID.add(strID);

        //풍선뷰 선택할 때 나타나는 이벤트
        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {

                lat = markerItem.latitude;
                lon = markerItem.longitude;

                //위도, 경도로 주소 검색하기
                tmapdata.convertGpsToAddress(lat, lon, new TMapData.ConvertGPSToAddressListenerCallback() {
                    @Override
                    public void onConvertToGPSToAddress(String strAddress) {
                        address = strAddress;
                    }
                });
                Toast.makeText(mContext, "주소 : " + address, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //길게 누를때 이벤트
    @Override
    public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint) {
        double lat = Math.round(tMapPoint.getLatitude()*10000)/10000.0;     //경도 4자리 반올림
        double lon = Math.round(tMapPoint.getLongitude()*10000)/10000.0;     //위도 4자리 반올림

        if(carpoolJoinCount<=3) {
            m_mapPoint.add(new MapPoint(lat, lon));
            makerLat[carpoolJoinCount] = lat;
            makerLon[carpoolJoinCount] = lon;
            for (int i = 0; i < m_mapPoint.size(); i++) {
                addMarker(m_mapPoint.get(i).getLatitude(), m_mapPoint.get(i).getLongitude());
                carpoolJoinCount++;
            }
        }
        else {
            Toast.makeText(this, "카풀 조인 지점은 최대 3개까지입니다.", Toast.LENGTH_SHORT).show();
        }
        Log.i("전달유무", "맵포인트사이즈 : " + m_mapPoint.size());
    }

    //주소 검색 메소드
    public void locationSearch(double lat, double lon){
        tmapdata = new TMapData();
        //위도, 경도로 주소 검색하기
            tmapdata.convertGpsToAddress(lat, lon, new TMapData.ConvertGPSToAddressListenerCallback() {
                @Override
                public void onConvertToGPSToAddress(String strAddress) {
                    address = strAddress;
                }
            });
        Toast.makeText(CarpoolUploadMain.this, "주소 : " + address, Toast.LENGTH_SHORT).show();
    }

    //경로 출력 메소드
    public void searchRoute(TMapPoint start, TMapPoint end){
        tmapdata = new TMapData();
        tmapview.setZoom(17);

        //철영이 경로 띄우기
        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end,
                new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        tmapview.addTMapPath(polyLine);
                    }
                });
    }

    //경유지 추가버튼을 누르면 추가할 경유지를 생성하기 위한 메소드
    public void addTransferLocation(String routeName, LinearLayout container) {
        if(countTransfer<3) {
            //EditText 생성
            EditText addLocation = new EditText(this);
            addLocation.setHint(routeName);
            addLocation.setTextColor(Color.BLACK);
            addLocation.setId(transferID);

            //layout_width, layout_height, gravity 설정
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            addLocation.setLayoutParams(lp);

            //부모 뷰에 추가
            container.addView(addLocation);
            transferID++;
        }
        else {
            Toast.makeText(this, "경유지는 최대 3개까지 설정가능합니다.", Toast.LENGTH_SHORT).show();
        }
        countTransfer++;

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

}

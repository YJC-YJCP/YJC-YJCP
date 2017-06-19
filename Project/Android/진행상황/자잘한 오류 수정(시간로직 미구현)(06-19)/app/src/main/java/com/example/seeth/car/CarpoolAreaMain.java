package com.example.seeth.car;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapAddressInfo;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapMarkerItem2;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

public class CarpoolAreaMain extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int mMarkerID=0;    //마커를 구별하기 위한 ID
    int carpoolJoinCount=0;     //AP지점을 최대 3개까지 카운트하기위한 변수
    ArrayList<String> mArrayMarkerID = new ArrayList<String>(); //마커의 중복을 허용하기 위한 마커의 id
    private Context mContext;
    Date currentTime = new Date();
    Date tempcurrentTime1 = new Date();
    TMapData tmapdata = null;
    //데이터베이스 사용을 위한 클래스 선언
    ArrayList<TPoint> locationArray =null;
    ArrayList<TMapPoint> passList=null;
    ArrayList<Spinner> spinners = null;
    ArrayList<String> example=null;
    private TMapView tmapview;
    TMapPoint start= null;
    TMapPoint end =null;
    RelativeLayout tmap=null;
    Spinner sp=null;
    String[] str=null;
    String Addr=null;
    TPoint Tstart;
    TPoint TEnd;
    int fi;
    int cnt=0;
    String state;
    Button successButton=null;
    private String mapKey = "c2b1ca96-9e28-3cc7-8697-092c86b91e5d";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carpool_area_main);//XML 레이아웃에 정의된 뷰들을 메모리상에 객체화 하는 역할을 수행하는 메소드
        //===========초기화 하는 부분
        successButton=(Button)findViewById(R.id.success);
        successButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CarpoolAreaMain.this);
                // 제목셋팅
                alertDialogBuilder.setTitle("카풀 지점 등록");
                // AlertDialog 셋팅
                alertDialogBuilder
                        .setMessage("등록을 완료하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent resultIntent = new Intent();
                                TPoint tpoint = new TPoint(0, 0);
                                tpoint.mp= new ArrayList<TPoint>(locationArray);
                                resultIntent.putExtra("locationArray",tpoint);
                                setResult(RESULT_OK, resultIntent);
                                finish();

                            }
                        })
                        .setNegativeButton("아니오",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // 다이얼로그를 취소한다
                                        dialog.cancel();
                                    }
                                });

                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();
                // 다이얼로그 보여주기
                alertDialog.show();
            }
        });
        // LinearLayout ll = (LinearLayout) findViewById(R.id.listLayout);
        //----------이전 화면에서 출발지-경유지-목적지를 불러온다.
        Intent it = this.getIntent();
        TPoint tt= (TPoint) it.getSerializableExtra("locationArray");
        state = (String)it.getStringExtra("state");
        currentTime = tt.time;
        Log.d("TAG3",""+currentTime.toString());
        locationArray =new ArrayList<>(tt.mp);
        settingSpinner(locationArray);
        tmapdata= new TMapData();
        tmapview = new TMapView(this);
        tmapSet(tmapview);


    }
    public void settingSpinner(ArrayList<TPoint> locationArray2){
        example=new ArrayList<>();
        this.locationArray = locationArray2;
        spinners=new ArrayList<>();
        passList=new ArrayList<>();
        if(locationArray.size()==2){
            String str =locationArray.get(0).description;
            str=str+" - "+locationArray.get(1).description;
            example.add(str);
        }
        else if(locationArray.size()>2) {
            for (int i = 0; i < locationArray.size()-1; i++) {
                String str = locationArray.get(i).description;
                str = str + " - " + locationArray.get(i + 1).description;
                example.add(str);
            }
        }
        example.add(0,"전체경로출력");
        sp= (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, example);
        //ll.setOrientation(LinearLayout.VERTICAL);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);
        sp.setSelection(1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
public void stateTime(){
    if(state.equals("등교"))
    {
        TPoint st = null;
        TPoint ed = null;
        if (locationArray.size() > 1) {
            for(int i=locationArray.size()-1;i>=0;i--) {
                if (i == locationArray.size()-1) {
                    st = locationArray.get(i-1);
                    ed= locationArray.get(i);
                    timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                } else if (i > 0 && i < (locationArray.size() - 1)) {
                    ed = locationArray.get(i);
                    st = locationArray.get(i-1);
                    timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                } else if (i ==0) {
                    st =locationArray.get(i);
                    ed =locationArray.get(i+1);
                    timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                }
            }
        }
    }else if(state.equals("하교")){
        TPoint st = null;
        TPoint ed = null;
        if (locationArray.size() > 1) {
            for(int i=0;i<locationArray.size();i++) {
                if (i == 0) {
                    st =locationArray.get(i);
                    ed = locationArray.get(i+1);
                    timeSelect(st, ed,currentTime);//timeSelect(final TPoint start, final TPoint end);이게 등교
                } else if (i > 0 && i < (locationArray.size() - 1)) {
                    st = locationArray.get(i);
                    ed = locationArray.get(i+1);
                    timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                } else if (i == (locationArray.size() - 1)) {
                    st = locationArray.get(i-1);
                    ed = locationArray.get(i);
                    timeSelect(st, ed,tempcurrentTime1);//timeSelect(final TPoint start, final TPoint end);이게 등교
                }
            }
        }
    }
}

    public void tmapSet(final TMapView tmapview) {
        tmap = (RelativeLayout)findViewById(R.id.mapview);
        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(8);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        tmapview.setLocationPoint(128.62196445465088,35.89601207015857 );
        tmapview.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, final TMapPoint point) {
                if(start!=null&&end!=null) {

                    tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(),
                            new TMapData.ConvertGPSToAddressListenerCallback() {
                                @Override
                                public void onConvertToGPSToAddress(String strAddress) {
                                    Addr = strAddress;
                                }
                            });

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(CarpoolAreaMain.this);

//
                    if (Addr != null)
                        dialog.setMessage(Addr + "을 카풀 지점으로 등록하시겠습니까?");
                    else
                        dialog.setMessage("카풀 지점으로 등록하시겠습니까?");

                    View linearlayout = getLayoutInflater().inflate(R.layout.car_description, null);
                    dialog.setView(linearlayout);

                    //탑승인원지정
                    final EditText edt = (EditText) linearlayout.findViewById(R.id.description);

                    dialog.setPositiveButton("등록",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    TPoint tempPoint = new TPoint(point.getLatitude(), point.getLongitude());
                                    tempPoint.flag = "카풀";

                                    cnt++;
                                    if (Addr != null)
                                        tempPoint.description=Addr;
                                    else
                                        tempPoint.description = "임의카풀지점";

                                    tempPoint.description = edt.getText().toString();
                                    locationArray.add(fi + 1, tempPoint);
                                    for(int i=0;i<locationArray.size();i++)
                                    {
                                        locationArray.get(i).hour=null;
                                        locationArray.get(i).minute=null;
                                        locationArray.get(i).second=null;
                                    }

                                    stateTime();
                                    settingSpinner(locationArray);
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("이전",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    dialog.create();
                    dialog.show();
                }
            }
        });
        tmap.addView(tmapview);
        }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tmapview.removeAllTMapPolyLine();
        tmapview.removeAllMarkerItem();
        String selectExam =  (String)sp.getSelectedItem();
        start= new TMapPoint(0,0);
        end = new TMapPoint(0,0);
        if(selectExam.equals("전체경로출력")) {
            stateTime();
            ArrayList<TMapPoint> passList = new ArrayList<>();
            start= new TMapPoint(locationArray.get(0).latitude,locationArray.get(0).longitude);
            end= new TMapPoint(locationArray.get(locationArray.size()-1).latitude,locationArray.get(locationArray.size()-1).longitude);
            tmapview.zoomToTMapPoint(start, end );
            TPoint tpd = locationArray.get(0);
            addMarker(tpd,tpd.hour,tpd.minute,tpd.second);
            tpd=locationArray.get(locationArray.size()-1);
            addMarker(tpd,tpd.hour,tpd.minute,tpd.second);
            if(locationArray.size()>2) {
                for (int i = 1; i <= locationArray.size() - 2; i++) {
                    TMapPoint tmp = new TMapPoint(locationArray.get(i).latitude, locationArray.get(i).longitude);
                    TPoint tp = locationArray.get(i);
                    addMarker(tp,tp.hour,tp.minute,tp.second);
                    passList.add(tmp);
                }
                tmapview.refreshMap();
                tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end, passList, 0,
                        new TMapData.FindPathDataListenerCallback() {
                            @Override
                            public void onFindPathData(TMapPolyLine polyLine) {
                                tmapview.addTMapPath(polyLine);
                                tmapview.refreshMap();
                            }
                        });
            }else{
                tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end,
                        new TMapData.FindPathDataListenerCallback() {
                            @Override
                            public void onFindPathData(TMapPolyLine polyLine) {
                                tmapview.addTMapPath(polyLine);
                                tmapview.refreshMap();
                            }
                        });
            }
        }
        else {
           stateTime();
            str = selectExam.split(" - ");
            if (locationArray.size() == 2) {
                TPoint tp = locationArray.get(0);
                start.setLatitude(tp.latitude);
                start.setLongitude(tp.longitude);
                fi = 0;
                tp = locationArray.get(1);
                end.setLatitude(tp.latitude);
                end.setLongitude(tp.longitude);
            } else if (locationArray.size() > 2) {
                for (int i = 0; i < locationArray.size(); i++) {
                    TPoint tp = locationArray.get(i);
                    String stp = tp.description;
                    if (stp.equals(str[0])) {
                        fi = i;
                        start.setLatitude(tp.latitude);
                        start.setLongitude(tp.longitude);
                        addMarker(tp, tp.hour, tp.minute, tp.second);
                        tmapview.refreshMap();
                    }
                    if (stp.equals(str[1])) {
                        end.setLatitude(tp.latitude);
                        end.setLongitude(tp.longitude);
                        addMarker(tp, tp.hour, tp.minute, tp.second);
                        tmapview.refreshMap();
                    }
                }
            }
            tmapview.removeAllTMapPolyLine();
            tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            tmapview.addTMapPath(polyLine);
                            tmapview.refreshMap();
                        }
                    });
        }

    }



    public void timeSelect(final TPoint start, final TPoint end, final Date date)
    {
        if(state.equals("등교")){
            HashMap<String, String> pathInfo = new HashMap<String, String>();
            pathInfo.put("rStName", start.description);//비어있다.
            pathInfo.put("rStlat", String.valueOf(start.latitude));
            pathInfo.put("rStlon", String.valueOf(start.longitude));
            pathInfo.put("rGoName",end.description);
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
                            end.minute= String.valueOf(minutes);
                            end.second=String.valueOf(second);
                            hour= hour*3600;
                            minutes=minutes*60;
                            int startTime = hour+minutes+second;
                            tempcurrentTime1.setHours((startTime-totalTime)/ 3600);
                            tempcurrentTime1.setMinutes((startTime-totalTime) % 3600 / 60);
                            tempcurrentTime1.setSeconds((startTime-totalTime) % 3600 % 60);
                            start.hour=String.valueOf((startTime-totalTime)/ 3600);//도착시간
                            start.minute= String.valueOf((startTime-totalTime) % 3600 / 60);
                            start.second= String.valueOf((startTime-totalTime) % 3600 % 60);
                        }
                    });
        }
        else if(state.equals("하교"))
        {
            HashMap<String, String> pathInfo = new HashMap<String, String>();
            pathInfo.put("rStName", start.description);//비어있다.
            pathInfo.put("rStlat", String.valueOf(start.latitude));
            pathInfo.put("rStlon", String.valueOf(start.longitude));
            pathInfo.put("rGoName",end.description);//,
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
                            start.minute= String.valueOf(minutes);
                            start.second=String.valueOf(second);
                            hour= hour*3600;
                            minutes=minutes*60;
                            int startTime = hour+minutes+second;
                            tempcurrentTime1.setHours((startTime+totalTime)/ 3600);
                            tempcurrentTime1.setMinutes((startTime+totalTime) % 3600 / 60);
                            tempcurrentTime1.setSeconds((startTime+totalTime) % 3600 % 60);


                            end.hour=String.valueOf((startTime+totalTime)/ 3600);//도착시간
                            end.minute= String.valueOf((startTime+totalTime) % 3600 / 60);
                            end.second= String.valueOf((startTime+totalTime) % 3600 % 60);

                        }
                    });
        }
    }
    public void addMarker(TPoint tp,String hour, String minutes, String seconds) {//지도에 마커 추가=String.valueOf((startTime+totalTime)/ 3600);//도착시간
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
        APmarker.setID(tp.description);
        APmarker.setAutoCalloutVisible(false);
        APmarker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        APmarker.setCalloutTitle(tp.description);
        APmarker.setCalloutSubTitle(nowTime);       //풍선뷰 보조메세지

//      tItem.setCalloutLeftImage(bitmap);  //풍선뷰의 왼쪽 이미지 지정 //오른쪽은 RIGHT

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.car);
        APmarker.setCalloutRightButtonImage(bitmap);

        String strID = tp.description;
        tmapview.addMarkerItem(strID, APmarker);
        mArrayMarkerID.add(strID);
        //풍선뷰 선택할 때 나타나는 이벤트
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

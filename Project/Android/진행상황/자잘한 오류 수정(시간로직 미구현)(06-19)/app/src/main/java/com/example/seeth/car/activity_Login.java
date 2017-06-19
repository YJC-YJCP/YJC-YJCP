package com.example.seeth.car;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.skp.Tmap.TMapGpsManager;

import java.util.Queue;

/*
이 자바소스는 로그인화면입니다.
 */
public class activity_Login extends AppCompatActivity {
    private EditText name;
    private String id_value;
    ClientSocket sk = null;

    TMapGpsManager gps = null;
    Context cc=this;
    String gpsEnabled;

    EditText pw;
    EditText id;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        chkGpsService();
        /**
         *  현재 사용자의 OS버전이 마시멜로우 인지 체크한다.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            /**
             *  사용자 단말기의 권한 중 전화걸기 권한이 허용되어 있는지 체크한다.
             */
            int permissionResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            // call_phong의 권한이 없을 떄
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                //  Package는 Android Application의 ID이다.
                /**
                 *  사용자가 CALL_PHONE 권한을 한번이라도 거부한 적이 있는지 조사한다.
                 *  거부한 이력이 한번이라도 있다면, true를 리턴한다.
                 *  거부한 이력이 없다면 false를 리턴한다.
                 */
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity_Login.this);
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("이 기능을 사용하기 위해서는 단말기의 \"위치\"권한이 필요합니다. 계속하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 위 리스너랑 다른 범위여서 마쉬멜로우인지 또 체크해주어야 한다.
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                                    }
                                }
                            })
                            .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(activity_Login.this, "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();

                }
                // 최초로 권한을 요청 할 때
                else {
                    // CALL_PHONE 권한을 안드로이드 OS에 요청합니다.
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                }
            }
            // call_phonne의 권한이 있을 떄
            else {
                start();
            }

        }
        // 사용자의 버전이 마시멜로우 이하일때
        else {
            start();
        }
    }
    public boolean chkGpsService() {

        //GPS가 켜져 있는지 확인함.
        gpsEnabled = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!(gpsEnabled.matches(".*gps.*") && gpsEnabled.matches(".*network.*"))) {
            //gps가 사용가능한 상태가 아니면
            new AlertDialog.Builder(this).setTitle("GPS 설정").setMessage("GPS가 꺼져 있습니다. \nGPS를 활성화 하시겠습니까?").setPositiveButton("GPS 켜기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    //GPS 설정 화면을 띄움
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }).setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).create().show();

        }else if((gpsEnabled.matches(".*gps.*") && gpsEnabled.matches(".*network.*"))) {

        }
        return false;
    }
    public void start() {
        pw = (EditText) findViewById(R.id.password_inupt);
        id = (EditText) findViewById(R.id.input_id);
        gps = new TMapGpsManager(this);
        gps.setMinTime(1000);
        gps.setMinDistance(0);
        gps.setProvider(gps.NETWORK_PROVIDER);
        gps.OpenGps();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 사용자 요청, 요청한 권한들, 응답들

        if (requestCode == 1000) {
            // 요청한 권한을 사용자가 허용했다면
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    start();
                }
            } else {
                Toast.makeText(activity_Login.this, "권한요청을 거부했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void join_membership(View V) {
        Intent abc = new Intent(activity_Login.this, membership.class);
        startActivity(abc);
    }

    public void next_main(View V) {
        sk = new ClientSocket(this);
        USER usr = new USER();
        usr.setID(id.getText().toString());
        usr.setPASSWORD(pw.getText().toString());
        usr.setOrderOperation("SELECT");
        usr.setSelectRequest("SELECT * FROM USER WHERE ID LIKE '"+usr.getID()+"'and PASSWORD LIKE '"+usr.getPASSWORD()+"';");
        Intent intent =new Intent(this, choice.class);
        ObjectTable obj = usr;
        sk.setObj(obj);
        sk.start();
        while(sk.flag){

        }
        Queue<ObjectTable> queue2 = sk.getQueue2();
        usr = (USER) queue2.poll();
        if(usr.getResultResponse())
        {
            intent.putExtra("USER",usr);
            intent.putExtra("latitude", gps.getLocation().getLatitude());
            intent.putExtra("longitude", gps.getLocation().getLongitude());
            CheckTypesTask task = new CheckTypesTask();
            task.execute();
            startActivity(intent);
        }
    }
    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog asyncDialog = new ProgressDialog(cc);
        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");
            asyncDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }
}



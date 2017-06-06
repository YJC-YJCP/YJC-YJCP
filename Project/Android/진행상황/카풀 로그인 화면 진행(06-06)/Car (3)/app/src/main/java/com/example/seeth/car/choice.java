package com.example.seeth.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/*
이 자바 소스는 드라이버,탑승자 선택입니다.
 */
public class choice extends AppCompatActivity {
    static final String TAG4 = "hoit_choice";
    USER user=null;
    ClientSocket sk=null;
    Bundle userBundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        Bundle userBundle = this.getIntent().getExtras();
        ArrayList<ObjectTable> obj= (ArrayList<ObjectTable>)userBundle.getSerializable("USER");
        user = (USER)obj.get(0);
        Button btn = (Button)findViewById(R.id.driver_button);
        if(user.isVEHICEL_OWNED()==false){
           btn.setEnabled(false);
        }




//        Queue<ObjectTable> queue =  (Queue<ObjectTable>) extras.getSerializable("USER");
//       this.getIntent().getExtras().get("USER");

     //


     //   if(objUser.isVEHICEL_OWNED()==false){
       //     Button btn = (Button)findViewById(R.id.driver_button);
       //     btn.setSelected(false);
       // }
    }

    public void user(View v) {
     //   Intent user = new Intent(this,user_main.class);
     //   startActivity(user);
    }

    public void driver(View v){
        Intent driver = new Intent(this,MainActivity.class);
        driver.putExtra("USER",userBundle);//USER 정보를 인탠트에 담자 , 다음화면에서 필요하기 때문에
        sk = new ClientSocket(this);//현재 이화면의 context객체를 넘기겠다.
        COMMUTING_TIME ct = new COMMUTING_TIME();//시간표 테이블을 조회하려는 용도로 객체 생성
        ct.setOrderOperation("SELECT");//테이블 접근 용도는 SELECT다.
        ct.setSelectRequest("SELECT * FROM COMMUTING_TIME WHERE ID LIKE '"+user.getID()+"';");//아이디로 검색
        sk.setIntent(driver);
        ObjectTable obj = ct;//참조할 객체선언
        sk.setObj(obj);// 그거를 소켓쪽에 큐로 담는 메소드로 넘긴다.
        sk.start();//소켓 스타트

    }
}

package com.example.seeth.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Queue;

/*
이 자바 소스는 드라이버,탑승자 선택입니다.
 */
public class choice extends AppCompatActivity {
    static final String TAG4 = "hoit_choice";
    USER user = null;
    Queue<ObjectTable> queue2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        Intent it = getIntent();
        user= (USER) it.getSerializableExtra("USER");

        Button btn = (Button)findViewById(R.id.driver_button);
        Log.d("TAG3",""+user.isVEHICEL_OWNED());
        if(user.isVEHICEL_OWNED()==true){
           btn.setEnabled(true);
        }


    }

    public void user(View v) {
      //     Intent user = new Intent(this,user_main.class);
    //      startActivity(user);
    }

    public void driver(View v){
        Intent driver = new Intent(this,MainActivity.class);
        driver.putExtra("USER",user);
        startActivity(driver);
    }
}
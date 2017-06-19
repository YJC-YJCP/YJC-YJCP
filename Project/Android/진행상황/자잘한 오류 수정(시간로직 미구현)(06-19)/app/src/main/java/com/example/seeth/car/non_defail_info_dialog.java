package com.example.seeth.car;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by tjehd on 2017-05-29.
 */

public class non_defail_info_dialog extends Dialog implements View.OnClickListener{

    TextView name,day,home_and_school,time,ap_position;
    Button yes_btn,no_btn;
    Context mcontext;
    String st_name,st_day,st_home_and_school,st_time,st_ap_position;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_btn_defail_info);
        name = (TextView)findViewById(R.id.defail_info_name);
        day = (TextView)findViewById(R.id.defail_info_day);
        home_and_school = (TextView)findViewById(R.id.home_and_school);
        time = (TextView)findViewById(R.id.defail_info_time);
        ap_position = (TextView)findViewById(R.id.defail_info_ap_position);

        name.setText(st_name);
        day.setText(st_day);
        home_and_school.setText(st_home_and_school);
        time.setText(st_time);
        ap_position.setText(st_ap_position);



//        no_btn.setOnClickListener((View.OnClickListener) mcontext);
    }

    public non_defail_info_dialog(@NonNull Context context) {
        super(context);
        mcontext = context;
    }

    public non_defail_info_dialog(@NonNull Context context, String name, String day, String home_and_school,String time, String ap_position ) {
        super(context);
        mcontext = context;

        st_name = name;
        st_day = day;
        st_home_and_school = home_and_school;
        st_time = time;
        st_ap_position = ap_position;
    }


    @Override
    public void onClick(View v) {
        if(v == yes_btn){

        }

    }
}

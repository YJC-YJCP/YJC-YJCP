package com.example.seeth.car;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.seeth.car.COMMUTING_TIME;
import com.example.seeth.car.ObjectTable;

/**
 * Created by parkjeongmi on 2017-06-06.
 */

public class time_select extends Fragment {
    View rootview=null;
    int home_flag=0,school_flag=0,home_h=0,home_m=0,school_h=0,school_m=0;
    Button home_h_up,home_h_down,home_m_up,home_m_down,school_h_up,school_h_down,school_m_up,school_m_down,home_time_text,school_time_text,home_h_text,home_m_text,school_h_text,school_m_text,submit;
    double latitude,longitude;
    String id,position,goback;
    ClientSocket cs;
    public time_select(double longitude,double latitude,String id,String position,String goback){
        this.latitude=latitude;
        this.longitude=longitude;
        this.id=id;
        this.position=position;
        this.goback=goback;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = (View)inflater.inflate(R.layout.activity_time_select,container,false);
        home_h_text = (Button)rootview.findViewById(R.id.gohome_h_text);
        home_m_text = (Button) rootview.findViewById(R.id.gohome_m_text);
        school_h_text = (Button) rootview.findViewById(R.id.goschool_h_text);
        school_m_text = (Button) rootview.findViewById(R.id.goschool_m_text);
        home_time_text = (Button)rootview.findViewById(R.id.gohome_button);
        home_time_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(home_flag==0){
                    home_time_text.setText("오후");
                    home_flag=1;
                }
                else{
                    home_time_text.setText("오전");
                    home_flag=0;
                }
            }
        });
        school_time_text = (Button)rootview.findViewById(R.id.goschool_button);
        school_time_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(school_flag==0){
                    school_time_text.setText("오후");
                    school_flag=1;
                }
                else{
                    school_time_text.setText("오전");
                    school_flag=0;
                }
            }
        });

        home_h_up = (Button)rootview.findViewById(R.id.gohome_h_up);
        home_h_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(home_h<12) {
                    home_h++;
                    home_h_text.setText(String.valueOf(home_h));
                }
                else if(home_h==12){
                    home_h=1;
                    home_h_text.setText(String.valueOf(home_h));
                }
            }
        });
        home_h_down = (Button)rootview.findViewById(R.id.gohome_h_down);
        home_h_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(home_h>1) {
                    home_h--;
                    home_h_text.setText(String.valueOf(home_h));
                }
                else if(home_h==1||home_h==0){
                    home_h=12;
                    home_h_text.setText(String.valueOf(home_h));
                }
            }
        });
        home_m_up = (Button)rootview.findViewById(R.id.gohome_m_up);
        home_m_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(home_m<59) {
                    home_m++;
                    home_m_text.setText(String.valueOf(home_m));
                }
                else if(home_m==59){
                    home_m=0;
                    home_m_text.setText("00");
                }
            }
        });
        home_m_down = (Button)rootview.findViewById(R.id.gohome_m_down);
        home_m_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(home_m>1) {
                    home_m--;
                    home_m_text.setText(String.valueOf(home_m));
                }
                else if(home_m==0){
                    home_m=59;
                    home_m_text.setText("59");
                }
                else if(home_m==1){
                    home_m=0;
                    home_m_text.setText("00");
                }
            }
        });
        school_h_up = (Button)rootview.findViewById(R.id.goschool_h_up);
        school_h_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(school_h<12) {
                    school_h++;
                    school_h_text.setText(String.valueOf(school_h));
                }
                else if(school_h==12){
                    school_h=1;
                    school_h_text.setText(String.valueOf(school_h));
                }
            }
        });
        school_h_down = (Button)rootview.findViewById(R.id.goschool_h_down);
        school_h_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(school_h>1) {
                    school_h--;
                    school_h_text.setText(String.valueOf(school_h));
                }
                else if(school_h==1||school_h==0){
                    school_h=12;
                    school_h_text.setText(String.valueOf(school_h));
                }
            }
        });
        school_m_up = (Button)rootview.findViewById(R.id.goschool_m_up);
        school_m_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(school_m<59) {
                    school_m++;
                    school_m_text.setText(String.valueOf(school_m));
                }
                else if(school_m==59){
                    school_m=0;
                    school_m_text.setText("00");
                }
            }
        });
        school_m_down = (Button)rootview.findViewById(R.id.goschool_m_down);
        school_m_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(school_m>1) {
                    school_m--;
                    school_m_text.setText(String.valueOf(school_m));
                }
                else if(school_m==0){
                    school_m=59;
                    school_m_text.setText(String.valueOf(school_m));
                }
                else if(school_m==1){
                    school_m=0;
                    school_m_text.setText("00");
                }
            }
        });
        submit = (Button)rootview.findViewById(R.id.submit_but);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(home_flag==1&&home_h!=12){
                    home_h+=12;
                }
                else if(home_flag==0&&home_h==12){
                    home_h=0;
                }
                if(school_flag==1&&school_h!=12){
                    school_h+=12;
                }
                else if(school_flag==0&&school_h==12){
                    school_h=0;
                }
                cs = new ClientSocket(getActivity());
                COMMUTING_TIME ct = new COMMUTING_TIME();
                ct.setOrderOperation("INSERT");
                ct.setInsertRequest("insert into commuting_time values('"+position+"','"+id+"','"+String.valueOf(school_h)+":"+String.valueOf(school_m)+"','"+String.valueOf(home_h)+":"+String.valueOf(school_m)+"');");
                Log.v("abcd",""+ct.getInsertRequest());
                cs.setObj((ObjectTable)ct);
                cs.start();
                while (cs.flag){
                    Log.v("cs_time","aaa");
                }
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentBorC, new search_magic(longitude,latitude,id,position,goback));
                fragmentTransaction.commit();
            }
        });
        return rootview;
    }
}

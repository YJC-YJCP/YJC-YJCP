package com.example.seeth.car;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by parkjeongmi on 2017-06-05.
 */

public class waiting_magic extends Fragment{
    View rootview=null;
    Button textok;
    double longitude,latitude;
    String id,position,goback;
    double search_latitude,search_longitude;
    public waiting_magic(double search_longitude,double search_latitude,double longitude,double latitude,String id,String position,String goback) {
        if(search_latitude==0.0){
            this.search_latitude=latitude;
            this.search_longitude=longitude;
        }
        else{
            this.search_latitude=search_latitude;
            this.search_longitude=search_longitude;
        }

        this.id=id;
        this.position=position;
        this.goback=goback;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = (View)inflater.inflate(R.layout.activity_wating_magic,container,false);
        textok = (Button)rootview.findViewById(R.id.wating_ok);
        textok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentBorC, new waiting(search_longitude,search_latitude,longitude,latitude,id,position,goback));
                fragmentTransaction.commit();
            }
        });
        return rootview;
    }
}

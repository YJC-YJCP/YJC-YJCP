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

public class search_magic extends Fragment{
    View rootview=null;
    Button textok;
    double longitude,latitude;
    String id,position,goback;
    TextView tv;
    public search_magic(double longitude,double latitude,String id,String position,String goback){
        this.latitude=latitude;
        this.longitude=longitude;
        this.id=id;
        this.position=position;
        this.goback=goback;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = (View)inflater.inflate(R.layout.activity_search_magic,container,false);
        textok = (Button)rootview.findViewById(R.id.textok);
        textok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentBorC, new search(longitude,latitude,id,position,goback));
                fragmentTransaction.commit();
            }
        });
        return rootview;
    }
}

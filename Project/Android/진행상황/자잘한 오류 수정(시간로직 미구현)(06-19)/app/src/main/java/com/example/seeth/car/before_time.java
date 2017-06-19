package com.example.seeth.car;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by parkjeongmi on 2017-06-06.
 */

public class before_time extends Fragment {
    View rootview=null;
    Button textok;
    double longitude,latitude;
    String id,position,goback;
    public before_time(double longitude,double latitude,String id,String position,String goback){
        this.longitude=longitude;
        this.latitude=latitude;
        this.id=id;
        this.position=position;
        this.goback=goback;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = (View) inflater.inflate(R.layout.activity_before_time, container, false);
        textok = (Button)rootview.findViewById(R.id.textok);
        textok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentBorC, new time_select(longitude,latitude,id,position,goback));
                fragmentTransaction.commit();
            }
        });
        return rootview;
    }
}

package com.example.seeth.car;


import android.support.annotation.NonNull;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import static java.lang.Math.*;

/**
 * Created by 이현구 on 2017-05-23.
 */

public class TPoint implements Serializable ,Comparable<TPoint>{
    String flag;
    double latitude;
    double longitude;
    int index;
    String name;
    String hour;
    String minute;
    String second;
    String description;
    Date time=new Date();
    ArrayList<TPoint> mp;
    private static final long serialVersionUID=2L;
    public TPoint(double latitude, double longitude) {
        mp=new ArrayList<>();
        this.latitude=latitude;
        this.longitude=longitude;
    }
    public TPoint(double latitude, double longitude,String description,int index) {
        mp=new ArrayList<>();
        this.latitude=latitude;
        this.longitude=longitude;
        this.index=index;
        this.description=description;
    }
    public TPoint(double latitude, double longitude,String description) {
        mp=new ArrayList<>();
        this.latitude=latitude;
        this.longitude=longitude;
        this.description=description;
    }
    public int compareTo(TPoint tp) {

        int tic;
        if(this.index>tp.index)
            tic=1;
        else if(this.index<tp.index)
            tic=1;
        else
            tic=0;
        return tic;
    }
}

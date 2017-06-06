package com.example.seeth.car;


import android.support.annotation.NonNull;

import com.skp.Tmap.TMapPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Math.*;

/**
 * Created by 이현구 on 2017-05-23.
 */

public class TPoint implements Serializable{
    String flag;
    double latitude;
    double longitude;
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


}

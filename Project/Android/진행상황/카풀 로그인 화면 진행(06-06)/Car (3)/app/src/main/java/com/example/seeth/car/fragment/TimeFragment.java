package com.example.seeth.car.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.seeth.car.R;

/**
 * Created by seeth on 2017-05-31.
 */

public class TimeFragment extends Fragment{
    public TimeFragment() {
        //생성자
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_timetable2, container, false);
    }

}

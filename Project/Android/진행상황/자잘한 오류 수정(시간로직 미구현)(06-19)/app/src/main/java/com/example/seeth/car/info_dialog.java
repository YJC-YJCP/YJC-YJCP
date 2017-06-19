package com.example.seeth.car;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ListView;

import com.example.seeth.car.USER;

import java.util.ArrayList;

public class info_dialog extends Dialog {

    ArrayList<info_Item> Accepted_list_itemArrayList = new ArrayList<info_Item>();
    ArrayList<info_Item> Wating_list_itemArrayList = new ArrayList<info_Item>();

    ArrayList<USER> Acceptance_user_list = null;//수락한 리스트들
    ArrayList<USER> Waiting_user_list = null;//대기하는 리스트들

    ListView top_list;
    ListView bottom_list;
    Accepted_ListAdapter adapter;
    Wating_ListAdapter adapter2;
    Context context;
    String Gender_division;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_layout);//리스트뷰를 다이아로그로 만들기위해


        //탑승자의 상태 정보를 받아와서 1이면 Aceeped 2이면 Wating에 3이면 아무것도 취하지 않음
        for(int i = 0; Acceptance_user_list.size() > i;i++){
            if(Acceptance_user_list.get(i).isGENDER() == true){
                Gender_division = "@drawable/man1";
            }else
                Gender_division = "@drawable/woman1";
            Accepted_list_itemArrayList.add(new info_Item("@drawable/logo", (Acceptance_user_list.get(i).getNAME()), Gender_division, "위치 = 용산동")); //반복할 내용들을 리스트에 저장
        }

//        Accepted_list_itemArrayList.add(new info_Item("@drawable/logo", "서동현", "@drawable/man_woman", "위치 = 용산동")); //반복할 내용들을 리스트에 저장
//        Accepted_list_itemArrayList.add(new info_Item("@drawable/logo", "박승주", "@drawable/woman", "위치 = 용산동"));

        top_list = (ListView)findViewById(R.id.list_view);//리스트 뷰를 찾아오고
        adapter = new Accepted_ListAdapter(context,Accepted_list_itemArrayList,Acceptance_user_list);
        top_list.setAdapter(adapter);

        for(int i = 0 ;Waiting_user_list.size() > i ; i++){
            if(Waiting_user_list.get(i).isGENDER() == true){
                Gender_division = "@drawable/man1";
            }else
                Gender_division = "@drawable/woman1";

            Wating_list_itemArrayList.add(new info_Item("@drawable/logo",(Waiting_user_list.get(i).getNAME()),Gender_division,"위치 = 용산동"));
        }
//        Wating_list_itemArrayList.add(new info_Item("@drawable/logo","호돌이","@drawable/man","위치 = 용산동"));
//        Wating_list_itemArrayList.add(new info_Item("@drawable/hosun","호순이","@drawable/woman","위치 = 용산동"));

        bottom_list = (ListView)findViewById(R.id.list_view_bottom);//리스트 뷰를 찾아오고
        adapter2 = new Wating_ListAdapter(context,Wating_list_itemArrayList,Acceptance_user_list,Waiting_user_list,this);
        bottom_list.setAdapter(adapter2);

    }

    public info_dialog(@NonNull Context context, ArrayList<USER> Acceptance_user_list, ArrayList<USER> Waiting_user_list){
        super(context);
        this.context = context;
        this.Acceptance_user_list = Acceptance_user_list;
        this.Waiting_user_list = Waiting_user_list;
    }


}

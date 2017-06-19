package com.example.seeth.car;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seeth.car.BOARDING_PASS;
import com.example.seeth.car.ObjectTable;
import com.example.seeth.car.USER;

import java.util.ArrayList;

/**
 * Created by tjehd on 2017-05-29.
 */

public class defail_info_dialog extends Dialog implements View.OnClickListener{

    TextView name,day,home_and_school,time,ap_position;
    Button yes_btn,no_btn;
    Context mcontext;
    String st_name,st_day,st_home_and_school,st_time,st_ap_position;
    String OCCUPANT_ID;
    ClientSocket sk;
    ArrayList<USER> Waiting_user_list;
    ArrayList<USER> Acceptance_user_list;
    info_dialog info_Dialog;
    int temp=0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defail_info);



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


        yes_btn = (Button)findViewById(R.id.info_yes);
        no_btn = (Button)findViewById(R.id.info_no);
        //여기서 상대방의 신청을 수락 하면 카풀상태를 바꾸어주면 됩니다.
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sk = new ClientSocket(mcontext);

                Log.i("check","파국이다~");
                BOARDING_PASS BM = new BOARDING_PASS();

                BM.setOrderOperation("UPDATE");

                BM.setUpdateRequest("update BOARDING_PASS set ACCPTANCE_STATE = 2 where occupant_id = '"+OCCUPANT_ID+"';");

                sk.setObj((ObjectTable)BM);

                sk.start();
                Log.i("check","☆☆☆☆☆☆11111");
//                while(sk.flag){
//
//                };
                Log.i("check","☆☆☆☆☆☆22222");
                sk = null;

                Toast.makeText(mcontext,"상대방의 신청을 수락했습니다.",Toast.LENGTH_SHORT).show();
                info_Dialog.dismiss();
                dismiss();

//                ///////////////////////////////////////////////////////////////////////////////////////////////////
                //여기서 wating_list에서 받아온 id값과 생성자를 통해 받아온 id를 비교하여 위치를 찾는다.
                for(int i = 0; Waiting_user_list.size()>i;i++){
                    if(Waiting_user_list.get(i).getID() == OCCUPANT_ID){
                        temp = i ;
                    }
                }
                Acceptance_user_list.add(Waiting_user_list.get(temp));
                Waiting_user_list.remove(temp);
                ///////////////////////////////////////////////////////-인텐트 보내주는거
//                Intent intent = new Intent(mcontext,MainActivity.class);
//                intent.putExtra("Wating_ListAdater", Acceptance_user_list);
//                intent.putExtra("Acceptance_ListAdater", Acceptance_user_list);
            }
        });
        //여기서 상대방의 신청을 거절 하면 카풀상태를 바꾸어서 안보이게
        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mcontext,"상대방의 신청을 거절했습니다.",Toast.LENGTH_SHORT).show();

                sk = new ClientSocket(mcontext);

                BOARDING_PASS BM = new BOARDING_PASS();

                BM.setOrderOperation("DELETE");

                BM.setDeleteRequest("delete from BOARDING_PASS where OCCUPANT_ID = "+"'"+OCCUPANT_ID+"';");

                sk.setObj((ObjectTable)BM);

                sk.start();

                info_Dialog.dismiss();
                dismiss();

                for(int i = 0; Waiting_user_list.size()>i;i++){
                    if(Waiting_user_list.get(i).getID() == OCCUPANT_ID){
                        temp = i ;
                    }
                }

                Waiting_user_list.remove(temp);

            }
        });
//        no_btn.setOnClickListener((View.OnClickListener) mcontext);
    }

    public defail_info_dialog( Context context) {
        super(context);
        mcontext = context;

    }

    public defail_info_dialog(Context context, String name, String day, String home_and_school,String time, String ap_position ,String OCCUPANT_ID , ArrayList<USER> Acceptance_user_list ,ArrayList<USER> Waiting_user_list,info_dialog info_Dialog) {
        super(context);
        mcontext = context;
        this.Waiting_user_list = Waiting_user_list;
        this.Acceptance_user_list = Acceptance_user_list;
        this.OCCUPANT_ID = OCCUPANT_ID;
        st_name = name;
        st_day = day;
        st_home_and_school = home_and_school;
        st_time = time;
        st_ap_position = ap_position;
        this.info_Dialog = info_Dialog;
    }


    @Override
    public void onClick(View v) {
        if(v == yes_btn){

        }

    }
}

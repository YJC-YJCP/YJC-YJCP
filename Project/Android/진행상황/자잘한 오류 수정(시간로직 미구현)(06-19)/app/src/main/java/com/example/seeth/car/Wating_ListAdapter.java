package com.example.seeth.car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seeth.car.USER;

import java.util.ArrayList;

/**
 * Created by tjehd on 2017-05-21.
 */

public class Wating_ListAdapter extends BaseAdapter implements View.OnClickListener{
    int pos;
    Context context;
    ArrayList<info_Item> list_itemArrayList;
    Button godetail_btn;
    long Position1,Position2;
    ArrayList<USER> Waiting_user_list;
    ArrayList<USER> Acceptance_user_list;
    info_dialog info_Dialog;
    public Wating_ListAdapter(Context context, ArrayList<info_Item> list_itemArrayList, ArrayList<USER> Acceptance_user_list, ArrayList<USER> Waiting_user_list, info_dialog info_Dialog) {
        this.context = context;
        this.list_itemArrayList = list_itemArrayList;
        this.Waiting_user_list = Waiting_user_list;
        this.Acceptance_user_list = Acceptance_user_list;
        this.info_Dialog = info_Dialog;
    }


    @Override
    public int getCount() {//리스트뷰가 몇개의 아이템을 가지고 있는지 알려주는 함수
        return this.list_itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {//현재 어떤 아이템인지 알려주는 부분
        return this.list_itemArrayList.get(position);

    }
    public ArrayList<USER>get_Waiting_ArrayList(){
        return Waiting_user_list;
    }
    public ArrayList<USER>get_Acceptance_ArrayList(){
        return Acceptance_user_list;
    }
    @Override
    public long getItemId(int position) {//어떤 포지션인지 알려주는 부분
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {//convertView 라는 파라미터를 메소드에 준다.
        pos = position;
        // 이부분에 우리가 만든 아이템을 불러와야 합니다. 여기서는 액티비티가 아니므로 불러오기위한 약간의 절차가 피료하다. 그때문에 context를 생성자를 통해 받은것이다.
        //LayoutInflater 클래스를 이용하면 다른클레스에서도 xml를 가져올수있음
        String fail = "실패";
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.info, null);

            ImageView Ap_View_Img = (ImageView) convertView.findViewById(R.id.Ap_View_Img);
            TextView Ap_View_Name = (TextView) convertView.findViewById(R.id.Ap_View_Name);
            ImageView Ap_View_Sex = (ImageView) convertView.findViewById(R.id.Ap_View_Sex);
//            TextView Ap_View_Position = (TextView) convertView.findViewById(R.id.Ap_View_Position);
            godetail_btn = (Button) convertView.findViewById(R.id.Detail_check);

            int res_Img = context.getResources().getIdentifier(list_itemArrayList.get(position).getAp_View_Img(), "drawable", context.getPackageName());
            int res_Sex = context.getResources().getIdentifier(list_itemArrayList.get(position).getAp_View_Sex(), "drawable", context.getPackageName());
            Ap_View_Img.setBackgroundResource(res_Img);
            Ap_View_Sex.setBackgroundResource(res_Sex);
            Ap_View_Name.setText(list_itemArrayList.get(position).getAp_View_Name());
//            Ap_View_Position.setText(list_itemArrayList.get(position).getAp_View_Position());

            godetail_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i = 0;Waiting_user_list.size()>i;i++){
                        if(position==i){
                            defail_info_dialog dialog = new defail_info_dialog(context, (Waiting_user_list.get(i).getNAME()), "월", "하교", "9:40", "용산동",Waiting_user_list.get(i).getID()
                            ,Acceptance_user_list,Waiting_user_list,info_Dialog);
                            dialog.show();

                        }
                    }
                }
            });}
        return convertView;//이제 컨벌트뷰에서 item뷰를 불러왔다.

    }
    @Override
    public void onClick(View v) {
        if(v == godetail_btn){

        }
    }
}
package com.example.seeth.car;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;


public class Timetable extends AppCompatActivity implements View.OnClickListener{
    //버튼들과 에디트텍스트 필드선언
    Button GTS_Monday_Btn,GTS_Tuesday_Btn,GTS_Wendnesday_Btn,GTS_Thursday_Btn,GTS_Friday_Btn;
    Button Mon_x,Tuse_x,Wend_x,Thurs_x,Fri_x;
    EditText GTS_Monday_Text,GTS_Tuesday_Text,GTS_Wendnesday_Text,GTS_Thursday_Text,GTS_Friday_Text;

    COMMUTING_TIME commuting_time;

    ObjectTable obj;

    //커스텀 다이아로그 불러오기위한 필드선언
    public Timetable_dialog Timetable_dialog;
    int[]  buttemp = new int[5];

    Button[] day_btn = new Button[5];//요일버튼
    Button[] x_btn = new Button[5];//X버튼
    EditText[] day_text = new EditText[5];//등교시간
    EditText[] GTH_text = new EditText[5];//하교시간

    ArrayList<String> Time_value;

    private void setUseableEditText(EditText et, boolean useable) {
        et.setClickable(useable);
        et.setEnabled(useable);
        et.setFocusable(useable);
        et.setFocusableInTouchMode(useable);
    }

    private void setBasic_Color(Button button){
        button.setBackgroundColor(Color.parseColor("#EAEAEA"));
    }

    private void Disable_edit(EditText edittext1,EditText editText2,Button btn){ //에디트  비활성화
        setUseableEditText(edittext1,false);
        setUseableEditText(editText2,false);
        edittext1.setText("");
        editText2.setText("");
        btn.setClickable(false);
        btn.setBackgroundColor(Color.parseColor("#00FF0000"));
    }

    private void Activation_edit(EditText edittext1,EditText editText2,Button btn){ //에디트 활성화 메소드
        setUseableEditText(edittext1,true);
        setUseableEditText(editText2,true);
        btn.setBackgroundColor(Color.parseColor("#EAEAEA"));
        btn.setClickable(true);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        Log.i("전달유무", "타임테이블 실행");
//        Intent it = this.getIntent();
//        int flag = it.getExtras().getInt("flag");
        for(int i = 0; i<5;i++){//x버튼 활성화 비활성화를 위한 temp
            buttemp[i]=0;
        }
        //x버튼

        Mon_x = (Button)findViewById(R.id.mon_x); x_btn[0]= Mon_x;
        Tuse_x = (Button)findViewById(R.id.tuse_x); x_btn[1]=Tuse_x;
        Wend_x = (Button)findViewById(R.id.wend_x); x_btn[2]=Wend_x;
        Thurs_x = (Button)findViewById(R.id.thurs_x); x_btn[3]=Thurs_x;
        Fri_x = (Button)findViewById(R.id.fri_x); x_btn[4]=Fri_x;

        GTS_Monday_Btn = (Button) findViewById(R.id.GTS_Monday_button); day_btn[0]=GTS_Monday_Btn;
        GTS_Monday_Text = (EditText)findViewById(R.id.GTS_Monday_text) ; day_text[0]=GTS_Monday_Text;
        GTH_text[0] = (EditText)findViewById(R.id.GTH_Monday_text);

        GTS_Tuesday_Btn = (Button) findViewById(R.id.GTS_Tuesday_button);day_btn[1]=GTS_Tuesday_Btn;
        GTS_Tuesday_Text = (EditText)findViewById(R.id.GTS_Tuesday_text) ;day_text[1]=GTS_Tuesday_Text;
        GTH_text[1] = (EditText)findViewById(R.id.GTH_Tuesday_text);

        GTS_Wendnesday_Btn = (Button) findViewById(R.id.GTS_Wendnesday_button);day_btn[2]=GTS_Wendnesday_Btn;
        GTS_Wendnesday_Text = (EditText)findViewById(R.id.GTS_Wednesday_text) ;day_text[2]=GTS_Wendnesday_Text;
        GTH_text[2] = (EditText)findViewById(R.id.GTH_Wednesday_text);

        GTS_Thursday_Btn = (Button) findViewById(R.id.GTS_Thursday_button);day_btn[3]=GTS_Thursday_Btn;
        GTS_Thursday_Text = (EditText)findViewById(R.id.GTS_Thursday_text) ;day_text[3]=GTS_Thursday_Text;
        GTH_text[3] = (EditText)findViewById(R.id.GTH_Thursday_text);

        GTS_Friday_Btn = (Button) findViewById(R.id.GTS_Friday_button);day_btn[4]=GTS_Friday_Btn;
        GTS_Friday_Text = (EditText)findViewById(R.id.GTS_Friday_text) ;day_text[4]=GTS_Friday_Text;
        GTH_text[4] = (EditText)findViewById(R.id.GTH_Friday_text);

        //버튼,에디트텍스트 리스너 등록
        for(int i=0;day_btn.length>i;i++){
            day_btn[i].setOnClickListener(this);
        }
        for(int i=0;day_text.length>i;i++){
            day_text[i].setOnClickListener(this);
        }
        for(int i = 0; GTH_text.length>i;i++){
            GTH_text[i].setOnClickListener(this);
        }
        for(int i = 0; i<5; i++){
            x_btn[i].setOnClickListener(this);
        }




        }

    public void Store_btn_click(View v){//저장버튼이네
        Log.i("전달유무", "" + v.getId());
        MainActivity m = new MainActivity();
        View layoutID = m.findViewById(R.id.container);
        RelativeLayout layout = (RelativeLayout)layoutID;
        LayoutInflater inflater = (LayoutInflater)getSystemService(this.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_timetable, layout, true);
        Log.i("전달유무", "" + v.getId());
//        LinearLayout layout = (LinearLayout)findViewById(R.id.timeLayout);
//        LayoutInflater inflater = (LayoutInflater)getSystemService(this.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.activity_main, layout, true);
//        Intent returnMain = new Intent(this, MainActivity.class);
//        setResult(RESULT_OK, returnMain);
        finish();
    }//네트워크 전송되고 부른 액티비티에


    @Override
    public void onClick(View v) {
        Log.i("전달유무", "" + v.getId());
        if(v==x_btn[0] && buttemp[0]==0){
            Disable_edit(day_text[0],GTH_text[0],day_btn[0]);
            buttemp[0]++;
        }
        else if(v==x_btn[0]&& buttemp[0]==1){
            Activation_edit(day_text[0],GTH_text[0],day_btn[0]);
            buttemp[0]--;
        }

        if(v==x_btn[1] && buttemp[1]==0){
            Disable_edit(day_text[1],GTH_text[1],day_btn[1]);
            buttemp[1]++;
        }
        else if(v==x_btn[1]&& buttemp[1]==1){
            Activation_edit(day_text[1],GTH_text[1],day_btn[1]);
            buttemp[1]--;
        }

        if(v==x_btn[2] && buttemp[2]==0){
            Disable_edit(day_text[2],GTH_text[2],day_btn[2]);
            buttemp[2]++;
        }
        else if(v==x_btn[2]&& buttemp[2]==1){
            Activation_edit(day_text[2],GTH_text[2],day_btn[2]);
            buttemp[2]--;
        }

        if(v==x_btn[3] && buttemp[3]==0){
            Disable_edit(day_text[3],GTH_text[3],day_btn[3]);
            buttemp[3]++;
        }
        else if(v==x_btn[3]&& buttemp[3]==1){
            Activation_edit(day_text[3],GTH_text[3],day_btn[3]);
            buttemp[3]--;
        }

        if(v==x_btn[4] && buttemp[4]==0){
            Disable_edit(day_text[4],GTH_text[4],day_btn[4]);
            buttemp[4]++;
        }
        else if(v==x_btn[4]&& buttemp[4]==1){
            Activation_edit(day_text[4],GTH_text[4],day_btn[4]);
            buttemp[4]--;
        }


        if(v==day_btn[0]) {
           Timetable_dialog = new Timetable_dialog(this,day_text[0],GTH_text[0]);
            Timetable_dialog.show();
        }
        else if(v==day_btn[1]) {
            Timetable_dialog = new Timetable_dialog(this,day_text[1],GTH_text[1]);
            Timetable_dialog.show();
        }
        else if(v==day_btn[2]) {
            Timetable_dialog = new Timetable_dialog(this,day_text[2],GTH_text[2]);
            Timetable_dialog.show();
        }
        else if(v==day_btn[3]) {
            Timetable_dialog = new Timetable_dialog(this,day_text[3],GTH_text[3]);
            Timetable_dialog.show();
        }
        else if(v==day_btn[4]) {
            Timetable_dialog = new Timetable_dialog(this,day_text[4],GTH_text[4]);
            Timetable_dialog.show();
        }

    }





}



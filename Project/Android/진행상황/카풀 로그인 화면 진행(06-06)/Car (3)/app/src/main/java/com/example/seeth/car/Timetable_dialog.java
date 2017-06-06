package com.example.seeth.car;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

class Timetable_dialog extends Dialog implements View.OnClickListener{

    private EditText AM_text;
    private EditText PM_text;
    private Button Check;
    private Button Cancel;

    //생성자로 받아온 Edittext
    public EditText left,right;

//    private View.OnClickListener mLeftClickListener;
//    private View.OnClickListener mRightClickListener;
//    private View.OnClickListener mAMClickListener;
//    private View.OnClickListener mPMClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_timetable_dialog); // 이 레이아웃의 정보들을 참고하겠다.


        AM_text = (EditText)findViewById(R.id.AM_text);
        PM_text = (EditText)findViewById(R.id.PM_text);
        Check = (Button)findViewById(R.id.Check_button);
        Cancel = (Button)findViewById(R.id.cancel_button);

        // 제목과 내용을 생성자에서 셋팅한다.
//        mTitleView.setText(mTitle);
//        mContentView.setText(mContent);


        AM_text.setOnClickListener(this);
        PM_text.setOnClickListener(this);
        Check.setOnClickListener(this);
        Cancel.setOnClickListener(this);
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
//    public Timetable_dialog(Context context, String title,
//                        View.OnClickListener singleListener) {
//        super(context, android.R.style.Theme_Translucent_NoTitleBar);
//        this.mTitle = title;
//        this.mLeftClickListener = singleListener;
//    }


    public Timetable_dialog(Context context, EditText left, EditText right) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        String a,p;
//        this.mLeftClickListener = leftListener;

        this.left = left;
        this.right = right;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.AM_text){
//            Toast.makeText(getContext(), "am버튼 클릭", Toast.LENGTH_SHORT).show();
            TimePickerDialog dialog = new TimePickerDialog(getContext(), AM_listener, 15, 24, false);
            dialog.show();

        }
        else if (v.getId()==R.id.PM_text)
        {
//            Toast.makeText(getContext(), "ap버튼 클릭", Toast.LENGTH_SHORT).show();
            TimePickerDialog dialog = new TimePickerDialog(getContext(), PM_listener, 15, 24, false);
            dialog.show();
        }
        else if(v.getId()==R.id.Check_button){
            String a,p;
            a = AM_text.getText().toString();
            p = PM_text.getText().toString();

            left.setText(a);
            right.setText(p);
            dismiss();
        }
        else if (v.getId()==R.id.cancel_button){
            dismiss();
        }


    }

    public TimePickerDialog.OnTimeSetListener AM_listener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {// 설정버튼 눌렀을 때

            String temp = (hourOfDay+" : "+minute);
            AM_text.setText(temp);
        }
    };

    public TimePickerDialog.OnTimeSetListener PM_listener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {// 설정버튼 눌렀을 때

            String temp = (hourOfDay+" : "+minute);
            PM_text.setText(temp);

        }
    };


}


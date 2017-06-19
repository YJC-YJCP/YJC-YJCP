package com.example.seeth.car;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class membership extends AppCompatActivity implements View.OnClickListener{
    EditText Car_color_eidt,Car_number_edit,Car_model_edit;
    RadioButton Car_possession_true, Car_possession_false;
    COMMUTING_TIME cm ;
    USER user = new USER();
    EditText InputID;
    EditText InputPW;
    EditText InputName;
    EditText InputDe;
    EditText inputClass;
    RadioButton inputGirl;
    RadioButton inputMan;
    ClientSocket sk=null;
    private void setUseableEditText(EditText et, boolean useable) {
        et.setClickable(useable);
        et.setEnabled(useable);
        et.setFocusable(useable);
        et.setFocusableInTouchMode(useable);
    }
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);
        Car_color_eidt = (EditText)findViewById(R.id.Car_color_edit);
        Car_number_edit = (EditText)findViewById(R.id.Car_number_edit);
        Car_model_edit = (EditText)findViewById(R.id.Car_model_edit);
        InputID=(EditText)findViewById(R.id.inputID);
        InputPW=(EditText)findViewById(R.id.inputPW);
        InputName=(EditText)findViewById(R.id.inputName);
        InputDe=(EditText)findViewById(R.id.inputDEPART);
        inputClass=(EditText)findViewById(R.id.inputClass);
        inputGirl=(RadioButton)findViewById(R.id.inputGirl);
        inputMan=(RadioButton)findViewById(R.id.inputMan);

        Car_possession_false = (RadioButton)findViewById(R.id.Car_possession_false);//차량 소유 안함
        Car_possession_true = (RadioButton)findViewById(R.id.Car_possession_true);//차량 소유 함

        Car_possession_false.setOnClickListener(this);
        Car_possession_true.setOnClickListener(this);

    }

    public void onClick(View V){
        if(V == Car_possession_false){
            setUseableEditText(Car_color_eidt,false);
            setUseableEditText(Car_model_edit,false);
            setUseableEditText(Car_number_edit,false);
        }
        else if(V == Car_possession_true){
            setUseableEditText(Car_color_eidt,true);
            setUseableEditText(Car_model_edit,true);
            setUseableEditText(Car_number_edit,true);
        }

    }
    public void buttonOnClick(View view){
        user.setOrderOperation("INSERT");
        String str = "INSERT INTO USER (ID,DEPARTMENT_NUMBER,PASSWORD,NAME,GENDER,VEHICEL_OWNED,VEHICLE_NUMBER,CAR_MODEL,CAR_COLOR,PROFILE_PICTURE_URL,CAR_PICTURE_URL)VALUE ('"+InputID.getText().toString()+"','"+InputDe.getText().toString()+"','"+InputPW.getText().toString()+"','"+InputName.getText().toString()+"',";

        if(inputMan.isChecked()==true&&inputGirl.isChecked()==false)//남자
        {
            str=str+"true,";
            if(Car_possession_true.isChecked()==true&&Car_possession_false.isChecked()==false){//차량 소유 상태
                sk = new ClientSocket(this);
                str=str+true+",'"+Car_number_edit.getText().toString()+"','"+Car_model_edit.getText().toString()+"','"+Car_color_eidt.getText().toString()+"','"+null+"','"+null+"')";
                user.setOrderOperation("INSERT");
                user.setInsertRequest(str);
                ObjectTable obj = user;
                sk.setObj(obj);
                sk.start();
                while(sk.flag)
                {

                }
                registerTime();
            }else if(Car_possession_true.isChecked()==false&&Car_possession_false.isChecked()==true)//차량 미소유 상태
            {
                sk = new ClientSocket(this);
                str=str+false+",'null','null','null','null','null')";
                user.setOrderOperation("INSERT");
                user.setInsertRequest(str);
                ObjectTable obj = user;
                sk.setObj(obj);
                sk.start();
                while(sk.flag)
                {

                }
                registerTime();
            }
            else//소유 , 숫자, 차종,차색,null,null
            {
                Toast.makeText(getApplicationContext(),"차량 소유 여부를 선택해주세요.",Toast.LENGTH_LONG).show();
            }
        }
        else if(inputMan.isChecked()==false&&inputGirl.isChecked()==true)//여자
        {
            str=str+"false,";
            if(Car_possession_true.isChecked()==true&&Car_possession_false.isChecked()==false){//차량 소유 상태
                sk = new ClientSocket(this);
                str=str+true+",'"+Car_number_edit.getText().toString()+"','"+Car_model_edit.getText().toString()+"','"+Car_color_eidt.getText().toString()+"','"+null+"','"+null+"')";
                user.setOrderOperation("INSERT");
                user.setInsertRequest(str);
                ObjectTable obj = user;
                sk.setObj(obj);
                sk.start();
                while(sk.flag)
                {

                }
                registerTime();
            }else if(Car_possession_true.isChecked()==false&&Car_possession_false.isChecked()==true)//차량 미소유 상태
            {
                sk = new ClientSocket(this);
                str=str+false+",'null','null','null','null','null')";
                user.setOrderOperation("INSERT");
                user.setInsertRequest(str);
                ObjectTable obj = user;
                sk.setObj(obj);
                sk.start();
                while(sk.flag)
                {

                }
                registerTime();

            }
            else//소유 , 숫자, 차종,차색,null,null
            {
                Toast.makeText(getApplicationContext(),"차량 소유 여부를 선택해주세요.",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"성별을 선택해주세요.",Toast.LENGTH_LONG).show();
        }




    }
    public void registerTime(){
        sk=new ClientSocket(this);
        COMMUTING_TIME CMT1 = new COMMUTING_TIME();

        CMT1.setOrderOperation("INSERT");
        CMT1.setInsertRequest("INSERT INTO COMMUTING_TIME VALUES('월','"+InputID.getText().toString()+"',"+null+","+null+");");
        ObjectTable obj1 = CMT1;
        sk.setObj(obj1);




        COMMUTING_TIME CMT2 = new COMMUTING_TIME();
        CMT2.setOrderOperation("INSERT");
        CMT2.setInsertRequest("INSERT INTO COMMUTING_TIME VALUES('화','"+InputID.getText().toString()+"',"+null+","+null+");");
        ObjectTable obj2 = CMT2;
        sk.setObj(obj2);




        COMMUTING_TIME CMT3 = new COMMUTING_TIME();
        CMT3.setOrderOperation("INSERT");
        CMT3.setInsertRequest("INSERT INTO COMMUTING_TIME VALUES('수','"+InputID.getText().toString()+"',"+null+","+null+");");
        ObjectTable obj3 = CMT3;
        sk.setObj(obj3);

        COMMUTING_TIME CMT4 = new COMMUTING_TIME();
        CMT4.setOrderOperation("INSERT");
        CMT4.setInsertRequest("INSERT INTO COMMUTING_TIME VALUES('목','"+InputID.getText().toString()+"',"+null+","+null+");");
        ObjectTable obj4 = CMT4;
        sk.setObj(obj4);


        COMMUTING_TIME CMT5 = new COMMUTING_TIME();
        CMT5.setOrderOperation("INSERT");
        CMT5.setInsertRequest("INSERT INTO COMMUTING_TIME VALUES('금','"+InputID.getText().toString()+"',"+null+","+null+");");
        ObjectTable obj5 = CMT5;
        sk.setObj(obj5);
        sk.start();


    }
}

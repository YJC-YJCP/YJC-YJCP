package com.example.seeth.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        cm=new COMMUTING_TIME();
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

    public void cancelOnClick(View v) {
        finish();
    }
    public void buttonOnClick(View view){
        user.setOrderOperation("INSERT");
        cm.setOrderOperation("INSERT");
        String str = "INSERT INTO USER (ID,DEPARTMENT_NUMBER,PASSWORD,NAME,GENDER,VEHICEL_OWNED,VEHICLE_NUMBER,MODELCAR,CAR_COLOR,PROFILE_PICTURE_URL,CAR_PICTURE_URL)VALUE ('"+InputID.getText().toString()+"','"+InputDe.getText().toString()+"','"+InputPW.getText().toString()+"','"+InputName.getText().toString()+"',";
        String strd = "INSERT INTO COMMUTING_TIME (ID) VALUES +";
        if(inputMan.isChecked()==true&&inputGirl.isChecked()==false)//남자
        {
            str=str+"true,";
            if(Car_possession_true.isChecked()==true&&Car_possession_false.isChecked()==false){//차량 소유 상태
                str=str+true+",'"+Car_number_edit.getText().toString()+"','"+Car_model_edit.getText().toString()+"','"+Car_color_eidt.getText().toString()+"','"+null+"','"+null+"')";
                user.setOrderOperation("INSERT");
                user.setInsertRequest(str);
                sk = new ClientSocket(this);
                Intent intent =new Intent(this,choice.class);
                sk.setIntent(intent);

                ObjectTable obj = user;
                sk.setObj(obj);
                sk.start();
            }else if(Car_possession_true.isChecked()==false&&Car_possession_false.isChecked()==true)//차량 미소유 상태
            {
                str=str+false+",'null','null','null','null','null')";
                user.setOrderOperation("INSERT");
                user.setInsertRequest(str);
                sk = new ClientSocket(this);
                Intent intent =new Intent(this,choice.class);
                sk.setIntent(intent);
                ObjectTable obj = user;
                sk.setObj(obj);
                sk.start();
            }
            else//소유 , 숫자, 차종,차색,null,null
            {
                Toast.makeText(getApplicationContext(),"차량 소유 여부를 선택해주세요.",Toast.LENGTH_LONG).show();
            }
        }
        else if(inputMan.isChecked()==false&&inputGirl.isChecked()==true)//여자
        {
            str=str+",false,";
            if(Car_possession_true.isChecked()==true&&Car_possession_false.isChecked()==false){//차량 소유 상태
                str=str+true+",'"+Car_number_edit.getText().toString()+"','"+Car_model_edit.getText().toString()+"','"+Car_color_eidt.getText().toString()+"','"+null+"','"+null+"')";
                user.setOrderOperation("INSERT");
                user.setInsertRequest(str);
                sk = new ClientSocket(this);
                Intent intent =new Intent(this,choice.class);
                sk.setIntent(intent);
                ObjectTable obj = user;
                sk.setObj(obj);
                sk.start();
            }else if(Car_possession_true.isChecked()==false&&Car_possession_false.isChecked()==true)//차량 미소유 상태
            {
                str=str+false+",'null','null','null','null','null')";
                user.setOrderOperation("INSERT");
                user.setInsertRequest(str);
                sk = new ClientSocket(this);
                Intent intent =new Intent(this,choice.class);
                sk.setIntent(intent);
                ObjectTable obj = user;
                sk.setObj(obj);
                sk.start();
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
}

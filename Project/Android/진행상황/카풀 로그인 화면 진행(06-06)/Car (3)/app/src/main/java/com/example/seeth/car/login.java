package com.example.seeth.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/*
이 자바소스는 로그인화면입니다.
 */
public class login extends AppCompatActivity {
    private EditText name;
    private String id_value;
    ClientSocket sk = null;

    EditText pw;
    EditText id;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pw = (EditText) findViewById(R.id.password_inupt);
        id = (EditText) findViewById(R.id.input_id);
    }

    public void join_membership(View V) {
        Intent abc = new Intent(login.this, membership.class);
        startActivity(abc);
    }

    public void next_main(View V) {
        sk = new ClientSocket(this);
        USER usr = new USER();
        usr.setID(id.getText().toString());
        usr.setPASSWORD(pw.getText().toString());
        usr.setOrderOperation("SELECT");
//        usr.setSelectRequest("select * from USER WHERE ID LIKE 'user1' AND PASSWORD LIKE 'user1';");
        usr.setSelectRequest("SELECT * FROM USER WHE.RE ID LIKE '"+usr.getID()+"'and PASSWORD LIKE '"+usr.getPASSWORD()+"';");
        Intent intent =new Intent(this, choice.class);
        sk.setIntent(intent);
        ObjectTable obj = usr;
        sk.setObj(obj);
        sk.start();
    }

}



package com.example.seeth.intentflagtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Second extends Activity implements View.OnClickListener {
    Button b_next, b_home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        b_next = (Button)findViewById(R.id.secondNextButton);
        b_next.setOnClickListener(this);
        b_home = (Button)findViewById(R.id.secondHomeButton);
        b_home.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.secondHomeButton :
                Intent intentHome = new Intent(this, MainActivity.class);
                /*"FLAG_ACTIVITY_CLEAR_TOP" 플래그.
                간단히 현재 액티비티에서 어느 액티비티로 이동하는데, 스택 중간에 있었던 액티비티들을 지우는 역할은 한다고 이해하면 된다.
                이 플래그가 없으면, 중간에 액티비티는 스택에 그대로 남아있기 때문에 이동 중간에  화면에 표출되어 UI 흐름을 망친다.
                또한 시간이 지나면서 수 많은 액티비티가 쌓이게 되어 메모리 낭비를 초래한다.*/
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                /*"FLAG_ACTIVITY_SINGLE_TOP" 플래그.
                띄우려는 액티비티가 스택 맨위에 이미 실행 중이라면 재사용하겠다는 의미로 해석하면 된다.*/
                intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentHome);
                break;
            case R.id.secondNextButton :
                EditText name = (EditText)findViewById(R.id.nameSet);
                String n = name.getText().toString();
                EditText age = (EditText)findViewById(R.id.ageSet);
                String a = age.getText().toString();
                Intent i = new Intent(this, Third.class);
                i.putExtra("ID", n);
                i.putExtra("AGE", a);

                startActivity(i);
            default :
                break;
        }
    }
}

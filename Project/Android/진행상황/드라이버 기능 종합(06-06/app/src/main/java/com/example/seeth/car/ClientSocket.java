package com.example.seeth.car;

/**
 * Created by parkjeongmi on 2017-06-01.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

class ClientSocket extends Thread{
    Socket sk = null;
    ObjectOutputStream outstream = null;
    ObjectInputStream instream = null;
    String formServer;
    static final String TAG5 = "hoit_cli";
    Queue<ObjectTable> queue = null;
    Queue<ObjectTable> queue2 =null;
    String id;
    String pw;
    boolean bn;
    Context cc;
    boolean flag = true;
    //===============================DB클래스 추가 부분
    ObjectTable obj;
    Intent intent;
    public ClientSocket(Context con){
        cc=con;
        queue=new LinkedList();
        queue2=new LinkedList<>();
    }
    public void setObj(ObjectTable obj){//오브젝트 객체가 넘어온다.
        this.obj=obj;
        queue.add(this.obj);
    }
    public void setIntent(Intent it){
        intent=it;
    }
    public Object getObj(){
        return obj;
    }
    public Queue<ObjectTable> getQueue2(){
        return queue2;
    }
    public void run() {
        try {
            sk = new Socket("172.20.1.48",9311);
            outstream = new ObjectOutputStream(sk.getOutputStream());
            instream = new ObjectInputStream(sk.getInputStream());

            outstream.writeObject(queue);//데이터를 보낸다.
            outstream.flush();
            queue2= (Queue<ObjectTable>) instream.readObject();//큐를 받았다.
            flag=false;
            if(intent.equals(null)==false&&queue2.peek().getResultResponse()==true) {
                Bundle extras = new Bundle();
                extras.putSerializable("queue2", (Serializable) queue2);
                intent.putExtra("extras", extras);
                cc.startActivity(intent);//다음 화면으로 넘기자 ,
            }
            instream.close();
            outstream.close();
            sk.close();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }//소켓생성하면서 바로 연결

    }
}
package com.example.seeth.car;

import java.io.Serializable;
import java.sql.ResultSet;

public abstract class ObjectTable implements Serializable,Cloneable{
    String orderOperation;//디비가 동작해야할 동작을 지정
    String orderTable;
    String deleteRequest;
    String insertRequest;
    String updateRequest;
    String selectRequest;
    boolean resultResponse;//서버가 실행한 결과에 대한 성공 또는 실패 여부를 반환

    private static final long serialVersionUID=2L;

    public abstract String getOrderTable();
    public abstract String getInsertRequest();
    public abstract String getSelectRequest();
    public abstract String getUpdateRequest();
    public abstract String getDeleteRequest();
    public abstract String getOrderOperation();    //INSERT ,UPDATE, SELECT,DELETE 지정하는 역할을 반환하는 메소드
    public abstract boolean getResultResponse();

    public abstract void setUpdateRequest(String str);
    public abstract void setSelectRequest(String str);
    public abstract void setInsertRequest(String str);
    public abstract void setDeleteRequest(String str);
    public abstract void setOrderOperation(String str);    //INSERT ,UPDATE, SELECT,DELETE 지정하는 역할을 수행하는 메소드
    public abstract void setOrderTable(String str);
    public abstract void setResultResponse(boolean result);
    public abstract void setSelectOperation(ResultSet rs);
    public abstract Object clone();
}

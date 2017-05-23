package database;

import java.io.Serializable;
import java.sql.ResultSet;

public abstract class ObjectTable implements Serializable{
	String orderOperation;//��� �����ؾ��� ������ ����
	String orderTable;
	String deleteRequest;
	String insertRequest;
	String updateRequest;
	String selectRequest;
	boolean resultResponse;//������ ������ ����� ���� ���� �Ǵ� ���� ���θ� ��ȯ

    private static final long serialVersionUID=2L;

    public abstract String getOrderTable();
    public abstract String getInsertRequest();
    public abstract String getSelectRequest();
    public abstract String getUpdateRequest();
    public abstract String getDeleteRequest();
    public abstract String getOrderOperation();    //INSERT ,UPDATE, SELECT,DELETE �����ϴ� ������ ��ȯ�ϴ� �޼ҵ�
    public abstract boolean getResultResponse();

    public abstract void setUpdateRequest(String str);
    public abstract void setSelectRequest(String str);
    public abstract void setInsertRequest(String str);
    public abstract void setDeleteRequest(String str);
    public abstract void setOrderOperation(String str);    //INSERT ,UPDATE, SELECT,DELETE �����ϴ� ������ �����ϴ� �޼ҵ�
    public abstract void setOrderTable(String str);
    public abstract void setResultResponse(boolean result);
    public abstract void setSelectOperation(ResultSet rs);
}

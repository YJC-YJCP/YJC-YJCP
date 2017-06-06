package com.example.seeth.car;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/* �ش� Ŭ������
 * ������ȸ ���̺� �̿��ϴ� Ŭ���� �Դϴ�.
 */
class GENERAL_INQUIRY extends ObjectTable implements Serializable{

	private int DEPARTMENT_NUMBER;
	private String AFFILIATION;
	@Override
	public String getOrderTable() {
		// TODO Auto-generated method stub
		return this.orderTable;
	}
	@Override
	public String getInsertRequest() {
		// TODO Auto-generated method stub
		return this.insertRequest;
	}
	@Override
	public String getSelectRequest() {
		// TODO Auto-generated method stub
		return this.selectRequest;
	}
	@Override
	public String getUpdateRequest() {
		// TODO Auto-generated method stub
		return this.updateRequest;
	}
	@Override
	public String getDeleteRequest() {
		// TODO Auto-generated method stub
		return this.deleteRequest;
	}
	@Override
	public String getOrderOperation() {
		// TODO Auto-generated method stub
		return this.orderOperation;
	}
	@Override
	public boolean getResultResponse() {
		// TODO Auto-generated method stub
		return this.resultResponse;
	}
	@Override
	public void setUpdateRequest(String str) {
		// TODO Auto-generated method stub
		this.updateRequest=str;
	}
	@Override
	public void setSelectRequest(String str) {
		// TODO Auto-generated method stub
		this.selectRequest=str;
	}
	@Override
	public void setInsertRequest(String str) {
		// TODO Auto-generated method stub
		this.insertRequest=str;
	}
	@Override
	public void setDeleteRequest(String str) {
		// TODO Auto-generated method stub
		this.deleteRequest=str;
	}
	@Override
	public void setOrderOperation(String str) {
		// TODO Auto-generated method stub
		this.orderOperation=str;
	}
	@Override
	public void setOrderTable(String str) {
		// TODO Auto-generated method stub
		this.orderTable=str;
	}
	@Override
	public void setResultResponse(boolean result) {
		// TODO Auto-generated method stub
		this.resultResponse=result;
	}
	@Override
	public void setSelectOperation(ResultSet rs) {
		// TODO Auto-generated method stub
		try {
			this.setDEPARTMENT_NUMBER(rs.getInt("DEPARTMENT_NUMBER"));
			this.setAFFILIATION(rs.getString("AFFILIATION"));//String
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getDEPARTMENT_NUMBER() {
		return DEPARTMENT_NUMBER;
	}
	public void setDEPARTMENT_NUMBER(int dEPARTMENT_NUMBER) {
		DEPARTMENT_NUMBER = dEPARTMENT_NUMBER;
	}
	public String getAFFILIATION() {
		return AFFILIATION;
	}
	public void setAFFILIATION(String aFFILIATION) {
		AFFILIATION = aFFILIATION;
	}
	
}

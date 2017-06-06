package com.example.seeth.car;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class COMMUTING_TIME  extends ObjectTable implements Serializable,Cloneable{
	private String DAY;
	private String ID;
	private Time TIME_FOR_SCHOOL =new Time(0, 0, 0);
	private Time TIME_FOR_HOME =new Time(0, 0, 0);
	private static final long serialVersionUID=2L;
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
		try {
			this.setID(rs.getString("ID"));
			this.setDAY(rs.getString("DAY"));
			this.setTIME_FOR_SCHOOL(rs.getTime("TIME_FOR_SCHOOL"));
			this.setTIME_FOR_HOME(rs.getTime("TIME_FOR_HOME"));

		} catch (SQLException e) {


		}
	}
	public String getDAY() {
		return DAY;
	}
	public void setDAY(String dAY) {
		DAY = dAY;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public Time getTIME_FOR_SCHOOL() {
		return TIME_FOR_SCHOOL;
	}
	public void setTIME_FOR_SCHOOL(Time tt) {
		if(tt==null){
			TIME_FOR_SCHOOL=null;
		}else{
			TIME_FOR_SCHOOL=null;
			int hour=tt.getHours();
			int minute = tt.getMinutes();
			TIME_FOR_SCHOOL= new Time(hour,minute,0);
		}
	}
	public Time getTIME_FOR_HOME() {
		return TIME_FOR_HOME;
	}
	public void setTIME_FOR_HOME(Time tt) {
		if(tt==null){
			TIME_FOR_HOME=null;
		}else{
			TIME_FOR_HOME=null;
			int hour=tt.getHours();
			int minute = tt.getMinutes();
			TIME_FOR_HOME= new Time(hour,minute,0);
			}
	}
	public Object clone()
	{
		COMMUTING_TIME CT = new COMMUTING_TIME();

		return CT;
	}//end clone


}
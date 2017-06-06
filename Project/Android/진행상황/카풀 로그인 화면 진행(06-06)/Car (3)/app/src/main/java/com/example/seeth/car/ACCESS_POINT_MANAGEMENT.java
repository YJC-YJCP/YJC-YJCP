package com.example.seeth.car;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

class ACCESS_POINT_MANAGEMENT extends ObjectTable implements Serializable{
	
	private String KAPUL_ACCESS_POINT_SERIAL_NUMBER;
	private String CARPOOL_SERIAL_NUMBER;
	private double ACCESS_POINT_LATITUDE;
	private double ACCESS_POINT_LONGITUDE;
	private Time ACCESS_POINT_TIME_OF_ARRIVAL =new Time(0, 0, 0);
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
			this.setKAPUL_ACCESS_POINT_SERIAL_NUMBER(rs.getString("KAPUL_ACCESS_POINT_SERIAL_NUMBER"));
			this.setCARPOOL_SERIAL_NUMBER(rs.getString("CARPOOL_SERIAL_NUMBER"));
			this.setACCESS_POINT_LATITUDE(rs.getDouble("ACCESS_POINT_LATITUDE"));
			this.setACCESS_POINT_LONGITUDE(rs.getDouble("ACCESS_POINT_LONGITUDE"));
			this.setACCESS_POINT_TIME_OF_ARRIVAL(rs.getTime("ACCESS_POINT_TIME_OF_ARRIVAL"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getKAPUL_ACCESS_POINT_SERIAL_NUMBER() {
		return KAPUL_ACCESS_POINT_SERIAL_NUMBER;
	}
	public void setKAPUL_ACCESS_POINT_SERIAL_NUMBER(String kAPUL_ACCESS_POINT_SERIAL_NUMBER) {
		KAPUL_ACCESS_POINT_SERIAL_NUMBER = kAPUL_ACCESS_POINT_SERIAL_NUMBER;
	}
	public String getCARPOOL_SERIAL_NUMBER() {
		return CARPOOL_SERIAL_NUMBER;
	}
	public void setCARPOOL_SERIAL_NUMBER(String cARPOOL_SERIAL_NUMBER) {
		CARPOOL_SERIAL_NUMBER = cARPOOL_SERIAL_NUMBER;
	}
	public double getACCESS_POINT_LATITUDE() {
		return ACCESS_POINT_LATITUDE;
	}
	public void setACCESS_POINT_LATITUDE(double aCCESS_POINT_LATITUDE) {
		ACCESS_POINT_LATITUDE = aCCESS_POINT_LATITUDE;
	}
	public double getACCESS_POINT_LONGITUDE() {
		return ACCESS_POINT_LONGITUDE;
	}
	public void setACCESS_POINT_LONGITUDE(double aCCESS_POINT_LONGITUDE) {
		ACCESS_POINT_LONGITUDE = aCCESS_POINT_LONGITUDE;
	}
	public Time getACCESS_POINT_TIME_OF_ARRIVAL() {
		return ACCESS_POINT_TIME_OF_ARRIVAL;
	}
	public void setACCESS_POINT_TIME_OF_ARRIVAL(Time tt) {
		ACCESS_POINT_TIME_OF_ARRIVAL.setHours(tt.getHours());
		ACCESS_POINT_TIME_OF_ARRIVAL.setMinutes(tt.getMinutes());
	}
}

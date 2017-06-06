package com.example.seeth.car;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Time;

class CARPOOL_PATH_MANAGEMENT extends ObjectTable implements Serializable {

	private String CARPOOL_SERIAL_NUMBER;
	private String ID;
	private double STARTING_POINT_LATITUDE;
	private double STARTING_POINT_LONGITUDE;
	private double DESTINATION_LATITUDE;
	private double DESTINATION_LONGITUDE;
	private Time DEPARTURE_TIME=new Time(0,0,0);
	private Time ARRIVAL_TIME = new Time(0,0,0);
	private int THE_NUMBER_OF_OCCUPANT;
	private double CURRENT_DRIVER_LATITUDE;
	private double CURRENT_DRIVER_LONGITUDE;
	
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
		
		 try{
			// TODO Auto-generated method stub
			this.setCARPOOL_SERIAL_NUMBER(rs.getString("CARPOOL_SERIAL_NUMBER"));
			this.setID(rs.getString("ID"));//	private String ID;
			this.setSTARTING_POINT_LATITUDE(rs.getDouble("STARTING_POINT_LATITUDE"));//private double STARTING_POINT_LATITUDE;
			this.setSTARTING_POINT_LONGITUDE(rs.getDouble("STARTING_POINT_LONGITUDE"));//private double STARTING_POINT_LONGITUDE;
			this.setDESTINATION_LATITUDE(rs.getDouble("DESTINATION_LATITUDE"));//private double DESTINATION_LATITUDE;
			this.setDESTINATION_LONGITUDE(rs.getDouble("DESTINATION_LONGITUDE"));//private double DESTINATION_LONGITUDE;
			this.setDEPARTURE_TIME(rs.getTime("DEPARTURE_TIME"));//private Time DEPARTURE_TIME=new Time(0,0,0);
			this.setARRIVAL_TIME(rs.getTime("ARRIVAL_TIME"));//private Time ARRIVAL_TIME = new Time(0,0,0);
			this.setTHE_NUMBER_OF_OCCUPANT(rs.getInt("THE_NUMBER_OF_OCCUPANT"));//private int THE_NUMBER_OF_OCCUPANT;
			this.setCURRENT_DRIVER_LATITUDE(rs.getDouble("CURRENT_DRIVER_LATITUDE"));//	private double CURRENT_DRIVER_LATITUDE;
			this.setCURRENT_DRIVER_LONGITUDE(rs.getDouble("CURRENT_DRIVER_LONGITUDE"));//	private double ;
		
		 	} catch (Exception e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
			}
		 }
	public String getCARPOOL_SERIAL_NUMBER() {
		return CARPOOL_SERIAL_NUMBER;
	}

	public void setCARPOOL_SERIAL_NUMBER(String cARPOOL_SERIAL_NUMBER) {
		CARPOOL_SERIAL_NUMBER = cARPOOL_SERIAL_NUMBER;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public double getSTARTING_POINT_LATITUDE() {
		return STARTING_POINT_LATITUDE;
	}

	public void setSTARTING_POINT_LATITUDE(double sTARTING_POINT_LATITUDE) {
		STARTING_POINT_LATITUDE = sTARTING_POINT_LATITUDE;
	}

	public double getSTARTING_POINT_LONGITUDE() {
		return STARTING_POINT_LONGITUDE;
	}

	public void setSTARTING_POINT_LONGITUDE(double sTARTING_POINT_LONGITUDE) {
		STARTING_POINT_LONGITUDE = sTARTING_POINT_LONGITUDE;
	}

	public double getDESTINATION_LATITUDE() {
		return DESTINATION_LATITUDE;
	}

	public void setDESTINATION_LATITUDE(double dESTINATION_LATITUDE) {
		DESTINATION_LATITUDE = dESTINATION_LATITUDE;
	}

	public double getDESTINATION_LONGITUDE() {
		return DESTINATION_LONGITUDE;
	}

	public void setDESTINATION_LONGITUDE(double dESTINATION_LONGITUDE) {
		DESTINATION_LONGITUDE = dESTINATION_LONGITUDE;
	}

	public Time getDEPARTURE_TIME() {
		return DEPARTURE_TIME;
	}

	public void setDEPARTURE_TIME(Time dEPARTURE_TIME) {
		DEPARTURE_TIME.setHours(dEPARTURE_TIME.getHours());
		DEPARTURE_TIME.setMinutes(dEPARTURE_TIME.getMinutes());
	}

	public Time getARRIVAL_TIME() {
		return ARRIVAL_TIME;
	}

	public void setARRIVAL_TIME(Time aRRIVAL_TIME) {
		ARRIVAL_TIME.setHours(aRRIVAL_TIME.getHours());
		ARRIVAL_TIME.setMinutes(aRRIVAL_TIME.getMinutes());
	}

	public int getTHE_NUMBER_OF_OCCUPANT() {
		return THE_NUMBER_OF_OCCUPANT;
	}

	public void setTHE_NUMBER_OF_OCCUPANT(int tHE_NUMBER_OF_OCCUPANT) {
		THE_NUMBER_OF_OCCUPANT = tHE_NUMBER_OF_OCCUPANT;
	}

	public double getCURRENT_DRIVER_LATITUDE() {
		return CURRENT_DRIVER_LATITUDE;
	}

	public void setCURRENT_DRIVER_LATITUDE(double cURRENT_DRIVER_LATITUDE) {
		CURRENT_DRIVER_LATITUDE = cURRENT_DRIVER_LATITUDE;
	}

	public double getCURRENT_DRIVER_LONGITUDE() {
		return CURRENT_DRIVER_LONGITUDE;
	}

	public void setCURRENT_DRIVER_LONGITUDE(double cURRENT_DRIVER_LONGITUDE) {
		CURRENT_DRIVER_LONGITUDE = cURRENT_DRIVER_LONGITUDE;
	}

}

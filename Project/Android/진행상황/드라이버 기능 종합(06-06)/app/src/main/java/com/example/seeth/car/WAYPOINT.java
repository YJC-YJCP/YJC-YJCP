package com.example.seeth.car;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

class WAYPOINT extends ObjectTable implements Serializable,Cloneable{
	private static final long serialVersionUID=2L;
	private String CARPOOL_SERIAL_NUMBER;
	private int WAYPOINT_ORDER;
	private double WAYPOINT_LATITUDE;
	private double WAYPOINT_LONGITUDE;

	private int WAYPOINT_TOTALINDEX;
	public int getWAYPOINT_TOTALINDEX() {
		return WAYPOINT_TOTALINDEX;
	}

	public void setWAYPOINT_TOTALINDEX(int wAYPOINT_TOTALINDEX) {
		WAYPOINT_TOTALINDEX = wAYPOINT_TOTALINDEX;
	}


	public String getCARPOOL_SERIAL_NUMBER() {
		return CARPOOL_SERIAL_NUMBER;
	}

	public void setCARPOOL_SERIAL_NUMBER(String cARPOOL_SERIAL_NUMBER) {
		this.CARPOOL_SERIAL_NUMBER = cARPOOL_SERIAL_NUMBER;
	}

	public int getWAYPOINT_ORDER() {
		return WAYPOINT_ORDER;
	}

	public void setWAYPOINT_ORDER(int wAYPOINT_ORDER) {
		this.WAYPOINT_ORDER = wAYPOINT_ORDER;
	}

	public double getWAYPOINT_LATITUDE() {
		return WAYPOINT_LATITUDE;
	}

	public void setWAYPOINT_LATITUDE(double wAYPOINT_LATITUDE) {
		this.WAYPOINT_LATITUDE = wAYPOINT_LATITUDE;
	}

	public double getWAYPOINT_LONGITUDE() {
		return WAYPOINT_LONGITUDE;
	}

	public void setWAYPOINT_LONGITUDE(double wAYPOINT_LONGITUDE) {
		this.WAYPOINT_LONGITUDE = wAYPOINT_LONGITUDE;
	}


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
			this.setCARPOOL_SERIAL_NUMBER(rs.getString("CARPOOL_SERIAL_NUMBER"));
			this.setWAYPOINT_ORDER(rs.getInt("WAYPOINT_ORDER"));
			this.setWAYPOINT_LATITUDE(rs.getDouble("WAYPOINT_LATITUDE"));
			this.setWAYPOINT_LONGITUDE(rs.getDouble("WAYPOINT_LONGITUDE"));
			this.setWAYPOINT_TOTALINDEX(rs.getInt("WAYPOINT_TOTALINDEX"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public Object clone()
	{
		WAYPOINT wp = new WAYPOINT();

		return wp;
	}//end clone
}

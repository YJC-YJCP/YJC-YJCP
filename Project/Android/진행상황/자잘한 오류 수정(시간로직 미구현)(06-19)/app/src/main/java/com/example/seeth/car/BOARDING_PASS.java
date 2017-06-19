package com.example.seeth.car;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

class BOARDING_PASS  extends ObjectTable implements Serializable ,Cloneable{
	private String KAPUL_ACCESS_POINT_SERIAL_NUMBER;
	private String CARPOOL_SERIAL_NUMBER;
	private String OCCUPANT_ID;
	private String ACCPTANCE_STATE;
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
			this.setOCCUPANT_ID(rs.getString("OCCUPANT_ID"));
			this.setACCPTANCE_STATE(rs.getString("ACCPTANCE_STATE"));
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

	public String getOCCUPANT_ID() {
		return OCCUPANT_ID;
	}

	public void setOCCUPANT_ID(String oCCUPANT_ID) {
		OCCUPANT_ID = oCCUPANT_ID;
	}

	public String getACCPTANCE_STATE() {
		return ACCPTANCE_STATE;
	}

	public void setACCPTANCE_STATE(String aCCPTANCE_STATE) {
		ACCPTANCE_STATE = aCCPTANCE_STATE;
	}
	public Object clone()
	{
		BOARDING_PASS BP = new BOARDING_PASS();
		return BP;
	}//end clone
}

package database;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

class CUSTOMER_SERVICE_MANAGEMENT extends ObjectTable implements Serializable{

	private String ID;
	private String CUSTOMER_INQUIRY_CLASSFICATION;
	private String INQUIRY_TITLE;
	private String INQUIRY_CONTENTS;
	private String MAIL_ADDRESS;
	private boolean ANSWER_STATE;
	
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
			this.setID(rs.getString("ID"));//private String ID;
			this.setCUSTOMER_INQUIRY_CLASSFICATION(rs.getString("CUSTOMER_INQUIRY_CLASSFICATION"));
			this.setINQUIRY_TITLE(rs.getString("INQUIRY_TITLE"));
			this.setINQUIRY_CONTENTS(rs.getString("INQUIRY_CONTENTS"));//;
			this.setMAIL_ADDRESS(rs.getString("MAIL_ADDRESS"));
			this.setANSWER_STATE(rs.getBoolean("ANSWER_STATE")); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//;
	}
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getCUSTOMER_INQUIRY_CLASSFICATION() {
		return CUSTOMER_INQUIRY_CLASSFICATION;
	}

	public void setCUSTOMER_INQUIRY_CLASSFICATION(String cUSTOMER_INQUIRY_CLASSFICATION) {
		CUSTOMER_INQUIRY_CLASSFICATION = cUSTOMER_INQUIRY_CLASSFICATION;
	}

	public String getINQUIRY_TITLE() {
		return INQUIRY_TITLE;
	}

	public void setINQUIRY_TITLE(String iNQUIRY_TITLE) {
		INQUIRY_TITLE = iNQUIRY_TITLE;
	}

	public String getINQUIRY_CONTENTS() {
		return INQUIRY_CONTENTS;
	}

	public void setINQUIRY_CONTENTS(String iNQUIRY_CONTENTS) {
		INQUIRY_CONTENTS = iNQUIRY_CONTENTS;
	}

	public String getMAIL_ADDRESS() {
		return MAIL_ADDRESS;
	}

	public void setMAIL_ADDRESS(String mAIL_ADDRESS) {
		MAIL_ADDRESS = mAIL_ADDRESS;
	}

	public boolean isANSWER_STATE() {
		return ANSWER_STATE;
	}

	public void setANSWER_STATE(boolean aNSWER_STATE) {
		ANSWER_STATE = aNSWER_STATE;
	}

}

package kr.co.company.registration;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class USER extends ObjectTable implements Serializable{

    private String ID;
    private	int DEPARTMENT_NUMBER;
    private String PASSWORD;
    private String NAME;
    private boolean GENDER;
    private boolean VEHICEL_OWNED;
    private String VEHICEL_NUMBER;
    private String CAR_MODEL;
    private String CAR_COLOR;
    private String PROFILE_PICTURE_URL;
    private String CAR_PICTURE_URL;
    private static final long serialVersionUID=2L;

    public String getID() {
        return ID;
    }
    public void setID(String iD) {
        ID = iD;
    }
    public int getDEPARTMENT_NUMBER() {
        return DEPARTMENT_NUMBER;
    }
    public void setDEPARTMENT_NUMBER(int dEPARTMENT_NUMBER) {
        DEPARTMENT_NUMBER = dEPARTMENT_NUMBER;
    }
    public String getPASSWORD() {
        return PASSWORD;
    }
    public void setPASSWORD(String pASSWORD) {
        PASSWORD = pASSWORD;
    }
    public String getNAME() {
        return NAME;
    }
    public void setNAME(String nAME) {
        NAME = nAME;
    }
    public boolean isGENDER() {
        return GENDER;
    }
    public void setGENDER(boolean gENDER) {
        GENDER = gENDER;
    }
    public boolean isVEHICEL_OWNED() {
        return VEHICEL_OWNED;
    }
    public void setVEHICEL_OWNED(boolean vEHICEL_OWNED) {
        VEHICEL_OWNED = vEHICEL_OWNED;
    }
    public String getVEHICEL_NUMBER() {
        return VEHICEL_NUMBER;
    }
    public void setVEHICEL_NUMBER(String vEHICEL_NUMBER) {
        VEHICEL_NUMBER = vEHICEL_NUMBER;
    }
    public String getCAR_MODEL() {
        return CAR_MODEL;
    }
    public void setCAR_MODEL(String cAR_MODEL) {
        CAR_MODEL = cAR_MODEL;
    }
    public String getCAR_COLOR() {
        return CAR_COLOR;
    }
    public void setCAR_COLOR(String cAR_COLOR) {
        CAR_COLOR = cAR_COLOR;
    }
    public String getPROFILE_PICTURE_URL() {
        return PROFILE_PICTURE_URL;
    }
    public void setPROFILE_PICTURE_URL(String pROFILE_PICTURE_URL) {
        PROFILE_PICTURE_URL = pROFILE_PICTURE_URL;
    }
    public String getCAR_PICTURE_URL() {
        return CAR_PICTURE_URL;
    }
    public void setCAR_PICTURE_URL(String cAR_PICTURE_URL) {
        CAR_PICTURE_URL = cAR_PICTURE_URL;
    }


    public String getOrderTable(){
        return this.orderTable;
    }
    public String getInsertRequest(){
        return this.insertRequest;
    }
    public String getSelectRequest(){
        return this.selectRequest;
    }
    public String getUpdateRequest(){
        return this.updateRequest;
    }
    public String getDeleteRequest(){
        return this.deleteRequest;
    }
    public String getOrderOperation(){    //INSERT ,UPDATE, SELECT,DELETE 지정하는 역할을 반환하는 메소드
        return this.orderOperation;
    }
    public boolean getResultResponse(){
        return this.resultResponse;
    }
    public void setUpdateRequest(String str){
        this.updateRequest=str;
    }
    public void setSelectRequest(String str){
        this.selectRequest=str;
    }
    public void setInsertRequest(String str){
        this.insertRequest=str;
    }
    public void setDeleteRequest(String str){
        this.deleteRequest=str;
    }
    public void setOrderOperation(String str){//INSERT ,UPDATE, SELECT,DELETE 지정하는 역할을 수행하는 메소드
        this.orderOperation=str;
    }
    public void setOrderTable(String str){this.orderTable=str;}

    public void setResultResponse(boolean result){
        this.resultResponse=result;
    }
    public void setSelectOperation(ResultSet rs){
        try {
            this.setID(rs.getString("ID"));//ID
            this.setDEPARTMENT_NUMBER(rs.getInt("DEPARTMENT_NUMBER"));
            this.setNAME(rs.getString("NAME"));
            this.setGENDER(rs.getBoolean("GENDER"));
            this.setVEHICEL_OWNED(rs.getBoolean("VEHICEL_OWNED"));
            this.setVEHICEL_NUMBER(rs.getString("VEHICEL_NUMBER"));
            this.setCAR_MODEL(rs.getString("CAR_MODEL"));
            this.setCAR_COLOR(rs.getString("CAR_COLOR"));
            this.setPROFILE_PICTURE_URL(rs.getString("PROFILE_PICTURE_URL"));
            this.setCAR_PICTURE_URL(rs.getString("CAR_PICTURE_URL"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

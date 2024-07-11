package com.scenic.rownezcoreservice.model;

public enum EmailTemplateParam {
    STAFF_NAME ("staffName"),
    STAFF_ID ("staffId"),
    STAFF_ROLE("staffRole"),
    DEPARTMENT ("department"),
    DATE_OF_BIRTH ("dob"),
    MOBILE_NUMBER ("mobile");

    String param;
    EmailTemplateParam(String param) {
        this.param = param;
    }
    public String getParam(){
        return param;
    }
}

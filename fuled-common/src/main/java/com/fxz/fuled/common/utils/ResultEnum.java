package com.fxz.fuled.common.utils;

public enum ResultEnum {
    SUCCESS("0000", "", "success"),
    USER_NOT_SIGN_IN("10", "00", "user not login"),
    BIZ_EXCEPTION("20", "00", "bizException"),
    UNCAUGHT_EXCEPTION("99", "99", "UncaughtException");


    private String code;

    private String subCode;

    private String message;

    public String getCode() {
        return this.code.concat(this.subCode);
    }

    public ResultEnum setSubCode(String subCode) {
        this.subCode = subCode;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    ResultEnum(String code, String subCode, String message) {
        this.code = code;
        this.subCode = subCode;
        this.message = message;
    }

}

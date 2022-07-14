package com.fxz.fuled.common.utils;

public enum ResultEnum {
    SUCCESS("0000", "", "成功"),
    USER_NOT_SIGN_IN("10", "00", "用户未登录"),
    BIZ_EXCEPTION("20", "00", "业务异常");


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

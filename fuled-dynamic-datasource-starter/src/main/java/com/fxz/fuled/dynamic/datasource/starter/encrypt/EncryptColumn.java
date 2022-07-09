package com.fxz.fuled.dynamic.datasource.starter.encrypt;

import lombok.Data;

@Data
public class EncryptColumn {

    private String value;

    public EncryptColumn() {
    }

    public EncryptColumn(String value) {
        this.value = value;
    }

    public static EncryptColumn create(String value) {
        return new EncryptColumn(value);
    }
}

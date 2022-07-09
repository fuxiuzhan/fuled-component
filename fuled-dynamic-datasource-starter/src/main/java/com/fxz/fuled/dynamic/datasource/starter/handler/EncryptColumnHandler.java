package com.fxz.fuled.dynamic.datasource.starter.handler;

import com.fxz.fuled.common.converter.StringValueConveter;
import com.fxz.fuled.common.utils.SpringApplicationUtil;
import com.fxz.fuled.dynamic.datasource.starter.encrypt.EncryptColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
public class EncryptColumnHandler extends BaseTypeHandler<EncryptColumn> {

    private StringValueConveter valueConverter;

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EncryptColumn parameter, JdbcType jdbcType) throws SQLException {
        String value = parameter.getValue();
        try {
            if (!StringUtils.isEmpty(value)) {
                value = getValueConverter().encrypt(value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ps.setString(i, value);
    }

    @Override
    public EncryptColumn getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String ret = rs.getString(columnName);
        return get(ret);
    }

    @Override
    public EncryptColumn getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String ret = rs.getString(columnIndex);
        return get(ret);
    }

    @Override
    public EncryptColumn getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String ret = cs.getString(columnIndex);
        return get(ret);
    }

    /**
     * 解密
     *
     * @param value
     * @return
     */
    private EncryptColumn get(String value) {
        EncryptColumn encryptColumn = new EncryptColumn();
        try {
            if (!StringUtils.isEmpty(value)) {
                String decrypt = getValueConverter().decrypt(value);
                encryptColumn.setValue(decrypt);
            }
            return encryptColumn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return
     */
    private StringValueConveter getValueConverter() {
        if (Objects.isNull(valueConverter)) {
            valueConverter = SpringApplicationUtil.applicationContext.getBean(StringValueConveter.class);
        }
        if (Objects.isNull(valueConverter)) {
            valueConverter = new StringValueConveter() {
            };
        }
        return valueConverter;
    }
}

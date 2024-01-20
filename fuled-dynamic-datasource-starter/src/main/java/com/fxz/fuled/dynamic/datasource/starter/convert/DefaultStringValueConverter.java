package com.fxz.fuled.dynamic.datasource.starter.convert;

import com.fxz.fuled.common.Env;
import com.fxz.fuled.common.converter.StringValueConveter;
import com.fxz.fuled.common.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class DefaultStringValueConverter implements StringValueConveter {


    private static String password;

    public DefaultStringValueConverter(String password) {
        DefaultStringValueConverter.password = password;
    }

    @Override
    public String encrypt(String value) throws Exception {
        if (!StringUtils.isEmpty(value)) {
            return encrypt(value, getKey());
        }
        return value;
    }

    @Override
    public String decrypt(String value) throws Exception {
        if (!StringUtils.isEmpty(value)) {
            return decrypt(value, getKey());
        }
        return value;
    }

    public static String getKey() {
        if (StringUtils.isEmpty(password)) {
            ConfigUtil.initialize();
            String appId = ConfigUtil.getAppId();
            Env env = ConfigUtil.getEnv();
            return appId + "-" + env.name();
        }
        return password;
    }

    public static String encrypt(String context, String key) throws Exception {
        if (!StringUtils.isEmpty(context)) {
            return Base64.getEncoder().encodeToString(encrypt(context.getBytes(), key));
        }
        return context;
    }

    public static String decrypt(String context, String key) throws Exception {
        if (!StringUtils.isEmpty(context)) {
            return new String(decrypt(Base64.getDecoder().decode(context), key));
        }
        return context;
    }

    public static byte[] decrypt(byte[] context, String key) throws Exception {
        key = key + "0123456789abcdefghijklmn";
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(subBytes(key.getBytes(), 0, 16));
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128, random);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] encoder = secretKey.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(encoder, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(context);
    }

    private static byte[] encrypt(byte[] context, String key) throws Exception {
        key = key + "0123456789abcdefghijklmn";
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(subBytes(key.getBytes(), 0, 16));
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128, random);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] encoder = secretKey.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(encoder, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(context);
    }

    private static byte[] subBytes(byte[] buffer, int pos, int length) {
        byte[] temp = new byte[length];
        System.arraycopy(buffer, pos, temp, 0, length);
        return temp;
    }

}

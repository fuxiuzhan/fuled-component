package com.fxz.fuled.env.encryptor.starter;

import com.fxz.fuled.common.ConfigUtil;
import com.fxz.fuled.common.Env;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * @author fxz
 */
@Slf4j
public class EncryptablePropertySource<T> extends PropertySource<T> {
    PropertySource<T> source;

    String encPrefix = "ENC_";

    public EncryptablePropertySource(String name, PropertySource<T> source) {
        super(name, (T) source);
        this.source = source;
    }

    @Override
    public Object getProperty(String name) {
        Object property = source.getProperty(name);
        if (Objects.nonNull(property)) {
            //process value
            //for example encrypt or decrypt
            if (property.toString().startsWith(encPrefix)) {
                try {
                    String decrypt = decrypt(property.toString().replace(encPrefix, ""), getKey());
                    log.info("PropertySourceWrapper Encrypt Property key->{},encryptValue->{},decryptValue->{}", name, property, decrypt);
                    return decrypt;
                } catch (Exception e) {
                    log.error("decrypt property error->{}", e);
                    throw new RuntimeException(e.getCause());
                }
            }
            log.info("PropertySourceWrapper key->{},value->{}", name, property);
        }
        return property;
    }

    @Override
    public T getSource() {
        return (T) source;
    }

    public static String getKey() {
        ConfigUtil.initialize();
        String appId = ConfigUtil.getAppId();
        Env env = ConfigUtil.getEnv();
        return appId + "-" + env.name();
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

    public static void main(String[] args) throws Exception {
        String value = "1234567890";
        String key = "monitor-PRD";
        String encryptStr = encrypt(value, key);
        System.out.println("encryptStr->" + encryptStr);
        String decryptStr = decrypt(encryptStr, key);
        System.out.println("decryptStr->" + decryptStr);
    }
}
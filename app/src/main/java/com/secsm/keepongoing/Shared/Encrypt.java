package com.secsm.keepongoing.Shared;


import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class Encrypt {
    //////////////////////////////
    // encrypt                  //
    //////////////////////////////
    private static Key key;
    private static Cipher cipher;
    private static String LOG_TAG = "Encrypt";

    /* for encoding */
    public static Key generateKey(String algorithm, byte[] keyData) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        SecretKeySpec keySpec = new SecretKeySpec(keyData, algorithm);
        return keySpec;
    }

    public static void initKey() {
        /* encoding the message */
        try {
            key = generateKey("AES", ByteUtils.toBytes("696d697373796f7568616e6765656e61", 16));

            String transformation = "AES/ECB/PKCS5Padding";
            cipher = Cipher.getInstance(transformation);
        } catch (Exception e) {
            Log.e("KEY", e.toString());
        }
    }

    public static String encodingMsg(String msg) {
        /* encoding the message before send */
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypt = cipher.doFinal(msg.getBytes());
            String encryptStr = ByteUtils.toHexString(encrypt);
            Log.i(LOG_TAG, "encrypted message : " + encryptStr);
            return encryptStr;
        } catch (Exception ex) {
            return msg;
        }
    }




    private static Pattern HANGLE_PATTERN = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]");

    public static String encodeIfNeed(String input) {
        if (TextUtils.isEmpty(input)) {
            return input;
        }

        Matcher matcher = HANGLE_PATTERN.matcher(input);
        while(matcher.find()) {
            String group = matcher.group();

            try {
                input = input.replace(group, URLEncoder.encode(group, "UTF-8"));
            } catch (UnsupportedEncodingException ignore) {
            }
        }

        return input;
    }



}

package com.mirea.kt.ribo.passwordmanager;

import android.util.Base64;
import android.util.Log;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoHelper {

    private static final String TAG = "CryptoHelper";

    private static SecretKeySpec key = null;

    public static boolean hasKey() {
        return key != null;
    }

    public static String createSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    public static void setKey(String masterPassword, String salt) {
        try {
            byte[] saltBytes = Base64.decode(salt, Base64.NO_WRAP);
            PBEKeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), saltBytes, 10000, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey secret = factory.generateSecret(spec);
            key = new SecretKeySpec(secret.getEncoded(), "AES");
        } catch (Exception e) {
            Log.e(TAG, "Key generation error", e);
            key = null;
        }
    }

    public static void clearKey() {
        key = null;
    }

    public static String encrypt(String text) {
        try {
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));

            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
            return Base64.encodeToString(result, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(TAG, "Encryption error", e);
            return null;
        }
    }

    public static String decrypt(String data) {
        try {
            byte[] all = Base64.decode(data, Base64.NO_WRAP);
            byte[] iv = new byte[16];
            byte[] encrypted = new byte[all.length - 16];
            System.arraycopy(all, 0, iv, 0, 16);
            System.arraycopy(all, 16, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "Decryption error", e);
            return null;
        }
    }
}

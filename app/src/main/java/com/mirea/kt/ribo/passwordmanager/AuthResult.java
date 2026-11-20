package com.mirea.kt.ribo.passwordmanager;

public class AuthResult {

    public static final int CODE_SUCCESS = 1;

    private final int resultCode;
    private final String title;
    private final String task;
    private final int variant;

    public AuthResult(int resultCode, String title, String task, int variant) {
        this.resultCode = resultCode;
        this.title = title;
        this.task = task;
        this.variant = variant;
    }

    public boolean isSuccess() {
        return resultCode == CODE_SUCCESS;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getTitle() {
        return title;
    }

    public String getTask() {
        return task;
    }

    public int getVariant() {
        return variant;
    }
}

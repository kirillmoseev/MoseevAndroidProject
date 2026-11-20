package com.mirea.kt.ribo.passwordmanager;

public class Entry {
    public int id;
    public String service;
    public String login;
    public String password;

    public Entry(int id, String service, String login, String password) {
        this.id = id;
        this.service = service;
        this.login = login;
        this.password = password;
    }
}

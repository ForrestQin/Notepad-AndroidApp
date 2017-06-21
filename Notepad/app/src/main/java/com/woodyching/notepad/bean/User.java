package com.woodyching.notepad.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Woody on 2017/1/16.
 */

public class User extends DataSupport{
    private String email;
    private String username;
    private String headShotPath;
    private String backgroupPath;
    private int id;
    private static int count = 0;

    public User(){
        email = "yourname@example.com";
        username = "君の名";
        id = count;
        headShotPath = "default";
        backgroupPath = "default";
        count++;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
       this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getBackgroupPath() {
        return backgroupPath;
    }

    public String getHeadShotPath() {
        return headShotPath;
    }

    public void setBackgroupPath(String backgroupPath) {
        this.backgroupPath = backgroupPath;
    }

    public void setHeadShotPath(String headShotPath) {
        this.headShotPath = headShotPath;
    }
}

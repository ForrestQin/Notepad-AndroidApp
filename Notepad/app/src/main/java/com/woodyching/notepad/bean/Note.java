package com.woodyching.notepad.bean;

import com.bumptech.glide.load.engine.Resource;

import org.litepal.crud.DataSupport;

/**
 * Created by Woody on 2016/12/31.
 */

public class Note extends DataSupport{

    private int id;
    private String content;
    private String title;
    private String imagePath;
    private static int count = 0;

    public Note(String title){
        this.title = title;
    }


    public Note(String title, String content, String imagePath) {
        id = count;
        this.content = content;
        this.title = title;
        this.imagePath = imagePath;
        count++;
    }

    public Note() {
        id = count;
        content = " ";
        title = " ";
        imagePath = "default";
        count++;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImagePath() {
        return imagePath;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }
}

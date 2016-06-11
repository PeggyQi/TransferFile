package com.transferfile.Bean;

public class ApkBean {
    private long id;
    private String title;
    private long size;
    public void setId(long id){
        this.id = id;
    }

    public long getId(){return this.id;}

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){return this.title;}

    public void setSize(long size){this.size = size;}

    public long getSize(){return this.size;}
}


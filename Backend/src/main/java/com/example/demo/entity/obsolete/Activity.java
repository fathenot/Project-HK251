package com.example.demo.entity.utility;

import java.time.LocalDateTime;

// auxiliary class
public class Activity {
    private long id;
    private LocalDateTime datetime;
    private ActivityType type;
    private String content;
    public Activity(long id, String datetimeString, ActivityType type, String content){
        this.id = id;
        this.datetime = DateTimeUtils.createDatetime(datetimeString);
        this.type = type;
        this.content = content;
    }
    public long getID(){return id;}
    public LocalDateTime getDatetime(){return datetime;}
    public ActivityType getActivityType(){return type;}
    public String getContent(){return content;}
}

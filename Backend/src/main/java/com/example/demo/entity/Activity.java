package com.example.demo.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "activities")
public class Activity {
    private String id;
    private LocalDateTime datetime;
    private ActivityType type;
    private String content;
    public Activity(String id, String datetimeString, ActivityType type, String content){
        this.id = id;
        this.datetime = DateTimeUtils.createDatetime(datetimeString);
        this.type = type;
        this.content = content;
    }
    public String getID(){return id;}
    public LocalDateTime getDatetime(){return datetime;}
    public ActivityType getActivityType(){return type;}
    public String getContent(){return content;}
}

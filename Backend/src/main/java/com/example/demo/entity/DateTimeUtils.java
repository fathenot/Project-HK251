package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateTimeUtils {

    public static int[] parseDate (String date_str){
        String[] date_arr = date_str.split("-");
        int[] result =  {Integer.parseInt(date_arr[0]), Integer.parseInt(date_arr[1]), Integer.parseInt(date_arr[2])};
        return result;
    }
    public static int[] parseDatetime(String datetime_str){
        String[] datetime_arr = datetime_str.split(" "); // datetime str từ SQL có dạng yyyy-mm-dd hh:mm:ss
        String[] date_arr = datetime_arr[0].split("-");
        String[] time_arr = datetime_arr[1].split(":");
        int[] result =  {
            Integer.parseInt(date_arr[0]), Integer.parseInt(date_arr[1]), Integer.parseInt(date_arr[2]), 
            Integer.parseInt(time_arr[0]), Integer.parseInt(time_arr[1]), Integer.parseInt(time_arr[2])
        };
        return result;
    }
    public static LocalDate createDate(String date_str){
        int[] args = DateTimeUtils.parseDate(date_str);
        return LocalDate.of(args[0], args[1], args[2]);
    }
    public static LocalDateTime createDatetime(String datetime_str){
        int[] args = DateTimeUtils.parseDatetime(datetime_str);
        return LocalDateTime.of(args[0], args[1], args[2], args[3], args[4], args[5]);
    }
}

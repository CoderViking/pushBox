package com.viking.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 自定义log输出
 * Created by Viking on 2020/8/2
 */
public class CustomLog {

    private static Class clas;


    public CustomLog(Class clas){
        CustomLog.clas = clas;
    }
    public void info(String info){
        System.out.println(getNow() + "\t== " + clas.getName() + "\t== " + info);
    }

    private static String getNow(){
        LocalDateTime now = LocalDateTime.now();
        return now.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS"));
    }
}

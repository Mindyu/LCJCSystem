package com.tomatoLCJC.main.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 杨 陈强 on 2017/7/7.
 */
public class FileHelper {

    private static File file;
    private static FileWriter fileWriter;

    public static boolean init(){
        Date date = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   //sdf.format(date)
        String filename = sdf.format(date)+".txt";
        file=new File("/mnt/sdcard/doc/",filename);
        try {
            fileWriter=new FileWriter(file,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileWriter==null? false:true;
    }

    public static void writeString(String s){
        if (fileWriter==null){
            init();
        }
        if (fileWriter!=null){
            try {
                fileWriter.write(s);
            } catch (Exception e) {
                //写入异常时
                e.printStackTrace();
            }
        }
    }

    public static void close(){
        if (fileWriter!=null){
            try {
                fileWriter.close();
                fileWriter=null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

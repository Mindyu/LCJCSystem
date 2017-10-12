package com.tomatoLCJC.main.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by zch22 on 2017/5/23.
 */
public class CopyDBToSDCard {

    private String DATABASE_NAME = "lcjcsystem";
    private String oldPath = "/data/data/com.tomatoLCJC/databases/" + DATABASE_NAME+".db";//注意这里包名是com.tomato不是com.tomato.main
    public String newPath = Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME+".db";//复制后路径

    public void copyDBToSDcrad()throws Exception {
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (!newfile.exists()) {
                newfile.createNewFile();
            }
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fouStream = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fouStream.write(buffer, 0, byteread);
                }
                inStream.close();
                fouStream.close();
            }
    }
}

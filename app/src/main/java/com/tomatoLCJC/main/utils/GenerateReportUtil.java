package com.tomatoLCJC.main.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;

import com.tomatoLCJC.main.fragment.FragmentDataMeasure;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by zch22 on 2017/5/27.
 */
public class GenerateReportUtil {
    private Context context;
    private FragmentDataMeasure fragmentDataMeasure;
    private final String resPath;             // 模板文件的地址
    public String newPath;                    // 新建文件的地址
    private int reportIndex;                 // 新建文件的后缀序号
    private String year;
    private String month;
    private String day;
    private String time;
    private Bitmap pic;

    public GenerateReportUtil(Context context,FragmentDataMeasure fragmentDataMeasure,int index){
        this.context=context;
        this.fragmentDataMeasure=fragmentDataMeasure;
        resPath = "/mnt/sdcard/doc/template2.txt";
        Date date = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        newPath = "/mnt/sdcard/doc/"+ sdf.format(date)+".doc"; // 更新新建文件的地址（模板和数据合并后的文件）
        reportIndex = index;
    }

    //将数据和模板合并生成Word报告
    public void generateReport(){
//        try {
//            initTime();         //初始化时间
//            InputStream inputStream = context.getAssets().open("template.doc"); // 得到模板文件的输入流
////            File f=new File(resPath);
//             /* if (!f.exists()) {
//                f.createNewFile();//如果文件不存在则创建
//            }*/
//            FileUtils.writeFile(new File(resPath), inputStream);  // 将 inputStream 的 byte 赋至 address 为 resPath 的文件
//            File resFile = new File(resPath);   // 获得（已存在）目录为 resPath 的文件（只包含模板文件）
//            File newFile = new File(newPath);   // 新建一个 address 为 newPath 的文件：newFile
//            Map<String, String> map = new HashMap<String, String>();// 新建一个 HashMap 用来存放相应位置的文本
//            map.put("$INDEX$", String.valueOf(reportIndex));
//            map.put("$YEAR$", year);
//            map.put("$MONTH$", month);
//            map.put("$DAYOFMONTH$", day);
//            map.put("$QYMC$", "武汉Tomato科技有限公司");
//            map.put("$QYDZ$", "钢管");
//            map.put("$QYFZR$", time);
//            map.put("$FRDB$", "钢");
//            map.put("$SCPZMSJWT$", "5");
//            map.put("$JLJJJFF$", "尾部");
//            writeDoc(resFile, newFile, map);// 将文本写入新建文件
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            initTime();         //初始化时间
            InputStream inputStream = context.getAssets().open("template2.txt"); // 得到模板文件的输入流
            FileUtils.writeFile(new File(resPath), inputStream);  // 将 inputStream 的 byte 赋至 address 为 resPath 的文件

            //获取图片并转化格式为base64
            FileInputStream fis = new FileInputStream(getChartPic());
            Bitmap bitmap  = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            String imageBase64 = new String(Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT));

            //获取模板文件
            BufferedReader bufReader =new BufferedReader(new InputStreamReader(new FileInputStream(new File(resPath))));

            StringBuffer strBuf = new StringBuffer();
            for (String tmp = null; (tmp = bufReader.readLine()) != null; tmp = null) {
                // 在这里做替换操作
                tmp = tmp.replaceFirst("MmmModiIndex", String.valueOf(reportIndex));
                tmp = tmp.replaceFirst("MmmModiYear", String.valueOf(year));
                tmp = tmp.replaceFirst("MmmModiMonth", String.valueOf(month));
                tmp = tmp.replaceFirst("MmmModiDay", String.valueOf(day));
                tmp = tmp.replaceFirst("MmmModiSydw", String.valueOf("武汉Tomato科技有限公司"));
                tmp = tmp.replaceFirst("MmmModiBjmc", String.valueOf("钢管"));
                tmp = tmp.replaceFirst("MmmModiYxsj", String.valueOf(time));
                tmp = tmp.replaceFirst("MmmModiBjcl", String.valueOf("钢"));
                tmp = tmp.replaceFirst("MmmModiCgqxs", String.valueOf("5"));
                tmp = tmp.replaceFirst("MmmModiJcbw", String.valueOf("尾部"));
                tmp = tmp.replaceFirst("MmmModiPic", imageBase64);
                strBuf.append(tmp);
                strBuf.append(System.getProperty("line.separator"));
            }
            bufReader.close();

            PrintWriter printWriter = new PrintWriter(newPath);
            printWriter.write(strBuf.toString().toCharArray());
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化与时间有关的成员变量
    private void initTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat timer = new SimpleDateFormat("HH:mm:ss ");
        year = String.valueOf(c.get(c.YEAR));
        month = String.valueOf(c.get(c.MONTH)+1);
        day = String.valueOf(c.get(c.DAY_OF_MONTH));
        time = timer.format(new java.util.Date());
    }

    /**
     * 将文本写入新建文件
     * @param resFile address 为 resPath 的文件
     * @param newFile address 为 newPath 的文件
     * @param map     需要存储的文本
     * */
    private void writeDoc(File resFile ,File newFile ,Map<String, String> map) {
        try {
            FileInputStream in = new FileInputStream(resFile);  // 新建一个用来存储 resFile 中的 raw bytes 的输入流：in
            HWPFDocument hdt = new HWPFDocument(in);  // 新建一个用来将转化 in 中的 raw bytes 至文本 的HWPFDocument：hdt
            Range range = hdt.getRange(); // 新建一个用来读取 hdt 中文本内容的 Range：range （此时 range 中包含模板文件的文本）
            for(Map.Entry<String, String> entry : map.entrySet()) { // 依次替换 range 中文本的标记部分（修改直接作用于 hdt 中的文本）
                range.replaceText(entry.getKey(), entry.getValue()); // 用 entry.getValue() 替换 entry.getKey()
            }
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            FileOutputStream out = new FileOutputStream(newFile, true);
            hdt.write(out);
            out.write(ostream.toByteArray());
            out.close();
            ostream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    //得到图表的截图，返回图片的存放路径
    private String  getChartPic(){
        String newPath = "/mnt/sdcard/doc/picCut1.png";
        File f = new File(newPath);
        if (f.exists()){
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            fragmentDataMeasure.picCut().compress(Bitmap.CompressFormat.PNG,100, out);//90的品质，就保留原图像90%的品质
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newPath;
    }


    /**
     * 调用手机中安装的可打开word的软件
     */
    private void doOpenWord(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        String fileMimeType = "application/msword";
        intent.setDataAndType(Uri.fromFile(new File(newPath)), fileMimeType);
        try{
            context.startActivity(intent);
        } catch(ActivityNotFoundException e) {
            Toast.makeText(context, "未找到软件", Toast.LENGTH_LONG).show();
        }
    }



    //删除指定文件夹下所有文件
    //param path 文件夹完整绝对路径
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    //删除文件夹
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}

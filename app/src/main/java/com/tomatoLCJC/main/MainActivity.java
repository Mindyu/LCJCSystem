package com.tomatoLCJC.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tomatoLCJC.main.fragment.FragmentDataMeasure;
import com.tomatoLCJC.main.fragment.FragmentHistoryData;
import com.tomatoLCJC.main.fragment.SecondFragmentDataMeasure;
import com.tomatoLCJC.main.utils.CopyDBToSDCard;
import com.tomatoLCJC.main.utils.FileHelper;
import com.tomatoLCJC.main.utils.GenerateReportUtil;
import com.tomatoLCJC.main.utils.MainDialog;
import com.tomatoLCJC.main.utils.Quadratic;
import com.tomatoLCJC.main.utils.WaveletProcess;
import com.tomatoLCJC.thread.DataProcessThread;
import com.tomatoLCJC.thread.HandleStopThread;
import com.tomatoLCJC.thread.ReadDataThread;
import com.tomatoLCJC.tools.Parameter.SystemParameter;
import com.tomatoLCJC.tools.Parameter.ThreadParameter;
import com.tomatoLCJC.tools.dataBase.Dao.DeviceDetectionRecordDao;
import com.tomatoLCJC.usbutil.SerialPortOpe;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 主界面：用于控制多个页面（fragment）的切换以及组件的点击事件
 */
public class MainActivity extends FragmentActivity implements OnClickListener {
    private SerialPortOpe           serialPortOpe = null;       //串口操作工具类

    private ViewPager               m_vViewPager;               //声明ViewPager

    private FragmentPagerAdapter    m_vFmAdapter;               //适配器
    private List<Fragment>          m_vFragments;               //装载Fragment的集合

    //四个fragment
    private FragmentDataMeasure         m_vFragmentDataMeasure;
    private SecondFragmentDataMeasure   m_vSecondFragmentDataMeasure;
    private FragmentHistoryData         m_vFragmentHistoryData;

    //两个Tab对应的布局
    private LinearLayout m_vTabDtMeasure;
    private LinearLayout m_vTabHistoryDt;

    private ImageView   menu;                   //菜单按钮
    private ImageView   m_vImgDtMeasure;        //磁力测量按钮
    private ImageView   m_vImgHistoryDt;        //历史数据按钮
    private ImageButton generatReport;          //生成报告按钮
    private ImageButton exportData;             //导出数据按钮
    private ImageButton btnStart;               //开始按钮
    private ImageButton btnStop;                //停止按钮
    private ImageButton btnClean;               //清屏按钮

    //用于表示状态的FLAG
    private boolean ifClickIsStart = true;      //判断点击的按钮是否是开始按钮
    private boolean ifFirstStart = true;        //判断是否是第一次点击开始按钮
    public  boolean ifIsStopState = true;       //判断是否是停止状态,点击开始后变为false

    private GenerateReportUtil generateReportUtil;
    private CopyDBToSDCard copyDBToSDCard;
    private int     reportIndex = 1;                    // 生成word的编号
    private long    mExitTime = 0;
    private static ProgressDialog myDialog;             //数据采集中的进度条
    private Quadratic quadratic_1 = new Quadratic();
    private Quadratic quadratic_2 = new Quadratic();

    //刷新历史纪录列表的Handler
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {                 //读取数据中，更新弹框
                myDialog.setProgress(ThreadParameter.getInstance().xList.size() % 100);
            } else if (msg.what == 1) {           //测量完成，
                myDialog.setProgress(0);
                myDialog.dismiss();
                btnStart.callOnClick();
            } else if (msg.what == 2) {           //更新界面
                m_vFragmentDataMeasure.chartView.postInvalidate();
                m_vFragmentDataMeasure.scanView.postInvalidate();
                m_vSecondFragmentDataMeasure.chartView_1.postInvalidate();
                m_vSecondFragmentDataMeasure.chartView_2.postInvalidate();
            } else if (msg.what == 3) {         //刷新历史纪录列表的Handler
                Bundle bundle = msg.getData();
                String year = bundle.getString("year");
                String month = bundle.getString("month");
                String day = bundle.getString("day");
                m_vFragmentHistoryData.refreshListView(year, month, day);
            } else if (msg.what == 4) {          //暂停处理

            }
        }
    };

    //先绘制图线再保存数据到数据库
    class MyThread implements Runnable {
        @Override
        public void run() {
            if (ThreadParameter.getInstance().xList.size() == 0) {
                return;
            }
            if (ThreadParameter.getInstance().denoisingValue.get(0).size() != ThreadParameter.getInstance().xList.size()) {
                ThreadParameter.getInstance().denoisingValue = WaveletProcess.DenoisingCheckData(ThreadParameter.getInstance().xList.size(), SystemParameter.getInstance().nChannelNumber, ThreadParameter.getInstance().detectionValue, 1);
            }
            quadratic_1.setParameter(DeviceDetectionRecordDao.getInstance().getStatusIsZeroBean().getValue_a(), DeviceDetectionRecordDao.getInstance().getStatusIsZeroBean().getValue_b(), 0);
            quadratic_2.setParameter(DeviceDetectionRecordDao.getInstance().getStatusIsZeroBean().getValue_c(), DeviceDetectionRecordDao.getInstance().getStatusIsZeroBean().getValue_d(), DeviceDetectionRecordDao.getInstance().getStatusIsZeroBean().getValue_e());
            double tempValue, flaw;
            double dividingLine = quadratic_1.argTOdep(40);     //获取缺陷比为40%所对应的y值
//            Log.d("dividing", String.valueOf(dividingLine));
            for (int i = 0; i < ThreadParameter.getInstance().xList.size(); i++) {
                for (int j = 0; j < SystemParameter.getInstance().nChannelNumber; j++) {
                    tempValue = ThreadParameter.getInstance().denoisingValue.get(j).get(i);
                    if (tempValue < 0) {
                        ThreadParameter.getInstance().flawValue.get(j).add(0.0);   //去噪值小于0的时候直接赋百分比为0
                    } else if (tempValue <= dividingLine) {
                        flaw = quadratic_1.depTOarg(tempValue);    //计算缺陷值所对应的百分比
                        ThreadParameter.getInstance().flawValue.get(j).add(flaw);
                    } else {
                        flaw = quadratic_2.depTOarg(tempValue);
                        ThreadParameter.getInstance().flawValue.get(j).add(flaw);
                    }
                }
            }
            m_vFragmentDataMeasure.chartView.drawView(ThreadParameter.getInstance().xList, ThreadParameter.getInstance().denoisingValue);
            m_vFragmentDataMeasure.scanView.drawView(ThreadParameter.getInstance().xList, ThreadParameter.getInstance().yList, ThreadParameter.getInstance().flawValue);
            m_vSecondFragmentDataMeasure.chartView_1.drawView(ThreadParameter.getInstance().xList, ThreadParameter.getInstance().detectionValue);     //检测折线图
            m_vSecondFragmentDataMeasure.chartView_2.drawView(ThreadParameter.getInstance().xList, ThreadParameter.getInstance().gradientValue);     //横向梯度折线图
            if (ifIsStopState) {
                HandleStopThread handleStopThread = new HandleStopThread(handler);
                handleStopThread.start();       //启动停止处理线程，将数据保存起来
            }
            Message msg = new Message();
            msg.what = 2;
            handler.sendMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        设置手机通知栏样式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
//        去标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        锁定竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_mian_fragment);
        /*//设置屏幕常亮
        //**********方法修改为 在当前布局文件中，随便给一个UI组件设置:android:keepScreenOn="true"
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
        wakeLock.acquire();*/
        //为广播接收器创建过滤器
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(500);
        //注册广播接收器
        this.registerReceiver(mUsbReceiver, filter);
        initViews();//初始化控件
        initDatas();//初始化数据
        initEvents();//初始化事件
    }

    //初始化控件
    private void initViews() {
        m_vViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        //磁力测量和历史数据两个ImageView的布局
        m_vTabDtMeasure = (LinearLayout) findViewById(R.id.id_tab_datameasure);
        m_vTabHistoryDt = (LinearLayout) findViewById(R.id.id_tab_historydata);
        //磁力测量和历史数据两个ImageView
        m_vImgDtMeasure = (ImageView) findViewById(R.id.btn_magnetic_measurement);
        m_vImgDtMeasure.setImageResource(R.drawable.btn_magnetic_measurement_selected);//默认点击第一个tab
        m_vImgHistoryDt = (ImageView) findViewById(R.id.btn_history);

        generatReport = (ImageButton) findViewById(R.id.btn_GeneratReport);
        exportData = (ImageButton) findViewById(R.id.btn_ExportData);
        menu = (ImageView) findViewById(R.id.menu);

        btnStart = (ImageButton) findViewById(R.id.btnStart);
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        btnClean = (ImageButton) findViewById(R.id.btnClean);
        //初始只有开始按钮能点
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        btnClean.setEnabled(false);
    }

    //初始化数据
    private void initDatas() {
        //初始化串口操作对象
        serialPortOpe = SerialPortOpe.getInstance(this);

        //初始化Fragment
        m_vFragmentDataMeasure = FragmentDataMeasure.getInstance();
        m_vSecondFragmentDataMeasure = SecondFragmentDataMeasure.getInstance();
        m_vFragmentHistoryData = new FragmentHistoryData();

        //先预加载历史数据这个Fragment，初始化其组件，否则点击停止会报异常
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(m_vFragmentHistoryData);
        transaction.replace(R.id.main_frame_layout, m_vFragmentHistoryData);
        transaction.commit();

        //将三个Fragment加入集合中
        m_vFragments = new ArrayList<>();
        m_vFragments.add(m_vFragmentDataMeasure);
        m_vFragments.add(m_vSecondFragmentDataMeasure);
        //初始化适配器
        m_vFmAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {      //从集合中获取对应位置的Fragment
                return m_vFragments.get(position);
            }

            @Override
            public int getCount() {//获取集合中Fragment的总数
                return m_vFragments.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //super.destroyItem(container, position, object);
            }
        };
        //不要忘记设置ViewPager的适配器
        m_vViewPager.setAdapter(m_vFmAdapter);
        m_vViewPager.setOffscreenPageLimit(1);//设置加载Fragment数，默认是1，当前及左右的Fragment，设置为2，即每个都重新加载
        //设置ViewPager的切换监听
        //***********显示方法过时过时将setOnPageChangeListener转为addOnPageChangeListener
        m_vViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //页面滚动事件
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            //页面选中事件
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        m_vViewPager.setCurrentItem(0);
                        break;
                    case 1:
                        m_vViewPager.setCurrentItem(1);
                        break;
                    case 2:
                        m_vViewPager.setCurrentItem(2);
                        break;
                }
                m_vImgDtMeasure.setImageResource(R.drawable.btn_magnetic_measurement_selected);
                m_vImgHistoryDt.setImageResource(R.drawable.btn_history_normal);
            }

            @Override
            //页面滚动状态改变事件 0-静止 1-正在滑动 2-滑动结束
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    //重写双击返回的方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 1000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于1000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //初始化事件
    private void initEvents() {
        //为两个ImageView的布局设置点击事件
        m_vTabDtMeasure.setOnClickListener(this);
        m_vTabHistoryDt.setOnClickListener(this);
        //菜单点击事件
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        //生成报告点击事件
        generatReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    m_vFragmentDataMeasure.chartView.resetAxis();
                    m_vFragmentDataMeasure.scanView.resetAxis();
                    if (Math.abs(m_vFragmentDataMeasure.chartView.getxDistance()- 0.1) < 0.0001 &&
                            Math.abs(m_vFragmentDataMeasure.chartView.getxDistance() - 300) < 0.0001 &&
                            Math.abs(m_vFragmentDataMeasure.chartView.getY()) < 0.0001) {
                        Toast.makeText(MainActivity.this, "请先选择一组数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    generateReportUtil = new GenerateReportUtil(getApplication(), m_vFragmentDataMeasure, reportIndex);
                    try {
                        generateReportUtil.generateReport();
                        reportIndex++;//报告编号

                        final Dialog dialogSendReport = new Dialog(MainActivity.this, R.style.dialog);
                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_send_report, null);
                        dialogSendReport.setContentView(view);
                        view.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
                        view.getLayoutParams().height = 500;
                        dialogSendReport.getWindow().setGravity(Gravity.BOTTOM);
                        dialogSendReport.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
                        dialogSendReport.show();

                        ImageView sendFromQQ = (ImageView) view.findViewById(R.id.send_from_qq);
                        ImageView sendFromWeChat = (ImageView) view.findViewById(R.id.send_from_weichat);
//                        ImageView sendFromMail = (ImageView) view.findViewById(R.id.send_from_mail);

                        sendFromQQ.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent sendReport = new Intent(Intent.ACTION_SEND);
                                sendReport.setType("text/plain");
                                sendReport.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(generateReportUtil.newPath)));
                                sendReport.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
                                startActivity(Intent.createChooser(sendReport, "发送报告"));
                                dialogSendReport.dismiss();
                            }
                        });

                        sendFromWeChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent sendReport = new Intent(Intent.ACTION_SEND);
                                sendReport.setType("text/plain");
                                sendReport.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(generateReportUtil.newPath)));
                                sendReport.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                                startActivity(Intent.createChooser(sendReport, "发送报告"));
                                dialogSendReport.dismiss();
                            }
                        });

                        /*sendFromMail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent sendReport = new Intent(Intent.ACTION_SEND);
                                File file = new File(generateReportUtil.newPath);
                                sendReport.setType("application/octet-stream");
                                sendReport.setType("image*//*");
                                sendReport.setType("message/rfc882");
                                sendReport.putExtra(Intent.EXTRA_EMAIL, "");
                                sendReport.putExtra(Intent.EXTRA_SUBJECT, "检测报告");
                                sendReport.putExtra(Intent.EXTRA_TEXT, "");
                                sendReport.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                                startActivity(Intent.createChooser(sendReport, "请选择邮件发送软件"));
                            }
                        });*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //导出数据点击事件
        exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将.db文件复制到SD卡,然后发送
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    copyDBToSDCard = new CopyDBToSDCard();
                    try {
                        copyDBToSDCard.copyDBToSDcrad();

                        final Dialog dialogSendReport = new Dialog(MainActivity.this, R.style.dialog);
                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_send_report, null);
                        dialogSendReport.setContentView(view);
                        view.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
                        view.getLayoutParams().height = 500;
                        dialogSendReport.getWindow().setGravity(Gravity.BOTTOM);
                        dialogSendReport.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
                        dialogSendReport.show();

                        ImageView sendFromQQ = (ImageView) view.findViewById(R.id.send_from_qq);
                        ImageView sendFromWeChat = (ImageView) view.findViewById(R.id.send_from_weichat);
//                        ImageView sendFromMail = (ImageView) view.findViewById(R.id.send_from_mail);

                        sendFromQQ.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent sendReport = new Intent(Intent.ACTION_SEND);
                                sendReport.setType("text/plain");
                                sendReport.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(copyDBToSDCard.newPath)));
                                sendReport.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
                                startActivity(Intent.createChooser(sendReport, "发送报告"));
                                dialogSendReport.dismiss();
                            }
                        });

                        sendFromWeChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent sendReport = new Intent(Intent.ACTION_SEND);
                                sendReport.setType("text/plain");
                                sendReport.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(copyDBToSDCard.newPath)));
                                sendReport.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                                startActivity(Intent.createChooser(sendReport, "发送报告"));
                                dialogSendReport.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "请打开存储权限", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                        startActivityForResult(intent, 0); //此为设置完成后返回到获取界面
                    }
                }
            }

        });

        //设置开始按钮的点击事件
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ifFirstStart) {
                    btnClean.callOnClick();
                    //如果初始化并打开设备成功，则返回true,否则返回false
                    serialPortOpe.createDeviceList();
                    boolean flag = serialPortOpe.initDevice();
                    if (flag) {
                        if (myDialog == null) {
                            myDialog = new ProgressDialog(MainActivity.this);
                            myDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                            myDialog.setTitle("请稍后");
                            myDialog.setMessage("\n  数据采集中...");
                            myDialog.setMax(100);
                            myDialog.setCancelable(false);
                            myDialog.setProgressNumberFormat(null);      //不显示进度条右侧的百分比    12/100
                            myDialog.setProgressPercentFormat(null);     //不显示进度条左侧的百分比    12%
                        }
                        myDialog.setProgress(0);
                        myDialog.show();
                        ThreadParameter.getInstance().initThreadParameter();    //初始化参数
                        ThreadParameter.getInstance().ifHaveEncoder = true;     //以有位移的方式处理数据
                        ThreadParameter.getInstance().threadFlag = true;        //打开线程开关
                        ReadDataThread readDataThread = new ReadDataThread(MainActivity.this);
                        ReadDataThread.isPause = false;
                        new Thread(readDataThread).start();            //启动读数据线程
                        DataProcessThread dataProcessThread = new DataProcessThread(handler, 0);
                        new Thread(dataProcessThread).start();         //启动数据处理线程
                        btnStart.setImageResource(R.drawable.btn_suspend_normal);
                        btnStop.setImageResource(R.drawable.stop_normal);
                        btnClean.setImageResource(R.drawable.clean_normal);
                        btnStop.setEnabled(true);
                        btnClean.setEnabled(false);         //开始测量后不可清屏
                        menu.setEnabled(false);             //开始测量后菜单不可点击
                        m_vTabHistoryDt.setEnabled(false);  //开始测量后历史数据不可点击
                        ifFirstStart = false;
                        ifClickIsStart = false;
                        ifIsStopState = false;
//                        Log.d("data",String.valueOf(serialPortOpe.getMeasureData()));
                    }
                } else {
                    if (ifClickIsStart) {     //按下开始
                        myDialog.show();
                        ReadDataThread.isPause = false;
                        btnStart.setImageResource(R.drawable.btn_suspend_normal);
                        ifClickIsStart = false;
                    } else {          //按下暂停
                        ReadDataThread.isPause = true;
                        btnStart.setImageResource(R.drawable.start_normal);   //开始
                        ifClickIsStart = true;
                        //绘制图线线程
                        new Thread(new MyThread()).start();
                        //弹框显示是否继续测量
                        //系统默认的AlertDialog
//                        showMultiBtnDialog();
                        DecimalFormat df = new DecimalFormat("#0.0000");
                        double leng = ThreadParameter.getInstance().xList.get(ThreadParameter.getInstance().xList.size()-1);
                        String text = "已测量"+ df.format(leng) +"m距离";
                        //自定义弹出框
                        showCustomDialog( text );
                    }
                }
            }
        });
        //设置停止按钮的点击事件
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ifFirstStart = true;
                ifClickIsStart = true;
                ifIsStopState = true;
                btnStart.setEnabled(true);
                btnStart.setImageResource(R.drawable.start_normal);
                btnStop.setEnabled(false);
                btnStop.setImageResource(R.drawable.stop_select);
                btnClean.setEnabled(true);             //停止后可以点击清屏
                btnClean.setImageResource(R.drawable.clean_normal);
                menu.setEnabled(true);              //停止后菜单变为可以点击
                m_vTabHistoryDt.setEnabled(true);   //停止后历史数据可点击
                serialPortOpe.stopReciveData();    //停止接收数据，关闭设备
                ThreadParameter.getInstance().threadFlag = false;     //停止线程
                //绘制图线线程
                new Thread(new MyThread()).start();
            }
        });

        // 清屏按钮的点击事件：只有点击停止之后清屏按钮才变为可点
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnClean.setImageResource(R.drawable.clean_selected);
                btnClean.setEnabled(false);
                btnStop.setImageResource(R.drawable.stop_normal);
                clearAllChart(); // 清空图表
            }

        });
    }

    //清空所有图表并重新初始化图表和通道按钮数
    private void clearAllChart() {
        m_vFragmentDataMeasure.cleanChart();
        m_vSecondFragmentDataMeasure.cleanChart();
    }

    @Override
    public void onClick(View v) {   //根据点击的Tab切换不同的页面及设置对应的背景
        switch (v.getId()) {
            case R.id.id_tab_datameasure:
                findViewById(R.id.id_viewpager).setVisibility(View.VISIBLE);
                m_vViewPager.setCurrentItem(0, false);         //设置显示第一个Fragment
                m_vImgDtMeasure.setImageResource(R.drawable.btn_magnetic_measurement_selected);
                m_vImgHistoryDt.setImageResource(R.drawable.btn_history_normal);
                findViewById(R.id.activity_main_bottom).setVisibility(View.VISIBLE);
                break;
            case R.id.id_tab_historydata:
                findViewById(R.id.id_viewpager).setVisibility(View.GONE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.show(m_vFragmentHistoryData);
                transaction.replace(R.id.main_frame_layout, m_vFragmentHistoryData);
                transaction.commit();
                m_vImgDtMeasure.setImageResource(R.drawable.btn_magnetic_measurement_normal);
                m_vImgHistoryDt.setImageResource(R.drawable.btn_history_selected);
                findViewById(R.id.activity_main_bottom).setVisibility(View.GONE);//设置Activity下面的三个按钮再此Fragment中不显示
                break;
        }
    }

    //按下返回键的处理事件
    @Override
    public void onBackPressed() {
        //如果是停止状态则finish(),测量状态则返回键不可点
        if (ifIsStopState) {
            super.onBackPressed();//此方法会调用finish()方法
        } else {
            Toast.makeText(MainActivity.this, "请先结束测量", Toast.LENGTH_LONG).show();
        }
    }

    //USB广播接收器
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
                Toast.makeText(MainActivity.this, "设备已接入", Toast.LENGTH_LONG).show();
                serialPortOpe.createDeviceList();//更新设备数
            }
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Toast.makeText(MainActivity.this, "设备已拔出", Toast.LENGTH_LONG).show();
                handleDetached();//测量中途拔出设备的处理函数
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        FileHelper.close();
        this.unregisterReceiver(mUsbReceiver);//注销广播接收器
    }

    //拔掉设备后的处理函数
    public void handleDetached() {
        /**
         * 如果不是停止状态，表示在测量过程中拔出设备，应进行下面处理，否则不处理
         * 由于拔出设备后会收到多个广播，ifIsStopState能保证handleDetached()只会被执行一次
         */
        if (!ifIsStopState) {
            btnStop.callOnClick();
            ThreadParameter.getInstance().initThreadParameter();   //停止后重新初始化参数
        }
    }


    /************************************供外部调用的方法*****************************************/
    //fragment之间跳转页面
    public void jumpToFirstFragment() {
        findViewById(R.id.id_viewpager).setVisibility(View.VISIBLE);
        m_vViewPager.setCurrentItem(0, false);         //设置显示第一个Fragment
        m_vImgDtMeasure.setImageResource(R.drawable.btn_magnetic_measurement_selected);
        m_vImgHistoryDt.setImageResource(R.drawable.btn_history_normal);
        findViewById(R.id.activity_main_bottom).setVisibility(View.VISIBLE);
    }

    //在历史数据页面点击每条记录后跳转到在测量页面时显示图表的方法(在此方法中根据传过来的通道数初始化按钮数，图标的渲染器数据集的数量)
    public void showChart(List<Double> xList, List<ArrayList<Double>> yList, List<ArrayList<Double>> detectionValue, List<ArrayList<Double>> gradientValue, List<ArrayList<Double>> denoisingValue, List<ArrayList<Double>> flawValue, String title, int channelNum) {
        m_vFragmentDataMeasure.drawHistoryChart(xList, yList, denoisingValue, flawValue, title, channelNum);  //绘制第一个页面
        m_vSecondFragmentDataMeasure.drawHistoryChart(xList, detectionValue, gradientValue, title, channelNum);//绘制第二个页面
    }

    //写在这里再DialogSpinner中调用
    public void refreshHistoryData(String year, String month, String day) {
        m_vFragmentHistoryData.refreshListView(year, month, day);
    }

    public void showMultiBtnDialog() {
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setCancelable(false);
        normalDialog.setMessage("你要点击哪一个按钮呢?");
        normalDialog.setNegativeButton("继续测量",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnStart.callOnClick();
                    }
                });
        normalDialog.setNeutralButton("完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                btnStop.callOnClick();
                ifFirstStart = true;
                ifClickIsStart = true;
                ifIsStopState = true;
                btnStart.setEnabled(true);
                btnStart.setImageResource(R.drawable.start_normal);
                btnStop.setEnabled(false);
                btnStop.setImageResource(R.drawable.stop_select);
                btnClean.setEnabled(true);             //停止后可以点击清屏
                btnClean.setImageResource(R.drawable.clean_normal);
                menu.setEnabled(true);              //停止后菜单变为可以点击
                m_vTabHistoryDt.setEnabled(true);   //停止后历史数据可点击
                serialPortOpe.stopReciveData();    //停止接收数据，关闭设备
                ThreadParameter.getInstance().threadFlag = false;     //停止线程
                HandleStopThread handleStopThread = new HandleStopThread(handler);
                handleStopThread.start();       //启动停止处理线程，将数据保存起来
            }
        });
        normalDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ifFirstStart = true;
                ifClickIsStart = true;
                ifIsStopState = true;
                btnStart.setEnabled(true);
                btnStart.setImageResource(R.drawable.start_normal);
                btnStop.setEnabled(false);
                btnStop.setImageResource(R.drawable.stop_select);
                btnClean.setEnabled(true);             //停止后可以点击清屏
                btnClean.setImageResource(R.drawable.clean_normal);
                menu.setEnabled(true);              //停止后菜单变为可以点击
                m_vTabHistoryDt.setEnabled(true);   //停止后历史数据可点击
                serialPortOpe.stopReciveData();    //停止接收数据，关闭设备
                ThreadParameter.getInstance().threadFlag = false;     //停止线程
            }
        });
        // 创建实例并显示
        normalDialog.show();
    }

    public void showCustomDialog(String text){
        MainDialog mainDialog = new MainDialog(MainActivity.this, text);
        mainDialog.setMainDialgClickListener(new MainDialog.mainDialgClickListener() {
            @Override
            public void goonClick() {
                btnStart.callOnClick();
            }

            @Override
            public void finishClick() {
                ifFirstStart = true;
                ifClickIsStart = true;
                ifIsStopState = true;
                btnStart.setEnabled(true);
                btnStart.setImageResource(R.drawable.start_normal);
                btnStop.setEnabled(false);
                btnStop.setImageResource(R.drawable.stop_select);
                btnClean.setEnabled(true);             //停止后可以点击清屏
                btnClean.setImageResource(R.drawable.clean_normal);
                menu.setEnabled(true);              //停止后菜单变为可以点击
                m_vTabHistoryDt.setEnabled(true);   //停止后历史数据可点击
                serialPortOpe.stopReciveData();    //停止接收数据，关闭设备
                ThreadParameter.getInstance().threadFlag = false;     //停止线程
                HandleStopThread handleStopThread = new HandleStopThread(handler);
                handleStopThread.start();       //启动停止处理线程，将数据保存起来
            }

            @Override
            public void cancleClick() {
                ifFirstStart = true;
                ifClickIsStart = true;
                ifIsStopState = true;
                btnStart.setEnabled(true);
                btnStart.setImageResource(R.drawable.start_normal);
                btnStop.setEnabled(false);
                btnStop.setImageResource(R.drawable.stop_select);
                btnClean.setEnabled(true);             //停止后可以点击清屏
                btnClean.setImageResource(R.drawable.clean_normal);
                menu.setEnabled(true);              //停止后菜单变为可以点击
                m_vTabHistoryDt.setEnabled(true);   //停止后历史数据可点击
                serialPortOpe.stopReciveData();    //停止接收数据，关闭设备
                ThreadParameter.getInstance().threadFlag = false;     //停止线程
            }
        });
        mainDialog.show();
    }
}

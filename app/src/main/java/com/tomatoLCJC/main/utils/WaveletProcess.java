package com.tomatoLCJC.main.utils;

import com.tomatoLCJC.main.utils.WaveletUtils.DWT;
import com.tomatoLCJC.main.utils.WaveletUtils.Wavelet;

import org.bytedeco.javacpp.SizeTPointer;
import org.bytedeco.javacpp.gsl;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.gsl.*;
/**
 * Created by wzh on 2017/8/30.
 */

public class WaveletProcess {
    static {
        System.loadLibrary("jnigsl");            //加载.so文件
        System.loadLibrary("gsl");                //加载.so文件
    }

    /***************************************************************/
    //功能：利用GSL小波变换对原始数据去噪
    //参数：sourceData：原始数据；reData：去噪后数据，返回值；len：原始数据长度
    /***************************************************************/
    public static ArrayList<Double> DWTDenoising( double[] sourceData, int len) {
        ArrayList<Double> reData = new ArrayList<Double>();
        int i;
        double n;						//处理数据的size大小  需要是2的指数倍
        double a = Math.log(len)/Math.log(2);
        int Level = (int)a;
        if (a > Level)
            Level = Level+1;
        n = Math.pow(2, Level);

        //将原始数据赋值给data
        double []data = new double[(int)n];
        double []dwt = new double[(int)n];
        double []outputData = new double[(int)n];
        for (i = 0; i < n; i++)
        {
            if (i < len)
                data[i] = sourceData[i];
            else
                data[i] = sourceData[len-1];				//数据不够时 用最后一个数据填充
        }
        Wavelet wavelet = Wavelet.Daubechies;int order = 10;int L = 3;//wavelet是小波类型；order是小波类型的参数；L是coarsest scale（最粗糙的尺度）

        try {
            dwt = DWT.forwardDwt(data, wavelet, order, L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //去掉低频信号
        for (i =0; i < (int)Math.pow(2, L); i++)
        {
            dwt[i] = 0;
        }
        //去掉高频信号第一层
        for (i = (int)Math.pow(2, Level-1); i < (int)Math.pow(2, Level); i++)
        {
            dwt[i] = 0;
        }
        try {
            outputData = DWT.inverseDwt(dwt,wavelet,order, L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //将处理后的数据赋值给reData
        for (i = 0; i < len; i++)
        {
            reData.add(outputData[i]);   //减去基值 这里把data[0]当成基础值测试
        }
        return reData;
    }
    /***************************************************************/
    //功能：利用GSL小波变换对原始数据去噪
    //参数：sourceData：原始数据；reData：去噪后数据，返回值；len：原始数据长度
    /***************************************************************/
    public static ArrayList<Double> GSLDenoising( double[] sourceData, int len)
    {
        ArrayList<Double> reData = new ArrayList<Double>();
        int i;
        double n;						//处理数据的size大小  需要是2的指数倍
        double a = Math.log(len)/Math.log(2);
        int Level = (int)a;
        if (a > Level)
            Level = Level+1;
        n = Math.pow(2, Level);
        double []data = new double[(int)n];

        gsl_wavelet w;
        gsl_wavelet_workspace work;

        w = gsl_wavelet_alloc(gsl_wavelet_daubechies(), 10);
        work = gsl_wavelet_workspace_alloc((int)n);

        //将原始数据赋值给data
        for (i = 0; i < n; i++)
        {
            if (i < len)
                data[i] = sourceData[i];
            else
                data[i] = sourceData[len-1];				//数据不够时 用最后一个数据填充
        }

        gsl_wavelet_transform_forward(w, data,1, (int)n, work);

        //去掉低频信号
        for (i =0; i < (int)Math.pow(2, 0); i++)
        {
            data[i] = 0;
        }
        //去掉高频信号第一层
        for (i = (int)Math.pow(2, Level-1); i < (int)Math.pow(2, Level); i++)
        {
            data[i] = 0;
        }


        gsl_wavelet_transform_inverse(w, data, 1, (int)n, work);

        //将处理后的数据赋值给reData
        for (i = 0; i < len; i++)
        {
            reData.add(data[i] - data[0]);   //减去基值 这里把data[0]当成基础值测试
        }

        gsl_wavelet_free(w);
        gsl_wavelet_workspace_free(work);

        /*free(data);
        free(abscoeff);
        free(p);*/
        return reData;
    }

    /*******************************************************************************************
     * 函数名称：GSLDenoisingCheckData
     * 函数介绍：对原始数据去噪
     * 输入参数：(1)len:原始数据的个数;（2）nSensorNumber：通道数量；（3）CheckSourceData：原始数据；(4)type:去噪的方法：1是gsl；2是dwt
     * 输出参数：（1）denoiseDataBuffer：去噪后的数据
     * 返回值  ：去噪后的数据；
     ********************************************************************************************/
    public static List<ArrayList<Double>> DenoisingCheckData(int len, int nSensorNumber, List<ArrayList<Double>> CheckSourceData,int type)
    {

        List<ArrayList<Double>> denoiseDataBuffer =new ArrayList<>();
        for (int i = 0; i < nSensorNumber; i++)
        {
            double[] sourceData = new double[len];

            for (int Index = 0; Index < len; Index++)
            {
                sourceData[Index] = CheckSourceData.get(i).get(Index);
            }
            ArrayList<Double> reData = new ArrayList<Double>();
            if(type==1)
                reData= GSLDenoising(sourceData, len );
            else if(type == 2)
                reData = DWTDenoising(sourceData, len );

            denoiseDataBuffer.add(reData);

            //delete[]sourceData;
            //delete[]reData;
        }
        //数据去噪处理结束
        return denoiseDataBuffer;
    }

}

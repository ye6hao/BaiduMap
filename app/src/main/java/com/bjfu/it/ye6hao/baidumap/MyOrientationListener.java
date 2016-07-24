package com.bjfu.it.ye6hao.baidumap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by ye6hao on 16/7/22.
 */
public class MyOrientationListener implements SensorEventListener {


    private SensorManager mSensorManager;//传感器管理者
    private Context mContext;
    private Sensor mSensor;

    private float lastX; //X轴


    public void start(){

        mSensorManager= (SensorManager) mContext
                .getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager!=null){
            //获得方向传感器
           mSensor= mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }

        if(mSensor!=null){
            mSensorManager.registerListener(this,mSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }


    public void stop(){
        mSensorManager.unregisterListener(this);
    }


    //构造函数
    public MyOrientationListener(Context context){
        this.mContext=context;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    //方向发生变化
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ORIENTATION){
            float x=sensorEvent.values[SensorManager.DATA_X];

            //避免特别快更新UI
            if(Math.abs(x-lastX)>1.0){
                if(mOnOrientationListener!=null){
                    mOnOrientationListener.onOrientationChanged(x);
                }


            }

            lastX=x;
        }

    }


    private OnOrientationListener mOnOrientationListener;

    public void setOnOrientationListener(OnOrientationListener mOnOrientationListener) {
        this.mOnOrientationListener = mOnOrientationListener;
    }



    public interface OnOrientationListener{
          void onOrientationChanged(float x);
    }

}

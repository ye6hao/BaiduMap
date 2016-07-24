package com.bjfu.it.ye6hao.baidumap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ye6hao on 16/7/23.
 */
public class Info implements Serializable{

    private static final long serialVersionUID = -1010711775392052966L;


    private  double latitude;       //纬度
    private double  longtitude;     //经度
    private int imgId;              //图片资源id
    private String name;            //商家
    private String distance;        //
    private  int zan;               //点赞数量

    public static List<Info> infos=new ArrayList<Info>();

    /*
    ＊手动添加4个数值
    * 在实际开发中，需要从服务器获得json数据
    */
    static
    {
        infos.add(new Info(34.242652, 108.971171, R.drawable.a01, "英伦贵族小旅馆",
                "距离209米", 1456));
        infos.add(new Info(34.242952, 108.972171, R.drawable.a02, "沙井国际洗浴会所",
                "距离897米", 456));
        infos.add(new Info(34.242852, 108.973171, R.drawable.a03, "五环服装城",
                "距离249米", 1456));
        infos.add(new Info(34.242152, 108.971971, R.drawable.a04, "老米家泡馍小炒",
                "距离679米", 1456));
    }




    //构造函数
    public Info(double latitude, double longtitude, int imgId, String name, String distance, int zan) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.imgId = imgId;
        this.name = name;
        this.distance = distance;
        this.zan = zan;
    }





    public int getZan() {
        return zan;
    }

    public void setZan(int zan) {
        this.zan = zan;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }




}

package com.bjfu.it.ye6hao.baidumap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private Context context;


    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private boolean isFirstIn = true; //是否第一次定位，弹出Toast中文位置
    //记录当前的经纬度
    private double mLatitude;
    private double mLongtitude;

    //传感器 自定义定位图标
    private BitmapDescriptor mIconLocation;             //方向图标
    private MyOrientationListener myOrientationListener;//方向监听
    private float mCurrentX;                            //但前X发生变化
    private MyLocationConfiguration.LocationMode mLocationMode;


    //覆盖物相关
    private BitmapDescriptor mMarker;
    private RelativeLayout mMarkerLy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        this.context = this;//干啥的？？？？

        //初始化控件
        initView();

        //初始化定位
        initLoaction();

        //初始化覆盖物
        initMarker();


        //为覆盖物添加点击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Bundle extraInfo = marker.getExtraInfo();
                Info info = (Info) extraInfo.getSerializable("info");
                ImageView iv = (ImageView) mMarkerLy
                        .findViewById(R.id.id_info_img);
                TextView distance = (TextView) mMarkerLy
                        .findViewById(R.id.id_info_distance);
                TextView name = (TextView) mMarkerLy
                        .findViewById(R.id.id_info_name);
                TextView zan = (TextView) mMarkerLy
                        .findViewById(R.id.id_info_zan);

                iv.setImageResource(info.getImgId());
                distance.setText(info.getDistance());
                name.setText(info.getName());
                zan.setText(info.getZan() + "");//转换成字符串？？？？

                //生成一个TextView用户在地图中显示InfoWindow
                InfoWindow infoWindow;
                TextView tv = new TextView(context);
                tv.setBackgroundResource(R.drawable.location_tips);
                tv.setPadding(30, 20, 30, 50);
                tv.setText(info.getName());
                tv.setTextColor(Color.parseColor("#ffffff"));
                //将marker所在的经纬度的信息转化成屏幕上的坐标
                final LatLng latLng = marker.getPosition();
                Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
                p.y -= 47;
                LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p);

               /*
                *为弹出的InfoWindow添加点击事件
                */

                //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
                //infoWindow = new InfoWindow(tv, ll,1);

                infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(tv), ll, 0,
                        new InfoWindow.OnInfoWindowClickListener() {

                            @Override
                            public void onInfoWindowClick() {

                                mBaiduMap.hideInfoWindow();
                            }
                        });

                mBaiduMap.showInfoWindow(infoWindow);


                mMarkerLy.setVisibility(View.VISIBLE);
                return true;

            }
        });



        /*为地图添加点击事件
        ＊对marker布局进行消失*/
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerLy.setVisibility(View.GONE);//布局消失
                mBaiduMap.hideInfoWindow();       //InfoWindow消失
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });


    }

    private void initMarker() {
        mMarker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
        mMarkerLy = (RelativeLayout) findViewById(R.id.id_marker_ly);
    }

    private void initLoaction() {

        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;//定位模式默认为普通模式

        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();

        //这册监听
        mLocationClient.registerLocationListener(mLocationListener);

        //对定位进行设置
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true); //需要地址为true才能得到地址字符串
        option.setOpenGps(true);        //是否打开GPS
        option.setScanSpan(1000);

        mLocationClient.setLocOption(option);//很关键的一句话？？？？？

        mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);

        //实例化传感器监听
        myOrientationListener = new MyOrientationListener(context);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });

    }


    private void initView() {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        mBaiduMap = mMapView.getMap();

        //设置放大比例,标尺在500米左右？？？？
        /*
        **取值范围3到19之间，值越大，地图显示的信息越精细
        */
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);

        mBaiduMap.setMapStatus(msu);
    }

    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }


    //在onCreate()之后或者onStop()之后返回上一个活动后执行
    @Override
    protected void onStart() {
        super.onStart();
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //是否启动了,如果没启动，则启动
        if (!mLocationClient.isStarted())
            mLocationClient.start();

        //开启方向传感器＃＃＃＃＃和开启定位同时启动
        myOrientationListener.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //停止定位。。。。当不需要定位图层时关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();

        //停止方向传感器＃＃＃＃＃和停止定位同时
        myOrientationListener.stop();

    }

    //创建菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //这个菜单在右上角，观察不到。。。。。。
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.id_map_common:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.id_map_site:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.id_map_traffic:
                if (mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(false);
                    item.setTitle("实时交通（off）");
                } else {
                    mBaiduMap.setTrafficEnabled(true);
                    item.setTitle("实时交通（on）");
                }
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//可要可不要
                break;
            case R.id.id_map_location:
                centerToMyLocation(mLatitude, mLongtitude);//定位到当前位置
                break;

            case R.id.id_map_mode_common:

                mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
                break;

            case R.id.id_map_mode_following:

                mLocationMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.id_map_mode_compass:

                mLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
                break;
            case R.id.id_add_overlay:
                addOverlays(Info.infos);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 添加覆盖物
     *
     * @param infos 省去了服务器获取值
     */
    private void addOverlays(List<Info> infos) {
        mBaiduMap.clear();      //清除定位的一些图层
        LatLng latLng = null;   //经纬度类
        Marker marker = null;   //覆盖物类
        OverlayOptions options;
        for (Info info : infos) {
            // 经纬度
            latLng = new LatLng(info.getLatitude(), info.getLongtitude());
            // 图标
            options = new MarkerOptions().position(latLng).icon(mMarker)
                    .zIndex(5); //值越大，越在上面
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info);
            marker.setExtraInfo(arg0);
        }

        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);

    }

    /*定位到我的位置 */
    private void centerToMyLocation(double mLatitude, double mLongtitude) {
        LatLng latLng = new LatLng(mLatitude, mLongtitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    //定位监听
    private class MyLocationListener implements BDLocationListener {

        //异步回调函数
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // 构造定位数据
            MyLocationData data = new MyLocationData.Builder()//
                    .direction(mCurrentX)//
                    .accuracy(bdLocation.getRadius())       //
                    .latitude(bdLocation.getLatitude())     //
                    .longitude(bdLocation.getLongitude())   //
                    .build();
            mBaiduMap.setMyLocationData(data);

            //设置自定以图标
            MyLocationConfiguration config = new MyLocationConfiguration(
                    mLocationMode,  /*MyLocationConfiguration.LocationMode.NORMAL,*/
                    true, mIconLocation);


            mBaiduMap.setMyLocationConfigeration(config);

            //每次定位成功更新.用于记录更新经纬度
            mLatitude = bdLocation.getLatitude();     //得到当前定位的经度
            mLongtitude = bdLocation.getLongitude();    //得到当前定位的纬度


            if (isFirstIn) {
                centerToMyLocation(bdLocation.getLatitude(), bdLocation.getLongitude());
                isFirstIn = false;
                //弹出当前的位置
                Toast.makeText(context, bdLocation.getAddrStr(), Toast.LENGTH_LONG).show();
            }


        }
    }

}

package com.systemteam.application;

import android.app.Application;
import android.os.Environment;

import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechUtility;
import com.systemteam.R;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

import static com.iflytek.cloud.SpeechConstant.APPID;

public class BikeApplication extends Application {

    public static String mSDCardPath;
    public static String APP_FOLDER_NAME;

    public void onCreate() {
        super.onCreate();
        //百度地图
        SDKInitializer.initialize(getApplicationContext());
        //科大讯飞初始化
        SpeechUtility.createUtility(this, APPID +"=58f9ff61");

        initDirs();
        initBmob();

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    private boolean initDirs() {
        mSDCardPath = Environment.getExternalStorageDirectory().toString();
        if (mSDCardPath == null) {
            return false;
        }
        APP_FOLDER_NAME = getString(R.string.app_name);
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initBmob(){
        //		//第一：设置BmobConfig，允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)
		BmobConfig config = new BmobConfig.Builder(this)
		//设置appkey
		.setApplicationId("fe66bafa3d7118e88693e01a7ef41b60")
		//请求超时时间（单位为秒）：默认15s
		.setConnectTimeout(30)
		//文件分片上传时每片的大小（单位字节），默认512*1024
		.setUploadBlockSize(1024*1024)
		//文件的过期时间(单位为秒)：默认1800s
		.setFileExpiration(5500)
		.build();
		Bmob.initialize(config);
    }
}

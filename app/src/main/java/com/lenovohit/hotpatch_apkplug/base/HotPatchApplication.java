package com.lenovohit.hotpatch_apkplug.base;

import android.app.Application;
import android.content.Context;

import com.apkplug.packersdk.PackerManager;
import com.lenovohit.hotpatch_apkplug.utils.Constant;

import org.apkplug.app.FrameworkFactory;
import org.osgi.framework.BundleContext;

/**
 * 自定义application
 * Created by yuzhijun on 2017/1/12.
 */

public class HotPatchApplication extends Application {
    //保存bundleContext给其他地方调用
    private  BundleContext bundleContext;
    private static HotPatchApplication hotPatchApplication;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if(PackerManager.getInstance().applicationAttachBaseContext(base,this)){
            return;
        }
    }

    @Override
    public void onCreate() {
        if(PackerManager.getInstance().applicationOnCreate(this, Constant.APKPLUG_PUBLICKEY)){
            return;
        }
        super.onCreate();
        hotPatchApplication = this;
        try {
            bundleContext = FrameworkFactory.getInstance().start(null, this).getSystemBundleContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HotPatchApplication getHotPatchApplication(){
        return hotPatchApplication;
    }

    public BundleContext getBundleContext(){
        return bundleContext;
    }
}

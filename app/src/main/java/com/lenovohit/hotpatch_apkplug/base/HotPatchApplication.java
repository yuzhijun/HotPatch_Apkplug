package com.lenovohit.hotpatch_apkplug.base;

import android.app.Application;
import android.content.Context;

import com.apkplug.packersdk.PackerManager;

/**
 * 自定义application
 * Created by yuzhijun on 2017/1/12.
 */

public class HotPatchApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if(PackerManager.getInstance().applicationAttachBaseContext(base,this)){
            return;
        }
    }

    @Override
    public void onCreate() {
        if(PackerManager.getInstance().applicationOnCreate(this,"MIGdMA0GCSqGSIb3DQEBAQUAA4GLADCBhwKBgQDJAlbro3lIHxkUMpWrQUjveQYoUdSdT1KdpQl4qjqgf3U2tSHZY6SjXSHF/30N/h4LRBqKYff86AYFnCh2oYTUAZmXURw9K7KhqW5EzoSZa4nEkBB9wKjezBeY1I7FSGeynWxxKFoXoprjDDeXM/6pBpccE8kgFqfI5T9HFqKXYwIBAw==")){
            return;
        }
        super.onCreate();
    }
}

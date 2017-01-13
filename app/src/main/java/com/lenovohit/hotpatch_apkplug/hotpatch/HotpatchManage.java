package com.lenovohit.hotpatch_apkplug.hotpatch;

import android.util.Log;

import com.apkplug.packersdk.PackerManager;
import com.apkplug.packersdk.data.DownloadInfo;
import com.apkplug.packersdk.net.listeners.OnCheckVersionInfoListener;
import com.apkplug.packersdk.net.listeners.OnUpdataListener;

/**
 * 热更新管理
 * cautions:
 * 1.热更新文件是根据MD5值来标识的，本地安装的apk必须与服务器保持一致，debug版本和release版本也必须一致
 * Created by yuzhijun on 2017/1/12.
 */
public class HotpatchManage {
    private static final String HOTPATH_MANAGE = "Hotpatch";
    /**
     "package_name":"alipay",        #应用唯一识别码
     "version":100,                  #应用新版本
     "uri":"http://xxx.apk",         #文件下载地址
     "md5":"",                       #文件的MD5,可用于文件校验
     "info":"",                      #版本说明
     "execute":"update|force-update" #更新的执行策略，update进行热更新不提示任何信息，force-update，强制热更新，会提示用户
     "diff":详见Diff结构:[
     identification:"合并后新文件的MD5"
     base_identification:"参与合并的旧版本文件MD5"
     uri:"差分文件下载地址"
     md5:"差分文件MD5"
     size:"long类型，差分文件大小"]
     * */
    public static void hotpatchUpadate(){
        PackerManager.getInstance().checkVersionInfo(new OnCheckVersionInfoListener() {
            @Override
            public void onSuccess(DownloadInfo downloadInfo, String s) {
                Log.e(HOTPATH_MANAGE,"new MD5:"+downloadInfo.getDiff().getIdentification());
                Log.e(HOTPATH_MANAGE,"old MD5:"+downloadInfo.getDiff().getIdentification());
                PackerManager.getInstance().updata(new OnUpdataListener() {
                    @Override
                    public void onSuccess(String filePath, DownloadInfo downloadinfo) {
                        //更新成功
                    }

                    @Override
                    public void onFailure(int state, String responseMsg) {
                        Log.e(HOTPATH_MANAGE,responseMsg);
                    }

                    @Override
                    public void onProgress(String url, String filePath, long bytesWritten, long totalBytes) {
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e(HOTPATH_MANAGE,s);
            }
            @Override
            public void onAlreadyLastVersion(String s) {
                Log.e(HOTPATH_MANAGE,s);
            }
        });
    }
}

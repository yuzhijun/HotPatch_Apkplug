package com.lenovohit.pluginproject.rpcimpl;

import android.util.Log;

import com.lenovohit.pluginproject.rpcinterface.IShareBean;
import com.lenovohit.pluginproject.rpcinterface.ShareHandler;

/**
 * Created by yuzhijun on 2017/1/16.
 */

public class ShareProcessor implements ShareHandler {
    private static final String TAG = ShareProcessor.class.getSimpleName();
    @Override
    public String shareBean(IShareBean shareBean) {
        Log.e(TAG,shareBean.getName());
        return shareBean.getName();
    }
}

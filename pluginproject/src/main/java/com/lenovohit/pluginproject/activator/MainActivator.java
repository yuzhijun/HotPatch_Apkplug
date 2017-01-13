package com.lenovohit.pluginproject.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 插件入口类
 * Created by yuzhijun on 2017/1/13.
 */

public class MainActivator implements BundleActivator {
    public static BundleContext bundleContext;
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {

    }
}

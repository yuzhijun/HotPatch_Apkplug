package com.lenovohit.hotpatch_apkplug;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apkplug.trust.PlugManager;
import com.apkplug.trust.common.listeners.OnInstallListener;
import com.apkplug.trust.common.listeners.OnUpdataListener;
import com.apkplug.trust.data.PlugDownloadState;
import com.apkplug.trust.data.PlugInfo;
import com.apkplug.trust.net.listeners.OnGetPlugInfoListener;
import com.apkplug.trust.net.requests.GetPlugInfoRequest;
import com.lenovohit.hotpatch_apkplug.base.HotPatchApplication;
import com.lenovohit.hotpatch_apkplug.hotpatch.HotpatchManage;
import com.lenovohit.hotpatch_apkplug.ui.adapter.MyRecyclerAdapter;
import com.lenovohit.hotpatch_apkplug.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;
    private FloatingActionButton fab;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter mMyRecyclerAdapter;
    List<PlugInfo> plugInfoList;
    List<org.osgi.framework.Bundle> bundleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        plugInfoList = new ArrayList<>();
        bundleList = new ArrayList<>();
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.content);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.view2);
        setSupportActionBar(mToolbar);
        mSwipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.YELLOW, Color.RED);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mMyRecyclerAdapter = new MyRecyclerAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMyRecyclerAdapter);

        mMyRecyclerAdapter.setRecyclerItemClickListener(new MyRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onClick(View view, int person) {
                String btnName = ((Button)view).getText().toString();
                if(btnName.equals("下载")){
                    try {
                        installplug(plugInfoList.get(person));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(btnName.equals("运行")){
                    startPlug(person);
                }
            }
        });

        //检测有没有新的热更新包
        HotpatchManage.hotpatchUpadate();
        //插件托管前需要初始化
        PlugManager.getInstance().init(this, HotPatchApplication.getHotPatchApplication().getBundleContext(), Constant.APKPLUG_PUBLICKEY,true);
        //6.0申请权限
        PlugManager.getInstance().requestPermission(this);
        //获取后台的插件
        getPlugInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.update) {
            updataLocalPlug();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PlugManager.getInstance().onRequestPermissionsResult(requestCode, this,permissions,grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlugManager.getInstance().onDestroy();
    }

    /**
     * 获取插件
     * */
    private void getPlugInfo(){
        mSwipeRefreshLayout.setRefreshing(true);
        GetPlugInfoRequest request = new GetPlugInfoRequest();
        request.setNeed_diff(true);
        PlugManager.getInstance().getPlugInfo(request, new OnGetPlugInfoListener() {
            @Override
            public void onSuccess(List<PlugInfo> list) {
                if (list.size() > 0) {
                    plugInfoList = list;
                    for (PlugInfo info : list) {
                        mMyRecyclerAdapter.addData(info.getPlug_name(), 0);
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(String s) {
                mTextView.setText(s);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 下载插件
     * */
    private void installplug(PlugInfo info) {
        PlugManager.getInstance().installPlug(MainActivity.this, info, new OnInstallListener() {
            @Override
            public void onDownloadProgress(String s, String s1, long l, long l1, PlugInfo plugInfo) {
                try {
                    PlugDownloadState state = PlugManager.getInstance().queryDownloadState(s);
                    String plugid = state.getPlug_name();
                    float percent = (float) l / (float) l1;
                    mMyRecyclerAdapter.setData(mMyRecyclerAdapter.getOldValue(plugid), plugid + String.format("\n 下载：%d / %d", l, l1), percent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onInstallSuccess(org.osgi.framework.Bundle bundle, PlugInfo plugInfo) {
                if (bundle != null) {
                    Log.e("install", " bundle" + bundle.getName());
                }
                bundleList.add(bundle);
                mMyRecyclerAdapter.setBtnName(plugInfoList.indexOf(plugInfo),"运行");
            }

            @Override
            public void onInstallFailuer(int i, PlugInfo plugInfo, String errorMsg) {
                Log.e("install failuer", errorMsg);
                Toast.makeText(MainActivity.this,errorMsg,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDownloadFailure(String s) {
                Log.e("install f","download failure");
                Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 启动插件
     * */
    private void startPlug(int person){
        for(org.osgi.framework.Bundle bundle : bundleList){
            if(bundle.getName().equals(plugInfoList.get(person).getPlug_name())){
                try {
                    if(bundle.getState()!=bundle.ACTIVE){
                        //判断插件是否已启动
                        try {
                            bundle.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(bundle.getBundleActivity()!=null){
                        Intent i=new Intent();
                        i.setClassName(MainActivity.this, bundle.getBundleActivity().split(",")[0]);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MainActivity.this.startActivity(i);
                    }else{
                        Toast.makeText(MainActivity.this, "该插件没有配置BundleActivity",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检测更新
     * */
    private void updataLocalPlug(){
        try {
            org.osgi.framework.Bundle[] bundles = HotPatchApplication.getHotPatchApplication().getBundleContext().getBundles();
            for (org.osgi.framework.Bundle bundle : bundles) {
                PlugManager.getInstance().updatePlug(this, bundle, new OnUpdataListener() {
                    @Override
                    public void onDownloadProgress(String url, String filePath, long bytesWritten, long totalBytes, PlugInfo plugInfo) {
                        try {
                            PlugDownloadState state = PlugManager.getInstance().queryDownloadState(url);
                            String plugid = state.getPulgId();
                            float percent = (float) bytesWritten / (float) totalBytes;
                            mMyRecyclerAdapter.setData(mMyRecyclerAdapter.getOldValue(plugid), plugid + String.format("\n 下载：%d / %d", bytesWritten, totalBytes), percent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onUpdataSuccess(org.osgi.framework.Bundle bundle, PlugInfo plugInfo) {
                        if (bundle != null) {
                            Log.e("", bundle.getName());
                            mTextView.setText(bundle.getName());
                        }
                    }

                    @Override
                    public void onUpdataFailuer(int i, PlugInfo plugInfo, String errorMsg) {
                        mTextView.setText(errorMsg);
                    }

                    @Override
                    public void onDownloadFailure(String errorMsg) {
                        Log.e("", errorMsg);
                        mTextView.setText(errorMsg);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

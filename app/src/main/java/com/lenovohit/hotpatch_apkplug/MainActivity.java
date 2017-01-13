package com.lenovohit.hotpatch_apkplug;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.apkplug.trust.PlugManager;
import com.apkplug.trust.data.PlugInfo;
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

        //检测有没有新的热更新包
        HotpatchManage.hotpatchUpadate();
        //插件托管前需要初始化
        PlugManager.getInstance().init(this, HotPatchApplication.getHotPatchApplication().getBundleContext(), Constant.APKPLUG_PUBLICKEY,true);
        //6.0申请权限
        PlugManager.getInstance().requestPermission(this);


    }

    private void getPlugInfo(){

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
}

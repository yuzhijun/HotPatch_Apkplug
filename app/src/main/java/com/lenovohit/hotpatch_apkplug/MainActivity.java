package com.lenovohit.hotpatch_apkplug;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lenovohit.hotpatch_apkplug.hotpatch.HotpatchManage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HotpatchManage.hotpatchUpadate();
    }
}

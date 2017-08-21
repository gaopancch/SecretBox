package com.gaopan.serectbox.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import com.gaopan.serectbox.Listener.BaseUiListener;
import com.gaopan.serectbox.R;
import com.gaopan.serectbox.db.DataBaseHelper;
import com.gaopan.serectbox.utils.ConstantUtils;
import com.tencent.connect.auth.QQAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

public class BaseActivity extends AppCompatActivity {
    /*数据库操作*/
    protected DataBaseHelper dataBaseHelper;

    protected IWXAPI api;

    protected Tencent mTencent;

    protected QQAuth qqAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, ConstantUtils.WECHAT_APPID, true);
        api.registerApp(ConstantUtils.WECHAT_APPID);

        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
// 其中APP_ID是分配给第三方应用的appid，类型为String。
        mTencent = Tencent.createInstance(ConstantUtils.QQ_APPID, this.getApplicationContext());
        ConstantUtils.CATEGORY_TABLE_NAME=
                "category_table"+"_"+ConstantUtils.USER_NAME;
        dataBaseHelper=new DataBaseHelper(this, ConstantUtils.SECRETBOX_DATABASE_NAME,1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dataBaseHelper!=null){
            dataBaseHelper.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
        if (null != mTencent) {
            mTencent.onActivityResult(requestCode, resultCode, data);
        }
    }
}

package com.gaopan.serectbox;

import android.app.Application;

import com.pgyersdk.crash.PgyCrashManager;

/**
 * Created by gaopan on 2017/6/2.
 */

public class PgyApplication extends Application {
    @Override
    public void onCreate() {
// TODO Auto-generated method stub
        super.onCreate();
        PgyCrashManager.register(this);   //就是把这句加到你的application之中，如果没有application就新建一个
    }
}
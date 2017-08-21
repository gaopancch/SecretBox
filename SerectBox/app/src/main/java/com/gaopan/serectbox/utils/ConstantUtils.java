package com.gaopan.serectbox.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by gaopan on 2017/5/26.
 */

public class ConstantUtils {
    public static boolean isDebug=false;
    public static String SECRETBOX_DATABASE_NAME ="secretbox.db";
    public static String CATEGORY_TABLE_NAME="category_table";
    public static String CATEGORY="category";
    public static String ITEM_TITLE="title";
    public static String ITEM_MESSAGE="message";
    public static String SAMPLE="Soundbite";
    public static String USER_NAME="gaopancch";
    public static String PASSWORD="5886656cch";

    public static String GETUI_APPID="h7TLNt8unp6IP4ae4t8gw1";
    public static String GETUI_APIKYE="UR3J6EEuGx8h0JT4HWXhYA";
    public static String GETUI_SECRETKYE="3NWism0p8O6aKuUMTf2wD8";

    //weixin  242978649c31f48711143325e8cb285d
    public static String WECHAT_APPID="wx9997793cd0f4ab62";

    //1106151601
    public static String QQ_APPID="1106151601";

    public static String getAPPVersionCode(Context ctx) {
        int currentVersionCode = 0;
        String appVersionName="";
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
             appVersionName = info.versionName; // 版本名
            currentVersionCode = info.versionCode; // 版本号
            System.out.println(currentVersionCode + " " + appVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersionName;
    }
}

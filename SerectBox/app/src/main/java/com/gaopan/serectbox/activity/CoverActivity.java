package com.gaopan.serectbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;

import com.gaopan.serectbox.R;

import java.util.Random;
import com.igexin.sdk.PushManager;
public class CoverActivity extends BaseActivity {
    private Handler timeHandler;
    private Runnable taskRunnable;
    private Random random;
    private int randonInt;
    private RelativeLayout relativeLayout;
    private int[] drawables;
    private int SHOW_TIME=4000;//ms
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover2);
        relativeLayout=(RelativeLayout)findViewById(R.id.activity_cover2);
//        relativeLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);//动态 隐藏状态栏（闪烁）
        initCover();
        initChangeHandler();
        // com.getui.demo.DemoPushService 为第三方自定义推送服务
        PushManager.getInstance().initialize(this.getApplicationContext(),
                com.gaopan.serectbox.service.GeTuiPushService.class);
        // com.getui.demo.DemoIntentService 为第三方自定义的推送服务事件接收类
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(),
                com.gaopan.serectbox.service.GeTuiIntentService.class);
    }

    private void initCover(){
        drawables= new int[]{
                R.drawable.angel,
                R.drawable.anglely,
                R.drawable.bobo,
                R.drawable.girl,
                R.drawable.horse,
                R.drawable.watch};
        random=new Random();
        randonInt=random.nextInt(6);
        relativeLayout.setBackgroundResource(drawables[randonInt]);
    }

    private void initChangeHandler(){
        timeHandler=new Handler();
        taskRunnable=new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent();
                intent.setClass(CoverActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        timeHandler.postDelayed(taskRunnable,SHOW_TIME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacksAndMessages(null);
    }
}

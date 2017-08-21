package com.gaopan.serectbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.GetChars;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopan.serectbox.R;
import com.gaopan.serectbox.utils.ConstantUtils;
import com.gaopan.serectbox.utils.PreferenceUtil;
import com.gaopan.serectbox.view.GestureLockViewGroup;

import java.util.ArrayList;
import java.util.List;

public class GestureSetActivity extends BaseActivity {
    private GestureLockViewGroup mGestureLockViewGroup;
    private List<Integer> anserList=new ArrayList<Integer>();
    private int[] answer=null;
    private String answerString="";
    private boolean hasSetGestured =false;
    private TextView alertText=null;
    private TextView userName=null;
    private Button forgetButton=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_set);
        alertText=(TextView) findViewById(R.id.alert_text_gesture);
        userName=(TextView) findViewById(R.id.user_name_gesture);
        forgetButton=(Button)findViewById(R.id.forget_gesture);
        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtil.putBoolean("hasSetGestured",false,getApplicationContext());
                PreferenceUtil.putString("gesturedAnswer","",getApplicationContext());
                goToActivity(LoginActivity.class);
            }
        });
        hasSetGestured =PreferenceUtil.getBoolean("hasSetGestured",false,getApplicationContext());
        mGestureLockViewGroup = (GestureLockViewGroup) findViewById(R.id.id_gestureLockViewGroup);
        if(!hasSetGestured) {
            alertText.setText(getString(R.string.set_gesture));
            mGestureLockViewGroup.setIsSetAnswer(true);
        }else{
            hasSetGestured();
        }
        mGestureLockViewGroup.setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener(){

                    @Override
                    public void onUnmatchedExceedBoundary(){
                        Toast.makeText(GestureSetActivity.this, "You make too much errors !",
                                Toast.LENGTH_SHORT).show();
                        if(hasSetGestured){
                            PreferenceUtil.putBoolean("gesture_try_false",true,getApplicationContext());
                            goToActivity(LoginActivity.class);
                        }else {
                            mGestureLockViewGroup.setUnMatchExceedBoundary(5);
                            goToActivity(MenuActivity.class);
                        }
                    }

                    @Override
                    public void onGestureEvent(boolean matched){
                        if(!hasSetGestured) {
                            //没有设置过图形密码，第一次设置
                            hasNotSetGesturedonGestureEvent(matched);
                        }else{
                            if (matched) {
                                goToActivity(MenuActivity.class);
                            } else {
                                if(forgetButton.getVisibility()==View.GONE) {
                                    startForgetButtonAnnimation();
                                }
                                alertText.setText(getString(R.string.input_gesture_error));
                            }
                        }
                    }

                    @Override
                    public void onBlockSelected(int cId){

                    }
                });
    }

    private void startForgetButtonAnnimation(){
        forgetButton.setVisibility(View.VISIBLE);
        //创建渐变动画
        Animation animation = new TranslateAnimation(0,0, 0,-20);
        animation.setDuration(100);
        animation.setRepeatCount(5);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setFillAfter(true);//设置为true，动画转化结束后被应用
        forgetButton.startAnimation(animation);//开始动画
    }

    private void goToActivity(Class<?> cls){
        Intent intent=new Intent(GestureSetActivity.this,cls);
        startActivity(intent);
        finish();
    }

    private void hasSetGestured(){
        userName.setVisibility(View.VISIBLE);
        userName.setText(ConstantUtils.USER_NAME);
        alertText.setText(getString(R.string.intput_gesture_answer));
        answerString=PreferenceUtil.getString("gesturedAnswer","00",getApplicationContext());
        answer=new int[answerString.length()];
        for(int i=0;i<answerString.length();i++){
            answer[i]=Integer.parseInt(String.valueOf(answerString.charAt(i)));
        }
        mGestureLockViewGroup.setAnswer(answer);
    }

    private void hasNotSetGesturedonGestureEvent(boolean matched){
        if (mGestureLockViewGroup.isSetAnswer()) {
            //没有设置过图形密码，设置的第一遍
            anserList = mGestureLockViewGroup.mChoose;
            int size = anserList.size();
            answer = new int[anserList.size()];
            for (int i = 0; i < size; i++) {
                answer[i] = anserList.get(i);
            }
            mGestureLockViewGroup.setAnswer(answer);
            mGestureLockViewGroup.setIsSetAnswer(false);
            mGestureLockViewGroup.reset();
            alertText.setText(getString(R.string.set_gesture_again));
        } else {
            //设置的第2遍
            if (matched) {
                hasSetGestured=true;
                PreferenceUtil.putBoolean("hasSetGestured",hasSetGestured,getApplicationContext());
                for(int i=0;i<answer.length;i++) {
                    answerString += answer[i] + "";
                }
                PreferenceUtil.putString("gesturedAnswer",answerString,getApplicationContext());
                goToActivity(MenuActivity.class);
            } else {
                alertText.setText(getString(R.string.set_gesture_error));
            }
        }
    }
}

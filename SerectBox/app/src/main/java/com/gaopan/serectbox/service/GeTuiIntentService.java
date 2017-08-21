package com.gaopan.serectbox.service;

import android.content.Context;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;

/**
 * Created by gaopan on 2017/6/7.
 */
/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */

public class GeTuiIntentService extends GTIntentService{
    public GeTuiIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.i(TAG, "pid = " + pid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        Log.i(TAG,  "msg = " + msg.toString());
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.i(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.i(TAG, "online = " + online);
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.i(TAG, "cmdMessage = " + cmdMessage.toString());
    }
}

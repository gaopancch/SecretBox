package com.gaopan.serectbox.Listener;

import android.util.Log;

import com.tencent.open.utils.HttpUtils;
import com.tencent.tauth.IRequestListener;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

/**
 * Created by gaopan on 2017/6/15.
 */

public class BaseApiListener implements IRequestListener {
    @Override
    public void onComplete(JSONObject jsonObject) {
        Log.i("tencentListener","BaseApiListener onComplete");
    }

    @Override
    public void onIOException(IOException e) {
        Log.i("tencentListener","BaseApiListener onIOException");
    }

    @Override
    public void onMalformedURLException(MalformedURLException e) {
        Log.i("tencentListener","BaseApiListener onMalformedURLException");
    }

    @Override
    public void onJSONException(JSONException e) {
        Log.i("tencentListener","BaseApiListener onJSONException");
    }

    @Override
    public void onConnectTimeoutException(ConnectTimeoutException e) {
        Log.i("tencentListener","BaseApiListener onConnectTimeoutException");
    }

    @Override
    public void onSocketTimeoutException(SocketTimeoutException e) {
        Log.i("tencentListener","BaseApiListener onSocketTimeoutException");
    }

    @Override
    public void onNetworkUnavailableException(HttpUtils.NetworkUnavailableException e) {
        Log.i("tencentListener","BaseApiListener onNetworkUnavailableException");
    }

    @Override
    public void onHttpStatusException(HttpUtils.HttpStatusException e) {
        Log.i("tencentListener","BaseApiListener onHttpStatusException");
    }

    @Override
    public void onUnknowException(Exception e) {
        Log.i("tencentListener","BaseApiListener onUnknowException");
    }
}

package com.gaopan.serectbox.Listener;

import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

/**
 * Created by gaopan on 2017/6/15.
 */

public class BaseUiListener implements IUiListener {

    @Override
    public void onComplete(Object response) {
        Log.i("tencentListener","BaseUiListener onComplete  response="+response);
    }

    @Override
    public void onError(UiError uiError) {
        Log.i("tencentListener","BaseUiListener onError");
    }

    @Override
    public void onCancel() {
        Log.i("tencentListener","BaseUiListener onCancel");
    }
}

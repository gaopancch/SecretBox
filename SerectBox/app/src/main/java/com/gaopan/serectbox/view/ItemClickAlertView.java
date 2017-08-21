package com.gaopan.serectbox.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.gaopan.serectbox.R;

/**
 * Created by gaopan on 2017/5/27.
 */

public class ItemClickAlertView{
    private View contentView;
    private Context context;
    private LayoutInflater inflater;
    private Button copyButton,deleteButton;
    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener l){
        onClickListener=l;
    }

    public ItemClickAlertView(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
        init();
    }

    private void init(){
        contentView=inflater.inflate(R.layout.item_click_alert_view,null);
        copyButton=(Button) contentView.findViewById(R.id.copy);
        deleteButton=(Button) contentView.findViewById(R.id.delete);
        deleteButton.setOnClickListener(onClickListener);
        copyButton.setOnClickListener(onClickListener);
    }

    public View getView(){
        return contentView;
    }

}

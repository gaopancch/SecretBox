package com.gaopan.serectbox.Adapter;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.gaopan.serectbox.R;
import com.gaopan.serectbox.db.DataBaseHelper;
import com.gaopan.serectbox.utils.DataBaseUtils;
import com.gaopan.serectbox.utils.ToastUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaopan on 2017/5/25.
 */

public class ItemAdapter extends BaseAdapter {
    private List<String> itemDataList = null;
    private Map<String, String> dataMap = new HashMap<>();
    private LayoutInflater inflater;
    private Context context;
    private ClipboardManager clipboardManager;
    private DataBaseHelper dataBaseHelper;
    private String tableName;
    private AlertDialog deleteAlertDialog = null;
    private AlertDialog editAlertDialog = null;
    private IWXAPI api;

    public ItemAdapter(Context context, DataBaseHelper dataBaseHelper, String tableName, List<String> itemDataList, Map<String, String> dataMap,IWXAPI api) {
        this.itemDataList = itemDataList;
        this.dataBaseHelper = dataBaseHelper;
        this.tableName = tableName;
        this.dataMap = dataMap;
        this.context = context;
        this.api=api;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itemDataList == null ? 0 : itemDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ItemAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.menu_item_layout, null);
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.item_titl);
            viewHolder.msgTextView = (TextView) convertView.findViewById(R.id.item_msg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ItemAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.titleTextView.setText(itemDataList.get(position));
        viewHolder.msgTextView.setText(dataMap.get(itemDataList.get(position)));
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                showLongPressDialog(v,position);
                return false;
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView titleTextView;
        TextView msgTextView;
    }

    private void deleteItem(final int position) {
        deleteAlertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.alert))
                .setMessage(context.getString(R.string.delete_alert))
                .setPositiveButton(context.getString(R.string.delete_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataBaseUtils.deleteItemData(dataBaseHelper.getReadableDatabase(), tableName, itemDataList.get(position));
                        dataMap.remove(itemDataList.get(position));
                        itemDataList.remove(position);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void copyItem(final int position) {
        if (clipboardManager == null) {
            //获取剪贴板管理器：
            clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", itemDataList.get(position) + ":" + dataMap.get(itemDataList.get(position)));
        // 将ClipData内容放到系统剪贴板里。
        clipboardManager.setPrimaryClip(mClipData);
        ToastUtils.showMessage(context, dataMap.get(itemDataList.get(position)) +
                " " + context.getString(R.string.copy_sucess));
    }

    private void copyAll() {
        if (clipboardManager == null) {
            //获取剪贴板管理器：
            clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        String allMsg="";
        for(int i=0;i<itemDataList.size();i++){
            allMsg+=itemDataList.get(i)+":";
            allMsg+=dataMap.get(itemDataList.get(i))+" \n";
        }
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", allMsg);
        // 将ClipData内容放到系统剪贴板里。
        clipboardManager.setPrimaryClip(mClipData);
        ToastUtils.showMessage(context, allMsg +
                " " + context.getString(R.string.copy_sucess));
    }

    private void editItem(final View view,final int position) {
        view.setBackgroundColor(Color.CYAN);
        final EditText editText = new EditText(context);
        final String key = itemDataList.get(position);
        editText.setText(dataMap.get(key));
        editAlertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.edit_input_titile))
                .setView(editText)
                .setPositiveButton(context.getString(R.string.edit_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        DataBaseUtils.updateItemData(dataBaseHelper.getReadableDatabase(), tableName, key, text);
                        view.setBackgroundColor(Color.TRANSPARENT);
                        dataMap.put(key, text);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.setBackgroundColor(Color.TRANSPARENT);
                        dialog.dismiss();
                    }
                }).setCancelable(false)
                .show();
    }

    private void showLongPressDialog(final View v,final int position){
        final CharSequence[] items = {
                "     Copy",
                "     Copy All",
                "     Edit",
                "     Delete",
                "     Share to WeChat comments",
                "     Share to WeChat friend"};
        v.setBackgroundColor(Color.CYAN);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.choos_opera));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                v.setBackgroundColor(Color.TRANSPARENT);
                if (item == 0) {
                    copyItem(position);
                }else if(item==1){
                    copyAll();
                } else if (item == 2) {
                    editItem(v,position);
                } else if (item == 3) {
                    deleteItem(position);
                }else if(item==4){
                   String shareText= itemDataList.get(position) + ":" + dataMap.get(itemDataList.get(position));
                    shareSerectBoxToWechat(shareText,1);
                }else if(item==5){
                String shareText= itemDataList.get(position) + ":" + dataMap.get(itemDataList.get(position));
                shareSerectBoxToWechat(shareText,0);
            }
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                v.setBackgroundColor(Color.TRANSPARENT);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void shareSerectBoxToWechat(String text,int flag){
        if (!api.isWXAppInstalled()) {
            ToastUtils.showMessage(context, "您还未安装微信");
            return;
        }
        // 0-分享给朋友  1-分享到朋友圈
        //share to wechat
        WXTextObject wxTextObject = new WXTextObject();
        wxTextObject.text = text;
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.mediaObject = wxTextObject;
        wxMediaMessage.description = wxTextObject.text;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        ////transaction字段用于唯一标识一个请求，这个必须有，否则会出错
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = wxMediaMessage;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }
}


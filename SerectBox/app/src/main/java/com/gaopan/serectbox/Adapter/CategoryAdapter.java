package com.gaopan.serectbox.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.gaopan.serectbox.R;
import com.gaopan.serectbox.db.DataBaseHelper;
import com.gaopan.serectbox.utils.ConstantUtils;
import com.gaopan.serectbox.utils.DataBaseUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaopan on 2017/5/25.
 */

public class CategoryAdapter extends BaseAdapter {
    private List<String> dataList=new ArrayList<String>();
    private LayoutInflater inflater;
    private Context context;
    private DataBaseHelper dataBaseHelper;
    private boolean isShowDeleteButton=false;

    public CategoryAdapter(Context context, List<String> list,DataBaseHelper dataBaseHelper){
        dataList=list;
        this.context=context;
        this.dataBaseHelper=dataBaseHelper;
        inflater=LayoutInflater.from(context);
    }

    public void setShowDeleteButton(boolean isShow){
        isShowDeleteButton=isShow;
    }

    @Override
    public int getCount() {
        return dataList==null?0:dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view=null;
        ViewHolder viewHolder = null;
        if(viewHolder==null) {
            viewHolder=new ViewHolder();
            view=inflater.inflate(R.layout.menu_category_layout,null);
            viewHolder.textView=(TextView) view.findViewById(R.id.category_titl);
            viewHolder.button=(Button) view.findViewById(R.id.delete_category_button);
            view.setTag(viewHolder);
        }else{
            view =convertView;
            viewHolder=(ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(dataList.get(position));
        viewHolder.button.setOnClickListener(new DeleteButtonOnClickListener(dataList.get(position)));
        if(!isShowDeleteButton){
            viewHolder.button.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.button.setVisibility(View.VISIBLE);
        }
        return view;
    }

    class ViewHolder {
        TextView textView;
        Button  button;
    }

    class DeleteButtonOnClickListener implements OnClickListener{
        private AlertDialog alertDialog=null;
        private String content;
        public DeleteButtonOnClickListener(String content){
            this.content=content;
        }

        @Override
        public void onClick(View v) {
            if(alertDialog==null) {
                alertDialog = new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.alert))
                        .setMessage(context.getString(R.string.delete_alert))
                        .setPositiveButton(context.getString(R.string.delete_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataBaseUtils.deleteTable(dataBaseHelper.getWritableDatabase(),content);//
                                dataList.remove(content);
                                DataBaseUtils.deleteCategoryData(dataBaseHelper.getWritableDatabase(),
                                        ConstantUtils.CATEGORY_TABLE_NAME,content);
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
            }else{
                alertDialog.show();
            }
        }
    }
}

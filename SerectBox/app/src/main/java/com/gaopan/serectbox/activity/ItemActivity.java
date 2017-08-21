package com.gaopan.serectbox.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.gaopan.serectbox.Adapter.ItemAdapter;
import com.gaopan.serectbox.R;
import com.gaopan.serectbox.utils.ConstantUtils;
import com.gaopan.serectbox.utils.DataBaseUtils;
import com.gaopan.serectbox.utils.FileUtils;
import com.gaopan.serectbox.utils.SoundbiteConstants;
import com.gaopan.serectbox.utils.ToastUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemActivity extends BaseActivity {
    private Button addItemButton = null;
    private Button importFromFile=null;
    private ListView itemListView = null;
    private EditText addItemTitleText = null;
    private EditText addItemMsgText = null;
    private ItemAdapter itemAdapter =null;
    private List<String> itemDataList=null;
    private Map<String,String> dataMap=new HashMap<>();
    private InputMethodManager inputMethodManager;
    private String tableName="";
    private boolean isShowImportFromFile=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        tableName=getIntent().getStringExtra(ConstantUtils.CATEGORY);
        initViews();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        itemListView.setAdapter(itemAdapter);


    }

    private void initViews(){
        addItemButton = (Button) findViewById(R.id.add_item_button);
        importFromFile=(Button)findViewById(R.id.import_from_file);
        addItemTitleText = (EditText) findViewById(R.id.add_item_title_editText);
        addItemMsgText = (EditText) findViewById(R.id.add_item_msg_editText);
        itemListView = (ListView) findViewById(R.id.item_list);
        itemDataList=new ArrayList<>();
        getAllItems();
        itemAdapter =new ItemAdapter(this,dataBaseHelper,tableName,itemDataList,dataMap,api);
        setButtonListener();
    }

    private void getFileName(File[] files) {
        if (files != null) {// 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if (file.isDirectory()) {
                    getFileName(file.listFiles());
                } else {
                    String fileName = file.getName();
                    if (fileName.endsWith(".secretbox")) {
                        ToastUtils.showMessage(this,"find file fileName="+fileName);
                        Log.i("FileUtils","secretbox="+ FileUtils.convertCodeAndGetDataMap(file,itemDataList,dataMap,dataBaseHelper,tableName));
                        itemAdapter.notifyDataSetChanged();
                    }else{
                        Log.i("fileName","fileName="+fileName);
                    }
                }
            }
        }
    }

    private void setButtonListener(){
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addItemTitleText.getVisibility()==View.GONE){
                    setHideWidetState(View.VISIBLE);
                    return;
                }
                String title= addItemTitleText.getText().toString();
                String msg= addItemMsgText.getText().toString();
                if(TextUtils.isEmpty(title)||TextUtils.isEmpty(msg)){
                    ToastUtils.showMessage(getApplicationContext(),getString(R.string.input_null));
                }else{
                    if(title.equals("gaopan")&&msg.equals("gaopan")) {
                        //自留地，为了导入本地文件，只有我自己用
                        isShowImportFromFile = true;
                    }
                    int i=1;
                    String temTitle="";
                    temTitle= title;
                    while(itemDataList.contains(temTitle)) {
                        temTitle=title+"_"+i;//当title值在数据中已经存在，则新的记录key后面加上"_1"
                        i++;
                    }
                    itemDataList.add(temTitle);
                    dataMap.put(temTitle,msg);
                    DataBaseUtils.insertDataToItemTable(dataBaseHelper.getWritableDatabase(),tableName,temTitle,msg);
                    itemAdapter.notifyDataSetChanged();
                    ToastUtils.showMessage(getApplicationContext(),getString(R.string.add_success));
                    setHideWidetState(View.GONE);
                    //如果输入法在窗口上已经显示，则隐藏，反之则显示
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
            }
        });

        importFromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getPermission();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/secret";
                    File file =new File(path);
                    // File path = new File("/mnt/sdcard/");
                    File[] files = file.listFiles();// 读取
                    getFileName(files);
                }
            }
        });
    }

    private void setHideWidetState(int state){
        addItemTitleText.setVisibility(state);
        addItemMsgText.setVisibility(state);
        if(ConstantUtils.isDebug||isShowImportFromFile) {
            importFromFile.setVisibility(state);
        }
    }

    private void getAllItems() {
        String sql = "select * from "+tableName;
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        String title="";
        String message="";
        while (cursor.moveToNext()) {
            title=cursor.getString(cursor
                    .getColumnIndex(ConstantUtils.ITEM_TITLE));
            message=cursor.getString(cursor
                    .getColumnIndex(ConstantUtils.ITEM_MESSAGE));
            dataMap.put(title,message);
            itemDataList.add(title);
        }
        if(itemDataList.size()==0&&tableName.equals(ConstantUtils.SAMPLE)){
            String temTitle="";
            for(int i=0;i< SoundbiteConstants.soundbites.length;i++){
                temTitle=""+(i+1);
                itemDataList.add(temTitle);
                dataMap.put(temTitle,SoundbiteConstants.soundbites[i]);
                DataBaseUtils.insertDataToItemTable(dataBaseHelper.getWritableDatabase(),tableName,temTitle,SoundbiteConstants.soundbites[i]);
            }
        }
    }

}

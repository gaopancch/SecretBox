package com.gaopan.serectbox.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gaopan.serectbox.Adapter.CategoryAdapter;
import com.gaopan.serectbox.Listener.BaseUiListener;
import com.gaopan.serectbox.R;
import com.gaopan.serectbox.utils.ConstantUtils;
import com.gaopan.serectbox.utils.DataBaseUtils;
import com.gaopan.serectbox.utils.PreferenceUtil;
import com.gaopan.serectbox.utils.StringUtils;
import com.gaopan.serectbox.utils.ToastUtils;
import com.pgyersdk.feedback.PgyFeedback;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.pgyersdk.update.PgyUpdateManager;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MenuActivity extends BaseActivity {
    private Button addCategoryButton = null;
    private ListView categoryList = null;
    private EditText addCategoryText = null;
    private CategoryAdapter categoryAdapter = null;
    private List<String> dataList = null;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView lvLeftMenu;
    private String[] lvs = null;
    private ArrayAdapter arrayAdapter;
    private TextView appVersion;
    private TextView userNameInDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PgyUpdateManager.register(MenuActivity.this, "com.pgyersdk.provider");//检查版本更新情况
        setContentView(R.layout.activity_menu);
        DataBaseUtils.createNewTable(dataBaseHelper.getWritableDatabase());
        initViews();
        setButtonListener();
        categoryList.setAdapter(categoryAdapter);
    }

    private void initListViewInDrawer() {
        lvs = new String[]{getString(R.string.add_category_item),
                getString(R.string.delete_category_item),
                getString(R.string.start_gesture),
                getString(R.string.feedBack),
                getString(R.string.copyright),
                getString(R.string.share_weixin),
                "share to QQ"};
        if (PreferenceUtil.getBoolean("hasSetGestured", false, getApplicationContext())) {
            lvs[2] = getString(R.string.close_gesture);
        }
    }

    private void initToolsBar_drawerToggle() {
        toolbar.setTitle(getString(R.string.toolbar));//设置Toolbar标题
        toolbar.setTitleTextColor(Color.BLACK); //设置标题颜色
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                addCategoryText.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private void initViews() {
        initListViewInDrawer();
        findViesw();
        initToolsBar_drawerToggle();
        //设置菜单列表
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lvs);
        lvLeftMenu.setAdapter(arrayAdapter);
        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                leftMenuItemClick(position);
            }
        });
    }

    private void leftMenuItemClick(int position) {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        switch (position) {
            case 0:
                //增加分类
                addCategoryButton.setVisibility(View.VISIBLE);
                categoryAdapter.setShowDeleteButton(false);
                categoryAdapter.notifyDataSetChanged();
                break;
            case 1:
                //删除分类
                categoryAdapter.setShowDeleteButton(true);
                categoryAdapter.notifyDataSetChanged();
                break;
            case 2:
                controllGestureLock();//手势密码开关
                break;
            case 3:
                // 以对话框的形式弹出 反馈
                PgyFeedback.getInstance().showDialog(MenuActivity.this);
                break;
            case 4:
                //copyright
                showCopyRightDialog();
            case 5:
                shareSerectBoxToWechat();
            case 6:
                shareSerectBoxToQQ();
//                goToActivity(WebViewActivity.class);
                break;
        }
    }

    private void shareSerectBoxToQQ() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "SecretBox");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "I am SecretBox");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.qq.com/news/1.html");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "SecretBox");
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 1);
        mTencent.shareToQQ(MenuActivity.this, params, new BaseUiListener());
    }

    private void shareSerectBoxToWechat() {
        if (!api.isWXAppInstalled()) {
            ToastUtils.showMessage(this, "您还未安装微信");
            return;
        }
        // 0-分享给朋友  1-分享到朋友圈
        int flag = 0;
        //share to wechat
        WXTextObject wxTextObject = new WXTextObject();
        wxTextObject.text = "share to weixin SecretBox";
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

    private void showCopyRightDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.copyright))
                .setMessage(this.getString(R.string.copyright_msg))
                .setPositiveButton(this.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void controllGestureLock() {
        if (PreferenceUtil.getBoolean("hasSetGestured", false, getApplicationContext())) {
            //如果已经设置过手势密码，则清除，并回到登录界面
            PreferenceUtil.putBoolean("hasSetGestured", false, getApplicationContext());
            PreferenceUtil.putString("gesturedAnswer", "", getApplicationContext());
            goToActivity(LoginActivity.class);
        } else {
            //如果没有设置手势密码，或者已经清除，则跳转到手势密码设置界面
            categoryAdapter.setShowDeleteButton(false);
            categoryAdapter.notifyDataSetChanged();
            goToActivity(GestureSetActivity.class);
        }
    }

    private void goToActivity(Class<?> cls) {
        Intent intent = new Intent(MenuActivity.this, cls);
        startActivity(intent);
        finish();
    }

    private void setButtonListener() {
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MenuActivity.this, ItemActivity.class);
                intent.putExtra(ConstantUtils.CATEGORY, dataList.get(position));
                startActivity(intent);
            }
        });

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addCategoryText.getVisibility() == View.GONE) {
                    addCategoryText.setVisibility(View.VISIBLE);
                    addCategoryText.requestFocus();
                    return;
                }
                String itemString = StringUtils.stringFilter(addCategoryText.getText().toString());
                if (TextUtils.isEmpty(itemString)) {
//                    DataBaseUtils.printAllTable(dataBaseHelper.getWritableDatabase());
                    ToastUtils.showMessage(getApplicationContext(), getString(R.string.input_null));
                } else {
                    int i = 1;
                    String temCategory = "";
                    temCategory = "BOX_" + itemString;
                    while (dataList.contains(temCategory)) {
                        temCategory = "BOX_" + itemString + "_" + i;//当title值在数据中已经存在，则新的记录key后面加上"_1"
                        i++;
                    }

                    long result = DataBaseUtils.insertDataToCategoryTable(dataBaseHelper.getReadableDatabase(),
                            ConstantUtils.CATEGORY_TABLE_NAME, temCategory);
                    if (result == -1) {
                        ToastUtils.showMessage(getApplicationContext(), getString(R.string.add_fail));
                        return;
                    }
                    DataBaseUtils.createNewTable(dataBaseHelper.getWritableDatabase(), temCategory);//创建表
                    dataList.add(temCategory);
                    categoryAdapter.notifyDataSetChanged();
                    ToastUtils.showMessage(getApplicationContext(), getString(R.string.add_success));
                }
            }
        });
    }


    private List<String> getAllCategorys() {
        String sql = "select * from " + ConstantUtils.CATEGORY_TABLE_NAME;
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        List<String> categoryList = new ArrayList<String>();
        Cursor cursor = db.rawQuery(sql, null);
        String category = "";
        while (cursor.moveToNext()) {
            category = cursor.getString(cursor
                    .getColumnIndex(ConstantUtils.CATEGORY));
            categoryList.add(category);
        }
        if (categoryList.size() == 0) {
            DataBaseUtils.insertDataToCategoryTable(dataBaseHelper.getReadableDatabase(),
                    ConstantUtils.CATEGORY_TABLE_NAME, ConstantUtils.SAMPLE);
            DataBaseUtils.createNewTable(dataBaseHelper.getWritableDatabase(), ConstantUtils.SAMPLE);//创建表
            categoryList.add(ConstantUtils.SAMPLE);
        }
        return categoryList;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        exitBy2Click();
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            ToastUtils.showMessage(this, "再按一次退出程序");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
        }
    }

    private void findViesw() {
        addCategoryButton = (Button) findViewById(R.id.add_category_button);
        addCategoryText = (EditText) findViewById(R.id.add_category_editText);
        categoryList = (ListView) findViewById(R.id.menu_list);
        dataList = getAllCategorys();
        categoryAdapter = new CategoryAdapter(this, dataList, dataBaseHelper);
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        lvLeftMenu = (ListView) findViewById(R.id.lv_left_menu);
        appVersion = (TextView) findViewById(R.id.app_version);
        appVersion.setText("Version:" + ConstantUtils.getAPPVersionCode(this));
        userNameInDrawer=(TextView)findViewById(R.id.user_name_in_drawer);
        userNameInDrawer.setText(ConstantUtils.USER_NAME+"  welcome back ");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 自定义摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
        PgyFeedbackShakeManager.setShakingThreshold(1000);
        // 以对话框的形式弹出
        PgyFeedbackShakeManager.register(MenuActivity.this);
        // 以Activity的形式打开，这种情况下必须在AndroidManifest.xml配置FeedbackActivity
        // 打开沉浸式,默认为false
        // FeedbackActivity.setBarImmersive(true);
        PgyFeedbackShakeManager.register(MenuActivity.this, false);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        PgyFeedbackShakeManager.unregister();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        api.unregisterApp();
        mTencent.logout(this);
    }

}

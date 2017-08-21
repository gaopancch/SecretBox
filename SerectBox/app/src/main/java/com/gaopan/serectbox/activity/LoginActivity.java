package com.gaopan.serectbox.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopan.serectbox.Listener.BaseUiListener;
import com.gaopan.serectbox.R;
import com.gaopan.serectbox.utils.ConstantUtils;
import com.gaopan.serectbox.utils.PreferenceUtil;
import com.gaopan.serectbox.utils.ToastUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.UIListenerManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A qqLogin screen that offers qqLogin via email/password.
 */
public class LoginActivity extends BaseActivity implements LoaderCallbacks<Cursor>{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the qqLogin task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText passwordConfirmText;
    private Button mEmailSignInButton;
    private Button mRegisterButton;
    private ImageButton qqLoginButton;
    private UserInfo qqUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(PreferenceUtil.getBoolean("hasSetGestured",false,getApplicationContext())){
            if(!PreferenceUtil.getBoolean("gesture_try_false",false,getApplicationContext())) {
                goToActivity(GestureSetActivity.class);
            }
        }
        setContentView(R.layout.activity_login);
        getUserInfoFromPreference();
        initTextViews();
        initButtons();
    }

    private void getUserInfoFromPreference(){
        ConstantUtils.USER_NAME=PreferenceUtil.getString("username","",getApplicationContext());
        ConstantUtils.PASSWORD=PreferenceUtil.getString("password","",getApplicationContext());
    }

    private void initTextViews(){
        // Set up the qqLogin form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        passwordConfirmText = (EditText) findViewById(R.id.passwordagain);
        populateAutoComplete();
        if(TextUtils.isEmpty(ConstantUtils.USER_NAME)) {
            mEmailView.setText(getString(R.string.user_name));
        }else{
            mEmailView.setText(ConstantUtils.USER_NAME);
        }
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    private void initButtons(){
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mRegisterButton= (Button) findViewById(R.id.email_register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        qqLoginButton=(ImageButton)findViewById(R.id.qq_login);
        qqLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                qqLogin();
            }
        });
    }

    private void qqLogin()
    {
        boolean isSessionValid=mTencent.isSessionValid();
        if (!isSessionValid)
        {
            mTencent.login(this, "all", new QQLoginListener());
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the qqLogin form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual qqLogin attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the qqLogin attempt.
        String userName = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!password.equals(ConstantUtils.PASSWORD)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid userName address.
        if (TextUtils.isEmpty(userName)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!userName.equals(ConstantUtils.USER_NAME)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt qqLogin and focus the first
            // form field with an error.
            focusView.requestFocus();
            if(ConstantUtils.isDebug) {
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user qqLogin attempt.
            goToActivity(MenuActivity.class);
        }
    }

    private void goToActivity(Class<?> cls){
        Intent intent=new Intent(LoginActivity.this,cls);
        startActivity(intent);
        finish();
    }


    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceUtil.putBoolean("gesture_try_false",false,getApplicationContext());
    }

    private void registerUser(){
        if(passwordConfirmText.getVisibility()==View.GONE) {
            mEmailView.setText(ConstantUtils.USER_NAME);
            mEmailView.requestFocus();
            passwordConfirmText.setVisibility(View.VISIBLE);
            mEmailSignInButton.setVisibility(View.GONE);
        }else{
            String password=mPasswordView.getText().toString();
            String userName=mEmailView.getText().toString();
            if(TextUtils.isEmpty(userName)){
                ToastUtils.showMessage(this,getString(R.string.user_name_null));
            }
            if(userName.equals(ConstantUtils.USER_NAME)){
                ToastUtils.showMessage(this,getString(R.string.account_exist));
                return;
            }
            if(!TextUtils.isEmpty(userName)&&password.equals(passwordConfirmText.getText().toString())){
                PreferenceUtil.putString("password",password,getApplicationContext());
                PreferenceUtil.putString("username",userName,getApplicationContext());
                ConstantUtils.USER_NAME=userName;
                ConstantUtils.PASSWORD=password;
                goToActivity(MenuActivity.class);
            }else{
                ToastUtils.showMessage(this,getString(R.string.prompt_password_confirm_error));
            }
        }
    }

    private void updateQQInfo(){
        qqAuth=QQAuth.createInstance(ConstantUtils.QQ_APPID,getApplicationContext());
        QQToken token=mTencent.getQQToken();
        qqUserInfo=new UserInfo(this,token);
        qqUserInfo.getOpenId(new BaseUiListener());
        qqUserInfo.getUserInfo(new QQUserInfoListener());
    }

    private class QQLoginListener implements IUiListener {

        @Override
        public void onComplete(Object value) {
            try {
                JSONObject jo = (JSONObject) value;

                int ret = jo.getInt("ret");

                System.out.println("json=" + String.valueOf(jo));

                if (ret == 0) {
                    Toast.makeText(LoginActivity.this, "登录成功",
                            Toast.LENGTH_LONG).show();

                    String openID = jo.getString("openid");
                    String accessToken = jo.getString("access_token");
                    String expires = jo.getString("expires_in");
                    mTencent.setOpenId(openID);
                    mTencent.setAccessToken(accessToken, expires);
                }

            } catch (Exception e) {
                // TODO: handle exception
            }
            updateQQInfo();
        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }
    private  class QQUserInfoListener implements IUiListener{

        @Override
        public void onComplete(Object response) {
            JSONObject json = (JSONObject) response;
            // 昵称
            String nickname = null;
            try {
                nickname = json
                        .getString("nickname");
                PreferenceUtil.putString("username",nickname,getApplicationContext());
                ConstantUtils.USER_NAME=nickname;
                goToActivity(MenuActivity.class);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }
}


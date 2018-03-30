package com.shuishou.kitchen.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shuishou.kitchen.InstantValue;
import com.shuishou.kitchen.R;
import com.shuishou.kitchen.bean.UserData;
import com.shuishou.kitchen.http.HttpOperator;
import com.shuishou.kitchen.io.IOOperator;
import com.yanzhenjie.nohttp.NoHttp;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String INTENTEXTRA_LOGINUSER = "LOGINUSER";
    private String TAG_SERVERURL = "serverurl";
    private String TAG_LOGIN = "login";

    private TextView txtName;
    private TextView txtPassword;
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity_layout);
        InstantValue.URL_TOMCAT = IOOperator.loadServerURL(InstantValue.FILE_SERVERURL);
        TextView tvServerURL = (TextView)findViewById(R.id.drawermenu_serverurl);
        tvServerURL.setTag(TAG_SERVERURL);
        tvServerURL.setOnClickListener(this);

        txtName = (TextView) findViewById(R.id.txtName);
        txtPassword = (TextView) findViewById(R.id.txtPassword);
        btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setTag(TAG_LOGIN);
        btnLogin.setOnClickListener(this);

        NoHttp.initialize(this);
    }

    private void doLogin(){
        if (txtName.getText() == null || txtName.getText().length() == 0){
            Toast.makeText(this, "Please input name", Toast.LENGTH_LONG).show();
            return;
        }
        if (txtPassword.getText() == null || txtPassword.getText().length() == 0){
            Toast.makeText(this, "Please input password", Toast.LENGTH_LONG).show();
            return;
        }
        HttpOperator ho = new HttpOperator(this);
        ho.login(txtName.getText().toString(), txtPassword.getText().toString());
    }

    public void loginSuccess(UserData loginUser){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(INTENTEXTRA_LOGINUSER, loginUser);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (TAG_SERVERURL.equals(v.getTag())){
            SaveServerURLDialog dlg = new SaveServerURLDialog(LoginActivity.this);
            dlg.showDialog();
        } else if (TAG_LOGIN.equals(v.getTag())){
            doLogin();
        }
    }
}

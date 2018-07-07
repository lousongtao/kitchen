package com.shuishou.kitchen.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.shuishou.kitchen.InstantValue;
import com.shuishou.kitchen.R;
import com.shuishou.kitchen.utils.CommonTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 22/06/2018.
 */

public class UpgradeAppDialog {
    private MainActivity mainActivity;
    private TextView txtCurrentVersion;
    private TableLayout versionLayout;
    private ArrayList<RadioButton> rbVersions = new ArrayList<>();
    private ArrayList<String> versions = new ArrayList<>();
    private RadioButtonListener listener = new RadioButtonListener();
    private AlertDialog dlg;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS = 1;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG = 2;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_FINISHQUERY = 0;
    private ProgressDialog progressDlg;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_FINISHQUERY) {
                listAvailableApk();
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS){
                progressDlg = ProgressDialog.show(mainActivity, "Loading", "downloading apk, please wait...");
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG){
                if (progressDlg != null){
                    progressDlg.dismiss();
                }
            }
            super.handleMessage(msg);
        }
    };
    public UpgradeAppDialog(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        initUI();
        initData();
    }

    private void listAvailableApk(){
        int margin = 5;
        TableRow.LayoutParams trlp = new TableRow.LayoutParams();
        trlp.setMargins(margin, margin ,0 ,0);
        TableRow tr = null;
        if (versions == null || versions.size() == 0){
            tr = new TableRow(mainActivity);
            TextView tv = new TextView(mainActivity);
            tv.setTextSize(18);
            tv.setText("Cannot find any version on server!");
            tr.addView(tv);
            versionLayout.addView(tr);
        } else {
            for (int i = 0; i < versions.size(); i++) {
                if (i % 4 == 0){
                    tr = new TableRow(mainActivity);
                    versionLayout.addView(tr);
                }
                RadioButton rb = new RadioButton(mainActivity);
                rbVersions.add(rb);
                rb.setOnClickListener(listener);
                rb.setText(versions.get(i));
                tr.addView(rb, trlp);
            }
        }
    }

    private void initData(){
        new Thread(){
            @Override
            public void run() {
                versions = mainActivity.getHttpOperator().getUpgradeApkFiles();
                handler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_FINISHQUERY, null));
            }
        }.start();
        PackageManager pm = mainActivity.getPackageManager();
        try {
            PackageInfo packInfo = pm.getPackageInfo(mainActivity.getPackageName(), 0);
            txtCurrentVersion.setText("Current Version : "+packInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            MainActivity.LOG.error(InstantValue.DFYMDHMS.format(new Date()) + e.getMessage());
        }


    }

    private void initUI() {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.upgrade_dialog_layout, null);
        versionLayout = (TableLayout) view.findViewById(R.id.versionLayout);
        txtCurrentVersion = (TextView) view.findViewById(R.id.txtCurrentVersion);

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, AlertDialog.THEME_HOLO_LIGHT);
        builder.setNegativeButton("Close", null);
        builder.setPositiveButton("Upgrade", null);
        builder.setView(view);
        dlg = builder.create();
        dlg.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //add listener for buttons
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doUpgrade();
                    }
                });
            }
        });
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
        Window window = dlg.getWindow();
        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        param.y = 0;
        window.setAttributes(param);
    }

    private void doUpgrade(){
        String selectedVersion = null;
        for (int i = 0; i < rbVersions.size(); i++) {
            if (rbVersions.get(i).isChecked()){
                selectedVersion = rbVersions.get(i).getText().toString();
                break;
            }
        }
        if (selectedVersion == null){
            Toast.makeText(mainActivity, "must select one version to upgrade", Toast.LENGTH_LONG).show();
            return;
        }

        handler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS));
        final String filename = selectedVersion;
        new Thread(){
            @Override
            public void run() {
                try {
                    //download
                    URL url = new URL(InstantValue.URL_TOMCAT + "/../" + InstantValue.SERVER_CATEGORY_UPGRADEAPK + "/" + filename);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();
                    if ( code == 200 ) {
                        InputStream is = conn.getInputStream();
                        File file = new File(InstantValue.LOCAL_CATEGORY_UPGRADEAPK+filename);
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ( (len = is.read(buffer)) != -1 ) {
                            fos.write(buffer, 0, len);
                        }
                        fos.flush();
                        fos.close();
                        is.close();

                        handler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG));
                        //install
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setAction("android.intent.action.VIEW");
                        intent.addCategory("android.intent.category.DEFAULT");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri uri = FileProvider.getUriForFile(mainActivity, "com.shuishou.digitalmenu.fileprovider", file);
                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        } else {

                            try {
                                Runtime.getRuntime().exec("chmod 777 " + file.getCanonicalPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Uri uri = Uri.fromFile(file);
                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        }
                        mainActivity.startActivity(intent);
                    }
                } catch (Exception e) {
                    MainActivity.LOG.error("Upgrade Error", e);
                }
            }
        }.start();
    }

    public void showDialog(){
        dlg.show();
    }

    class RadioButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (v instanceof RadioButton){
                for (int i = 0; i < rbVersions.size(); i++) {
                    rbVersions.get(i).setChecked(false);
                }
                ((RadioButton) v).setChecked(true);
            }
        }
    }
}

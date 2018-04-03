package com.shuishou.kitchen.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.kitchen.InstantValue;
import com.shuishou.kitchen.R;
import com.shuishou.kitchen.bean.Dish;
import com.shuishou.kitchen.bean.DishConfig;
import com.shuishou.kitchen.bean.HttpResult;
import com.shuishou.kitchen.utils.CommonTool;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONObject;

/**
 * 通过一个switch开关切换config的soldout状态.
 * Created by Administrator on 30/03/2018.
 */

public class DishConfigSoldListener implements View.OnClickListener{
    public static DishConfigSoldListener instance;
    private MainActivity mainActivity;
    private AlertDialog confirmDialog;
    private DishConfig dishConfig;
    private Gson gson = new Gson();
    private Switch swConfig;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS = 1;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG = 0;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_SHOWEXCEPTION = 2;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_CHANGESUCCESS = 3;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_CANCELOPERATION = 4; //取消操作
    private ProgressDialog progressDlg;
    private Handler progressDlgHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG) {
                if (progressDlg != null)
                    progressDlg.dismiss();
                confirmDialog.dismiss();
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS){
                if (progressDlg != null){
                    progressDlg.setMessage(msg.obj != null ? msg.obj.toString() : InstantValue.NULLSTRING);
                }
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_SHOWEXCEPTION){
                AlertDialog dlg = new AlertDialog.Builder(mainActivity)
                        .setTitle("Exception")
                        .setMessage((String)msg.obj)
                        .setNegativeButton("CLOSE", null)
                        .create();
                dlg.show();
                confirmDialog.dismiss();
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_CHANGESUCCESS){
                mainActivity.afterDishConfigStatusChange(dishConfig);
//                button.setText(dish.isSoldOut() ? DishCellComponent.TXT_SOLDOUT : DishCellComponent.TXT_ONSALE);
                if (progressDlg != null) {
                    progressDlg.dismiss();
                }
                confirmDialog.dismiss();
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_CANCELOPERATION){
                if (swConfig != null){
                    swConfig.setChecked(!swConfig.isChecked());
                }
                confirmDialog.dismiss();
            }
        }
    };

    private DishConfigSoldListener(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        confirmDialog = new AlertDialog.Builder(mainActivity, AlertDialog.THEME_HOLO_LIGHT)
                .setIcon(R.drawable.info)
                .setTitle("Confirm")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .create();
        confirmDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread() {
                            @Override
                            public void run() {
                                changeSoldoutStatus();
                            }
                        }.start();
                    }
                });
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread() {
                            @Override
                            public void run() {
                                //当选择否的时候, 把switch状态改回来
                                progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_CANCELOPERATION, null));
                            }
                        }.start();
                    }
                });
            }
        });
    }

    private void changeSoldoutStatus(){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/menu/changedishconfigsoldout", RequestMethod.POST);
        request.add("userId", String.valueOf(mainActivity.getLoginUser().getId()));
        request.add("configId", String.valueOf(dishConfig.getId()));
        request.add("isSoldOut", String.valueOf(!dishConfig.isSoldOut()));
        Response<JSONObject> response = NoHttp.startRequestSync(request);
        if (response.getException() != null){
            progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_SHOWEXCEPTION, response.getException()));
            return;
        }
        if (response.get() == null) {
            Log.e("DishSoldListener", "Error occur while change dishconfig id = " + dishConfig.getId() +" sold out status. response.get() is null.");
            MainActivity.LOG.error("Error occur while change dishconfig id = " + dishConfig.getId() + " sold out status. response.get() is null.");
            progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_SHOWEXCEPTION, "Error occur while change dishConfig  soldout = " + String.valueOf(!dishConfig.isSoldOut())+ ". response.get() is null."));
            return;
        }
        HttpResult<DishConfig> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<DishConfig>>(){}.getType());
        dishConfig = result.data;
//        mainActivity.replaceDish(dish);
        progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_CHANGESUCCESS, null));
    }

    public static DishConfigSoldListener getInstance(MainActivity mainActivity){
        if (instance == null)
            instance = new DishConfigSoldListener(mainActivity);
        return instance;
    }

    @Override
    public void onClick(View v) {
        dishConfig = (DishConfig) v.getTag();
        swConfig= (Switch)v;
        String msg = "Do you want to set dish [" + dishConfig.getFirstLanguageName() +"] to the status of "
                + (dishConfig.isSoldOut() ? "ON SALE" : "SOLDOUT") + "?";
        confirmDialog.setMessage(msg);
        confirmDialog.show();
    }

    public void release(){
        if (confirmDialog.isShowing())
            confirmDialog.dismiss();
        instance = null;
    }
}

package com.shuishou.kitchen.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.kitchen.InstantValue;
import com.shuishou.kitchen.R;
import com.shuishou.kitchen.bean.Dish;
import com.shuishou.kitchen.bean.HttpResult;
import com.shuishou.kitchen.utils.CommonTool;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONObject;

/**
 * Created by Administrator on 29/03/2018.
 */

public class DishSoldListener implements View.OnClickListener{
    public static DishSoldListener instance;
    private MainActivity mainActivity;
    private AlertDialog confirmDialog;
    private Dish dish;
    private Gson gson = new Gson();
    private Button button;//响应点击事件的button对象
    public static final int PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS = 1;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG = 0;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_SHOWEXCEPTION = 2;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_CHANGESUCCESS = 3;
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
                mainActivity.afterDishStatusChange(dish);
//                button.setText(dish.isSoldOut() ? DishCellComponent.TXT_SOLDOUT : DishCellComponent.TXT_ONSALE);
                if (progressDlg != null) {
                    progressDlg.dismiss();
                    confirmDialog.dismiss();
                }
            }
        }
    };
    private DishSoldListener(MainActivity mainActivity){
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
                        startProgressDialog("", "start posting data ... ");
                        new Thread() {
                            @Override
                            public void run() {
                                changeSoldoutStatus();
                            }
                        }.start();
                    }
                });
            }
        });
    }

    private void changeSoldoutStatus(){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/menu/changedishsoldout", RequestMethod.POST);
        request.add("userId", String.valueOf(mainActivity.getLoginUser().getId()));
        request.add("id", String.valueOf(dish.getId()));
        request.add("isSoldOut", String.valueOf(!dish.isSoldOut()));
        Response<JSONObject> response = NoHttp.startRequestSync(request);
        if (response.getException() != null){
            progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_SHOWEXCEPTION, response.getException()));
            return;
        }
        if (response.get() == null) {
            Log.e("DishSoldListener", "Error occur while make order. response.get() is null.");
            MainActivity.LOG.error("Error occur while make order. response.get() is null.");
            progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_SHOWEXCEPTION, "Error occur while make dish soldout = " + String.valueOf(!dish.isSoldOut())+ ". response.get() is null."));
            return;
        }
        HttpResult<Dish> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<Dish>>(){}.getType());
        dish = result.data;
//        mainActivity.replaceDish(dish);
        progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_CHANGESUCCESS, null));
    }

    public static DishSoldListener getInstance(MainActivity mainActivity){
        if (instance == null)
            instance = new DishSoldListener(mainActivity);
        return instance;
    }

    public void startProgressDialog(String title, String message){
        progressDlg = ProgressDialog.show(mainActivity, title, message);
    }

    @Override
    public void onClick(View v) {
        dish = (Dish) v.getTag();
        button = (Button)v;
        String msg = "Do you want to set dish [" + dish.getFirstLanguageName() +"] to the status of "
                + (dish.isSoldOut() ? "ON SALE" : "SOLDOUT") + "?";
        confirmDialog.setMessage(msg);
        confirmDialog.show();
    }
}

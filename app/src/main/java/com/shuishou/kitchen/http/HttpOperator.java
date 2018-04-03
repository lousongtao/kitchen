package com.shuishou.kitchen.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.kitchen.InstantValue;
import com.shuishou.kitchen.R;
import com.shuishou.kitchen.bean.Category1;
import com.shuishou.kitchen.bean.Category2;
import com.shuishou.kitchen.bean.Dish;
import com.shuishou.kitchen.bean.DishConfig;
import com.shuishou.kitchen.bean.HttpResult;
import com.shuishou.kitchen.bean.MenuVersion;
import com.shuishou.kitchen.bean.MenuVersionInfo;
import com.shuishou.kitchen.bean.UserData;
import com.shuishou.kitchen.db.DBOperator;
import com.shuishou.kitchen.ui.LoginActivity;
import com.shuishou.kitchen.ui.MainActivity;
import com.shuishou.kitchen.utils.CommonTool;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2017/6/9.
 */

public class HttpOperator {

    private String logTag = "HttpOperation";


    private MainActivity mainActivity;
    private LoginActivity loginActivity;

//    private ArrayList<String> listDishPictures = new ArrayList<>();
    private static final int WHAT_VALUE_QUERYMENU = 1;
    private static final int WHAT_VALUE_LOGIN = 2;

    private Gson gson = new Gson();

    private OnResponseListener responseListener =  new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            switch (what){
                case WHAT_VALUE_QUERYMENU :
                    doResponseQueryMenu(response);
                    break;
                case WHAT_VALUE_LOGIN :
                    doResponseLogin(response);
                    break;
                default:
            }
        }

        @Override
        public void onFailed(int what, Response<JSONObject> response) {
            Log.e("Http failed", "what = "+ what + "\nresponse = "+ response.get());
            MainActivity.LOG.error("Response Listener On Faid. what = "+ what + "\nresponse = "+ response.get());
            switch (what){
                case WHAT_VALUE_QUERYMENU :
                    CommonTool.popupWarnDialog(mainActivity, R.drawable.error, "WRONG", "Failed to load Menu data. Please restart app!");
                    break;
                case WHAT_VALUE_LOGIN :
                    CommonTool.popupWarnDialog(loginActivity, R.drawable.error, "WRONG", "Failed to login. Please retry!");
                    break;
                default:
            }

        }

        @Override
        public void onFinish(int what) {
        }
    };

    private RequestQueue requestQueue = NoHttp.newRequestQueue();

    public HttpOperator(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public HttpOperator(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    private void doResponseLogin(Response<JSONObject> response){
        if (response.getException() != null){
            Log.e(logTag, "doResponseLogin: " + response.getException().getMessage() );
            MainActivity.LOG.error("doResponseLogin: " + response.getException().getMessage());
            sendErrorMessageToToast("doResponseLogin: " + response.getException().getMessage());
            return;
        }
        JSONObject loginResult = response.get();
        try {
            if ("ok".equals(loginResult.getString("result"))){
                UserData loginUser = new UserData();
                loginUser.setId(loginResult.getInt("userId"));
                loginUser.setName(loginResult.getString("userName"));
                loginActivity.loginSuccess(loginUser);
            } else {
                Log.e(logTag, "doResponseLogin: get FAIL while login" + loginResult.getString("result"));
                MainActivity.LOG.error("doResponseLogin: get FAIL while login"  + loginResult.getString("result"));
                sendErrorMessageToToast("doResponseLogin: get FAIL while login"  + loginResult.getString("result"));
            }
        } catch (JSONException e) {
            Log.e(logTag, "doResponseLogin: parse json: " + e.getMessage() );
            MainActivity.LOG.error("doResponseLogin: parse json:" + e.getMessage());
            sendErrorMessageToToast("doResponseLogin: parse json:" + e.getMessage());
        }
    }
    private void doResponseQueryMenu(Response<JSONObject> response){
        if (response.getException() != null){
            Log.e(logTag, "doResponseQueryMenu: " + response.getException().getMessage() );
            MainActivity.LOG.error("doResponseQueryMenu: " + response.getException().getMessage());
            sendErrorMessageToToast("Http:doResponseQueryMenu: " + response.getException().getMessage());
            return;
        }
        HttpResult<ArrayList<Category1>> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<ArrayList<Category1>>>(){}.getType());
        if (result.success){
            ArrayList<Category1> c1s = result.data;
            sortAllMenu(c1s);
            mainActivity.setMenu(c1s);
            mainActivity.buildMenu();
        }else {
            Log.e(logTag, "doResponseQueryMenu: get FALSE for query confirm code");
            MainActivity.LOG.error("doResponseQueryMenu: get FALSE for query confirm code");
        }
    }

    //load menu
    public void loadMenuData(){
        mainActivity.getProgressDlgHandler().sendMessage(CommonTool.buildMessage(MainActivity.PROGRESSDLGHANDLER_MSGWHAT_STARTLOADDATA,
                "start loading menu data ..."));
        Request<JSONObject> menuRequest = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/menu/querymenu");
        requestQueue.add(WHAT_VALUE_QUERYMENU, menuRequest, responseListener);
    }

    //sort by sequence
    private void sortAllMenu(ArrayList<Category1> c1s){
        if (c1s != null){
            Collections.sort(c1s, new Comparator<Category1>() {
                @Override
                public int compare(Category1 o1, Category1 o2) {
                    return o1.getSequence() - o2.getSequence();
                }
            });
            for (Category1 c1 : c1s) {
                if (c1.getCategory2s() != null){
                    Collections.sort(c1.getCategory2s(), new Comparator<Category2>() {
                        @Override
                        public int compare(Category2 o1, Category2 o2) {
                            return o1.getSequence() - o2.getSequence();
                        }
                    });
                    for (Category2 c2 : c1.getCategory2s()) {
                        if(c2.getDishes() != null){
                            Collections.sort(c2.getDishes(), new Comparator<Dish>() {
                                @Override
                                public int compare(Dish o1, Dish o2) {
                                    return o1.getSequence() - o2.getSequence();
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private void onFailedLoadMenu(){
        //TODO: require restart app
    }

    private void sendErrorMessageToToast(String sMsg){
        if (mainActivity != null)
            mainActivity.getToastHandler().sendMessage(CommonTool.buildMessage(MainActivity.TOASTHANDLERWHAT_ERRORMESSAGE,sMsg));
    }

    public void uploadErrorLog(File file, String machineCode){
        int key = 0;// the key of filelist;
        UploadErrorLogListener listener = new UploadErrorLogListener(mainActivity);
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/common/uploaderrorlog", RequestMethod.POST);
        FileBinary bin1 = new FileBinary(file);
        request.add("logfile", bin1);
        request.add("machineCode", machineCode);
        listener.addFiletoList(key, file.getAbsolutePath());
        requestQueue.add(key, request, listener);
    }

    public void login(String name, String password){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/login");
        request.add("username", name);
        request.add("password", password);
        requestQueue.add(WHAT_VALUE_LOGIN, request, responseListener);
    }

}

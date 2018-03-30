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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private static final int WHAT_VALUE_QUERYMENUVERSION = 5;

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
                case WHAT_VALUE_QUERYMENUVERSION:
                    doResponseQueryMenuVersion(response);
                    break;
                default:
            }
        }

        @Override
        public void onFailed(int what, Response<JSONObject> response) {
            Log.e("Http failed", "what = "+ what + "\nresponse = "+ response.get());
            MainActivity.LOG.error("Response Listener On Faid. what = "+ what + "\nresponse = "+ response.get());
            String msg = InstantValue.NULLSTRING;
            switch (what){
                case WHAT_VALUE_QUERYMENU :
                    msg = "Failed to load Menu data. Please restart app!";
                    CommonTool.popupWarnDialog(mainActivity, R.drawable.error, "WRONG", msg);
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
            mainActivity.persistMenu();
            mainActivity.popRestartDialog("Data refresh finish successfully. Please restart the app.");
        }else {
            Log.e(logTag, "doResponseQueryMenu: get FALSE for query confirm code");
            MainActivity.LOG.error("doResponseQueryMenu: get FALSE for query confirm code");
        }
    }

    private void doResponseQueryMenuVersion(Response<JSONObject> response){
        if (response.getException() != null){
            Log.e(logTag, "doResponseQueryMenuVersion: " + response.getException().getMessage() );
            MainActivity.LOG.error("doResponseQueryMenuVersion: " + response.getException().getMessage());
            sendErrorMessageToToast("Http:doResponseQueryMenuVersion: " + response.getException().getMessage());
            return;
        }
        HttpResult<Integer> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<Integer>>(){}.getType());
        if (result.success){
            mainActivity.getDbOperator().saveObjectByCascade(new MenuVersion(1, result.data));
        } else {
            Log.e(logTag, "doResponseQueryMenuVersion: get FALSE for query menu version");
            MainActivity.LOG.error("doResponseQueryMenuVersion: get FALSE for query menu version");
            CommonTool.popupWarnDialog(mainActivity, R.drawable.error, "WRONG", "Failed to load Menu version data. Please redo synchronization action!");
        }
    }

    //load menu
    public void loadMenuData(){
        mainActivity.getProgressDlgHandler().sendMessage(CommonTool.buildMessage(MainActivity.PROGRESSDLGHANDLER_MSGWHAT_STARTLOADDATA,
                "start loading menu data ..."));
        Request<JSONObject> menuRequest = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/menu/querymenu");
        requestQueue.add(WHAT_VALUE_QUERYMENU, menuRequest, responseListener);
    }

    //load menu version
    public void loadMenuVersionData(){
        Request<JSONObject> mvRequest = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/menu/getlastmenuversion", RequestMethod.POST);
        requestQueue.add(WHAT_VALUE_QUERYMENUVERSION, mvRequest, responseListener);
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

    /**
     * check the menu version difference between client and server
     * @param localVersion
     * @return if same return null, otherwise return a map including changed dishes and dishconfigs
     */
    public HashMap<String, ArrayList<Integer>> checkMenuVersion(int localVersion){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/menu/checkmenuversion", RequestMethod.POST);
        request.add("versionId", localVersion);
        Response<JSONObject> response = NoHttp.startRequestSync(request);
        if (response.getException() != null){
            Log.e(logTag, "chechMenuVersion: There are Exception to checkmenuversion\n"+ response.getException().getMessage() );//TODO:
            MainActivity.LOG.error("chechMenuVersion: There are Exception to checkmenuversion\n"+ response.getException().getMessage() );
            sendErrorMessageToToast("Http:chechMenuVersion: " + response.getException().getMessage());
            return null;
        }
        HttpResult<ArrayList<MenuVersionInfo>> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<ArrayList<MenuVersionInfo>>>(){}.getType());
        if (result.success){
            if (result.data == null)
                return null;
            DBOperator dbOpr = mainActivity.getDbOperator();
            //collect all change into a set to remove the duplicate dishid
            Set<Integer> dishIdSet = new HashSet<>();
            Set<Integer> dishConfigIdSet = new HashSet<>();
            int maxVersion = 0;//get the biggest version number in this update
            for (int i = 0; i < result.data.size(); i++) {
                MenuVersionInfo mvi = result.data.get(i);
                if (mvi.type == InstantValue.MENUCHANGE_TYPE_DISHCONFIGSOLDOUT){
                    dishConfigIdSet.add(mvi.objectId);
                } else if (mvi.type == InstantValue.MENUCHANGE_TYPE_DISHSOLDOUT) {
                    dishIdSet.add(mvi.objectId);
                }
                if (mvi.id > maxVersion)
                    maxVersion = mvi.id;
            }
            //reload info about dishes in dishIdSet
            ArrayList<Integer> dishIdList = new ArrayList<>();
            dishIdList.addAll(dishIdSet);
            ArrayList<Integer> dishConfigIdList = new ArrayList<>();
            dishConfigIdList.addAll(dishConfigIdSet);
            boolean bSyncDishes = synchronizeDishes(dishIdList);
            boolean bSyncDishConfigs = synchronizeDishConfig(dishConfigIdList);
            if (bSyncDishes & bSyncDishConfigs){//only persist the maxVersion while sync the dishes successfully
                dbOpr.deleteAllData(MenuVersion.class);
                MenuVersion mv = new MenuVersion(1, maxVersion);
                dbOpr.saveObjectByCascade(mv);
                HashMap<String, ArrayList<Integer>> map = new HashMap<>();
                map.put("dish", dishIdList);
                map.put("dishConfig", dishConfigIdList);
                return map;
            }
        } else {
            Log.e(logTag, "get false from server while Check Menu Version");
            MainActivity.LOG.error("get false from server while Check Menu Version");
            sendErrorMessageToToast("get false from server while Check Menu Version");
        }
        return null;
    }

    /**
     * load dishes data from server by the id list;
     * compare the SOLDOUT and PROMOTION value with the local data, if different, modify local data
     * @param dishIdList
     * @return false while exception occur.
     */
    private boolean synchronizeDishes(ArrayList<Integer> dishIdList){
        if (dishIdList.isEmpty())
            return true;
        String sIds = dishIdList.toString().replace("[","").replace("]","").replace(" ","");
        Request<JSONObject> reqDish = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/menu/querydishbyidlist", RequestMethod.POST);
        reqDish.add("dishIdList", sIds);
        Response<JSONObject> respDish = NoHttp.startRequestSync(reqDish);
        if (respDish.getException() != null){
            Log.e(logTag, "get Exception while call menu/querydishbyidlist for dishidlist = "+ dishIdList+", Exception is "+ respDish.getException());
            MainActivity.LOG.error("get Exception while call menu/querydishbyidlist for dishidlist = "+ dishIdList+", Exception is "+ respDish.getException());
            sendErrorMessageToToast("get Exception while call menu/querydishbyidlist for dishidlist = "+ dishIdList+", Exception is "+ respDish.getException());
            return false;
        }
        HttpResult<ArrayList<Dish>> result = gson.fromJson(respDish.get().toString(), new TypeToken<HttpResult<ArrayList<Dish>>>(){}.getType());
        if (!result.success){
            Log.e(logTag, "get false value while call menu/querydishbyidlist for dishidlist = "+ dishIdList+", Exception is "+ respDish.getException());
            MainActivity.LOG.error("get false value while call menu/querydishbyidlist for dishidlist = "+ dishIdList+", Exception is "+ respDish.getException());
            sendErrorMessageToToast("get false value while call menu/querydishbyidlist for dishidlist = "+ dishIdList+", Exception is "+ respDish.getException());
            return false;
        }
        ArrayList<Dish> dishes = result.data;
        DBOperator dbOpr = mainActivity.getDbOperator();
        for (int i = 0; i < dishes.size(); i++) {
            Dish dish = dishes.get(i);
            Dish dbDish = dbOpr.queryDishById(dish.getId());
            if (dbDish == null){
                sendErrorMessageToToast("find unrecognized dish '"+dish.getFirstLanguageName()+"', please refresh data on this device.");
                return false;
            }
            if (dish.isSoldOut() != dbDish.isSoldOut()) {
                dbDish.setSoldOut(dish.isSoldOut());
                dbOpr.updateObject(dbDish);
            }
            if (dish.isPromotion() != dbDish.isPromotion()){
                dbDish.setPromotion(dish.isPromotion());
                dbDish.setOriginPrice(dish.getOriginPrice());
                dbDish.setPrice(dish.getPrice());
                dbOpr.updateObject(dbDish);
            }
        }
        return true;
    }

    /**
     * load dishes data from server by the id list;
     * compare the SOLDOUT and PROMOTION value with the local data, if different, modify local data
     * @param dishConfigIdList
     * @return false while exception occur.
     */
    private boolean synchronizeDishConfig(ArrayList<Integer> dishConfigIdList){
        if (dishConfigIdList.isEmpty())
            return true;
        String sIds = dishConfigIdList.toString().replace("[","").replace("]","").replace(" ","");
        Request<JSONObject> reqDish = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/menu/querydishconfigbyidlist", RequestMethod.POST);
        reqDish.add("dishConfigIdList", sIds);
        Response<JSONObject> respDish = NoHttp.startRequestSync(reqDish);
        if (respDish.getException() != null){
            Log.e(logTag, "get Exception while call menu/querydishconfigbyidlist for dishConfigIdList = "+ dishConfigIdList+", Exception is "+ respDish.getException());
            MainActivity.LOG.error("get Exception while call menu/querydishconfigbyidlist for dishConfigIdList = "+ dishConfigIdList+", Exception is "+ respDish.getException());
            sendErrorMessageToToast("get Exception while call menu/querydishconfigbyidlist for dishConfigIdList = "+ dishConfigIdList+", Exception is "+ respDish.getException());
            return false;
        }
        HttpResult<ArrayList<DishConfig>> result = gson.fromJson(respDish.get().toString(), new TypeToken<HttpResult<ArrayList<DishConfig>>>(){}.getType());
        if (!result.success){
            Log.e(logTag, "get false value while call menu/querydishconfigbyidlist for dishConfigIdList = "+ dishConfigIdList+", Exception is "+ respDish.getException());
            MainActivity.LOG.error("get false value while call menu/querydishconfigbyidlist for dishConfigIdList = "+ dishConfigIdList+", Exception is "+ respDish.getException());
            sendErrorMessageToToast("get false value while call menu/querydishconfigbyidlist for dishConfigIdList = "+ dishConfigIdList+", Exception is "+ respDish.getException());
            return false;
        }
        ArrayList<DishConfig> dishConfigs = result.data;
        DBOperator dbOpr = mainActivity.getDbOperator();
        for (int i = 0; i < dishConfigs.size(); i++) {
            DishConfig dishConfig = dishConfigs.get(i);
            DishConfig dbDishConfig = (DishConfig) dbOpr.queryObjectById(dishConfig.getId(), DishConfig.class);
            if (dbDishConfig == null){
                sendErrorMessageToToast("find unrecognized dishConfig '"+dishConfig.getFirstLanguageName()+"', please refresh data on this device.");
                return false;
            }
            if (dishConfig.isSoldOut() != dbDishConfig.isSoldOut()) {
                dbDishConfig.setSoldOut(dishConfig.isSoldOut());
                dbOpr.updateObject(dbDishConfig);
            }
        }
        return true;
    }

    private void sendErrorMessageToToast(String sMsg){
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

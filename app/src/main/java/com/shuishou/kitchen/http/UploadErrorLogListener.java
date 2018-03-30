package com.shuishou.kitchen.http;

import android.os.Handler;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.kitchen.bean.HttpResult;
import com.shuishou.kitchen.io.CrashHandler;
import com.shuishou.kitchen.ui.MainActivity;
import com.shuishou.kitchen.utils.CommonTool;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 2017/10/5.
 */

public class UploadErrorLogListener implements OnResponseListener<JSONObject> {
//    private MainActivity mainActivity;
    private Handler handler;
    //use a int key to point to a file name, as well as the 'what' value
    //this what value can be used in success/fail/finish function
    //IMPORTANT: SparseArray is not thread-safe
    private SparseArray<String> filelist = new SparseArray<>();
    private int totalFileAmount ;

    //to avoid generate too many objects, define some string here
    private String strLoading = "UpLoading ";
    private String strSlash =  " / ";
    private String strPic =  " log files";

    public UploadErrorLogListener(MainActivity mainActivity){
//        this.mainActivity = mainActivity;
        handler = mainActivity.getProgressDlgHandler();
    }
    @Override
    public void onStart(int what) {

    }

    @Override
    public void onSucceed(int what, Response response) {
        String filename = getThreadSafeArray().get(what);
        getThreadSafeArray().remove(what);
        int left = getThreadSafeArray().size();
        HttpResult<String> result = new Gson().fromJson(response.get().toString(), new TypeToken<HttpResult<String>>(){}.getType());
        if (result.success){
            //delte the file
            File file = new File(filename);
            file.delete();
            //change progress dialog text
            handler.sendMessage(CommonTool.buildMessage(MainActivity.PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS,
                    strLoading + (totalFileAmount - left) + strSlash + totalFileAmount + strPic));
        } else {
            //write error to log
            String s = "get FALSE from server while uploading error log : " + filename;
            if (response != null && response.get() != null)
                s += response.toString();
            Exception ex = new Exception(s);
            CrashHandler.getInstance().handleException(ex, false); //write exception to log file
        }
        //if left == 0, close progress dialog
        if (left == 0){
            handler.sendMessage(CommonTool.buildMessage(MainActivity.PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG,
                    strLoading + (totalFileAmount - left) + strSlash + totalFileAmount + strPic));
        }
    }

    @Override
    public void onFailed(int what, Response response) {
        String filename = getThreadSafeArray().valueAt(what);
        getThreadSafeArray().remove(what);
        int left = getThreadSafeArray().size();
        String s = "failed to upload error log : " + filename;
        if (response != null && response.get() != null)
            s += response.toString();
        Exception ex = new Exception(s);
        CrashHandler.getInstance().handleException(ex, false); //write exception to log file
        //if left == 0, close progress dialog
        if (left == 0){
            handler.sendMessage(CommonTool.buildMessage(MainActivity.PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG,
                    strLoading + (totalFileAmount - left) + strSlash + totalFileAmount + strPic));
        }
    }

    @Override
    public void onFinish(int what) {
    }

    public void addFiletoList(int key, String filename){
        getThreadSafeArray().append(key, filename);
    }

    public void setTotalFileAmount(int total){
        totalFileAmount = total;
    }

    private synchronized SparseArray<String> getThreadSafeArray(){
        return filelist;
    }
}

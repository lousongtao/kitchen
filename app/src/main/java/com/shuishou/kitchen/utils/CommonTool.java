package com.shuishou.kitchen.utils;

import android.content.Context;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.shuishou.kitchen.InstantValue;

/**
 * Created by Administrator on 2017/10/5.
 */

public class CommonTool {
    public static Message buildMessage(int what, Object obj, int arg1, int arg2){
        Message msg = new Message();
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.what = what;
        msg.obj = obj;
        return msg;
    }

    public static Message buildMessage(int what, Object obj){
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        return msg;
    }

    public static Message buildMessage(int what){
        Message msg = new Message();
        msg.what = what;
        return msg;
    }

    public static void popupToast(Context context, String msg, int shortlong){
        Toast.makeText(context, msg, shortlong).show();
    }

    public static void popupWarnDialog(Context context, int iconId, String title, String msg){
        new AlertDialog.Builder(context)
                .setIcon(iconId)
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton("OK", null)
                .create().show();
    }

    public static String transferDouble2Scale(double d){
        return String.format(InstantValue.FORMAT_DOUBLE, d);
    }

    /**
     * if d is positive, return d with 2 decimal
     * if d is negative, return -d with 2 decimal, surrounding the bracket()
     * @param d
     * @return
     */
    public static String transferNumberByPM(double d, String currencyIcon){
        if (d >= 0){
            if (currencyIcon != null)
                return transferDouble2Scale(d);
            else
                return currencyIcon + transferDouble2Scale(d);
        } else {
            if (currencyIcon != null)
                return "(" + currencyIcon + transferDouble2Scale(d * (-1)) + ")";
            else
                return "(" + transferDouble2Scale(d * (-1)) + ")";
        }
    }
}

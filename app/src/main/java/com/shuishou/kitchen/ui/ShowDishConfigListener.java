package com.shuishou.kitchen.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.kitchen.InstantValue;
import com.shuishou.kitchen.R;
import com.shuishou.kitchen.bean.Dish;
import com.shuishou.kitchen.bean.DishConfig;
import com.shuishou.kitchen.bean.DishConfigGroup;
import com.shuishou.kitchen.bean.HttpResult;
import com.shuishou.kitchen.ui.components.BorderView;
import com.shuishou.kitchen.utils.CommonTool;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 30/03/2018.
 */

public class ShowDishConfigListener implements View.OnClickListener{
    public static ShowDishConfigListener instance;
    private MainActivity mainActivity;
    private Dish dish;

    private ShowDishConfigListener(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public static ShowDishConfigListener getInstance(MainActivity mainActivity){
        if (instance == null)
            instance = new ShowDishConfigListener(mainActivity);
        return instance;
    }

    public static void rebuildInstance(MainActivity mainActivity){
        instance = new ShowDishConfigListener(mainActivity);
    }

    @Override
    public void onClick(View v) {
        dish = (Dish) v.getTag();
        int ROW_COMPONENT_AMOUNT = 4;//每行显示的控件数目
        ArrayList<DishConfigGroup> groups = dish.getConfigGroups();
        Collections.sort(groups, new Comparator<DishConfigGroup>() {
            @Override
            public int compare(DishConfigGroup o1, DishConfigGroup o2) {
                return o1.getSequence() - o2.getSequence();
            }
        });
        DishConfigSoldListener listener = DishConfigSoldListener.getInstance(mainActivity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 0, 0 ,0);
//        LinearLayout contentview = new LinearLayout(mainActivity);
//        contentview.setOrientation(LinearLayout.VERTICAL);
        TableLayout contentview = new TableLayout(mainActivity);
        for (int i = 0; i < groups.size(); i++) {
            DishConfigGroup group = groups.get(i);

            ArrayList<DishConfig> configs = group.getDishConfigs();
            TableRow tableRow = null;
            for (int j = 0; j < configs.size(); j++) {
                DishConfig config = configs.get(j);
                if (i % ROW_COMPONENT_AMOUNT == 0) {
                    tableRow = new TableRow(mainActivity);
                    contentview.addView(tableRow);
                }
                Switch swConfig = new Switch(mainActivity);
                swConfig.setText(config.getFirstLanguageName());
                swConfig.setTextSize(25);
                swConfig.setEllipsize(TextUtils.TruncateAt.END);
                swConfig.setSingleLine(true);
                swConfig.setChecked(!config.isSoldOut());
                swConfig.setTag(config);
                swConfig.setOnClickListener(listener);
                tableRow.addView(swConfig);
            }
        }
        ScrollView scrollView = new ScrollView(mainActivity);
        scrollView.addView(contentview);
        LinearLayout view = new LinearLayout(mainActivity);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setLayoutParams(layoutParams);
        view.addView(scrollView);
        AlertDialog dialog = new AlertDialog.Builder(mainActivity, AlertDialog.THEME_HOLO_LIGHT)
                .setNegativeButton("Close", null)
                .setView(view)
                .create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        param.y = 50;
        param.width = WindowManager.LayoutParams.MATCH_PARENT;
        param.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(param);
    }

    public void release(){
        instance = null;
    }
}

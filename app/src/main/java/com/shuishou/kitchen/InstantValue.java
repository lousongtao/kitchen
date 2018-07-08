package com.shuishou.kitchen;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/12/22.
 */

public final class InstantValue {
    public static final DateFormat DFYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String DOLLAR = "$";
    public static final String DOLLARSPACE = "$ ";
    public static final String NULLSTRING = "";
    public static final String SPACESTRING = " ";
    public static final String SLLASHSTRING = " ";
    public static final String ENTERSTRING = "\n";

    public static final String FORMAT_DOUBLE = "%.2f";


    public static final int DISPLAY_DISH_WIDTH = 240;
    public static final int DISPLAY_DISH_HEIGHT = 300;


    public static final byte DISH_CHOOSEMODE_DEFAULT = 1;

    public static final byte DISH_PURCHASETYPE_UNIT = 1;//按份购买
    public static final byte DISH_PURCHASETYPE_WEIGHT = 2;//按重量购买

    public static final String FORMAT_DOUBLE_2DECIMAL = "%.2f";

    public static String URL_TOMCAT = null;
    public static final String LOCAL_CATALOG_ERRORLOG = "/data/data/com.shuishou.kitchen/errorlog/";
    public static final String FILE_SERVERURL = "/data/data/com.shuishou.kitchen/serverconfig";
    public static final String ERRORLOGPATH = "/data/data/com.shuishou.kitchen/errorlog/";

    public static final String SERVER_CATEGORY_UPGRADEAPK = "upgradeApk";
    public static final String LOCAL_CATEGORY_UPGRADEAPK = "/data/user/0/com.shuishou.kitchen/files/";

    public static final byte MENUCHANGE_TYPE_DISHSOLDOUT = 0;//设置soldout或取消soldout
    public static final byte MENUCHANGE_TYPE_CHANGEPROMOTION = 1;//设置promotion或取消promotion
    public static final byte MENUCHANGE_TYPE_DISHCONFIGSOLDOUT = 2;//设置soldout或取消soldout
}

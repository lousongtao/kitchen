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
    /**
     * 测试中发现, 只有使用sdcard目录存储apk才能实现APP内部更新.
     * 1. 安装app需要先将apk拷贝到设备上, 然后运行安装; 不能直接使用Android studio运行到设备上, 因为as使用的是debug key,
     *    这样在升级安装时, 由于签名不同, 会导致无法升级. (这一步也许可以找到解决的办法, 但是目前不知道怎么实现)
     * 2. app内部目录, /data/data/com.shuishou.digitalmenu/.. 这些目录在设备上无法访问, 故无法把apk文件放置进去
     * 3. 安卓模拟器不能使用sdcard目录, 会报告permission deny的错误. 只有实体设备才有这个目录
     */
    public static final String LOCAL_CATEGORY_UPGRADEAPK = "/sdcard/";

    public static final byte MENUCHANGE_TYPE_DISHSOLDOUT = 0;//设置soldout或取消soldout
    public static final byte MENUCHANGE_TYPE_CHANGEPROMOTION = 1;//设置promotion或取消promotion
    public static final byte MENUCHANGE_TYPE_DISHCONFIGSOLDOUT = 2;//设置soldout或取消soldout
}

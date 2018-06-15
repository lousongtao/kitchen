package com.shuishou.kitchen.ui;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.shuishou.kitchen.InstantValue;
import com.shuishou.kitchen.R;
import com.shuishou.kitchen.bean.Category1;
import com.shuishou.kitchen.bean.Category2;
import com.shuishou.kitchen.bean.Dish;
import com.shuishou.kitchen.bean.DishConfig;
import com.shuishou.kitchen.bean.DishConfigGroup;
import com.shuishou.kitchen.bean.MenuVersion;
import com.shuishou.kitchen.bean.UserData;
import com.shuishou.kitchen.db.DBOperator;
import com.shuishou.kitchen.http.HttpOperator;
import com.shuishou.kitchen.io.IOOperator;
import com.shuishou.kitchen.utils.CommonTool;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MainActivity.class.getSimpleName());
    private String TAG_UPLOADERRORLOG = "uploaderrorlog";
    private String TAG_EXITSYSTEM = "exitsystem";
    private CategoryTabListView listViewCategorys;
    private UserData loginUser;
    private ArrayList<Category1> category1s = new ArrayList<>(); // = TestData.makeCategory1();
    private HttpOperator httpOperator;
    private RecyclerSoldoutDishAdapter soldoutDishAdapter;
    private ArrayList<Dish> soldoutDishList = new ArrayList<>();

    private SparseArray<DishDisplayFragment> mapDishDisplayFragments = new SparseArray<>();
    private SparseArray<DishCellComponent> mapDishCellComponents = new SparseArray<>();

    public static final int PROGRESSDLGHANDLER_MSGWHAT_STARTLOADDATA = 3;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_DOWNFINISH = 2;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS = 1;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG = 0;
    private ProgressDialog progressDlg;
    private Handler progressDlgHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG) {
                if (progressDlg != null)
                    progressDlg.dismiss();
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS){
                if (progressDlg != null){
                    progressDlg.setMessage(msg.obj != null ? msg.obj.toString() : InstantValue.NULLSTRING);
                }
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_DOWNFINISH){
                if (progressDlg != null){
                    progressDlg.setMessage(msg.obj != null ? msg.obj.toString() : InstantValue.NULLSTRING);
                }
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_STARTLOADDATA){
                if (progressDlg != null){
                    progressDlg.setMessage(msg.obj != null ? msg.obj.toString() : InstantValue.NULLSTRING);
                }
            }
        }
    };
    public static final int TOASTHANDLERWHAT_ERRORMESSAGE = 0;
    private Handler toastHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TOASTHANDLERWHAT_ERRORMESSAGE){
                Toast.makeText(MainActivity.this,msg.obj != null ? msg.obj.toString() : InstantValue.NULLSTRING, Toast.LENGTH_LONG).show();
            }
        }
    };

    public Handler getToastHandler(){
        return toastHandler;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginUser = (UserData)getIntent().getExtras().getSerializable(LoginActivity.INTENTEXTRA_LOGINUSER);
        setContentView(R.layout.activity_main);

        RecyclerView lvSoldout = (RecyclerView) findViewById(R.id.list_soldout);
        soldoutDishAdapter = new RecyclerSoldoutDishAdapter(this, R.layout.dish_soldoutlistitem_layout, soldoutDishList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lvSoldout.setLayoutManager(layoutManager);
        lvSoldout.setAdapter(soldoutDishAdapter);

        TextView tvUploadErrorLog = (TextView)findViewById(R.id.drawermenu_uploaderrorlog);
        TextView tvExit = (TextView)findViewById(R.id.drawermenu_exit);
        listViewCategorys = (CategoryTabListView) findViewById(R.id.categorytab_listview);

        tvUploadErrorLog.setTag(TAG_UPLOADERRORLOG);
        tvExit.setTag(TAG_EXITSYSTEM);
        tvUploadErrorLog.setOnClickListener(this);
        tvExit.setOnClickListener(this);

        //init tool class, NoHttp
        NoHttp.initialize(this);
        Logger.setDebug(true);
        Logger.setTag("kitchen:nohttp");

        InstantValue.URL_TOMCAT = IOOperator.loadServerURL(InstantValue.FILE_SERVERURL);
        httpOperator = new HttpOperator(this);
        httpOperator.loadMenuData();

    }

    private void initSoldoutList(){
        for (int i = 0; i< category1s.size(); i++){
            Category1 c1 = category1s.get(i);
            if (c1.getCategory2s() != null){
                for (int j = 0; j < c1.getCategory2s().size(); j++){
                    Category2 c2 = c1.getCategory2s().get(j);
                    if (c2.getDishes() != null){
                        for (int k = 0; k < c2.getDishes().size(); k++){
                            if (c2.getDishes().get(k).isSoldOut()){
                                soldoutDishList.add(c2.getDishes().get(k));
                            }
                        }
                    }
                }
            }
        }
        if (!soldoutDishList.isEmpty()){
            soldoutDishAdapter.notifyDataSetChanged();
        }
    }

    public void buildMenu(){
        Collections.sort(category1s, new Comparator<Category1>() {
            @Override
            public int compare(Category1 category1, Category1 t1) {
                return category1.getSequence() - t1.getSequence();
            }
        });
        initialDishCellComponents();

        CategoryTabAdapter categoryTabAdapter = new CategoryTabAdapter(MainActivity.this, R.layout.categorytab_listitem_layout, category1s);
        listViewCategorys.setAdapter(categoryTabAdapter);
        listViewCategorys.post(new Runnable() {
            @Override
            public void run() {
                listViewCategorys.chooseItemByPosition(0);
            }
        });

        progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG));

        initSoldoutList();
    }

    /**
     * For reduce the time of switch different fragments, build all fragments at the start time and store
     * them in a SparseArray. While need to display one fragment, just get it from the list.
     * one category2 = one fragment
     */
    private void initialDishCellComponents(){
        int DISPLAY_DISH_COLUMN_NUMBER = 4; //菜单界面每行显示的数目/列数
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);
        int leftMargin = (screenWidth - 180 -260 - 3 * InstantValue.DISPLAY_DISH_WIDTH) / 4;
        if (leftMargin < 0){
            DISPLAY_DISH_COLUMN_NUMBER = 2; //for small screen, show 2 columns
        }
        if (leftMargin < 7)
            leftMargin = 7;
        if (category1s != null){
            TableRow.LayoutParams trlp = new TableRow.LayoutParams();
            trlp.topMargin = 7;
            trlp.leftMargin = (int)(leftMargin * displayMetrics.density);
//            trlp.width = InstantValue.DISPLAY_DISH_WIDTH;
//            trlp.height = InstantValue.DISPLAY_DISH_HEIGHT;
            Bundle bundle = new Bundle();
            ActionBar.LayoutParams ablp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            for (Category1 c1 : category1s){
                if (c1.getCategory2s() != null){
                    for (Category2 c2 : c1.getCategory2s()){
                        DishDisplayFragment frag = new DishDisplayFragment();
                        bundle.putSerializable("category2", c2);
                        frag.setArguments(bundle);

                        View view = View.inflate(this, R.layout.dishdisplay_layout, null);
                        TableLayout contentLayout = (TableLayout) view.findViewById(R.id.dishdisplay_content);
                        ScrollView sv = new ScrollView(this);
                        sv.setLayoutParams(ablp);

                        ArrayList<Dish> dishes = c2.getDishes();
                        if (dishes != null){
                            TableLayout tl = new TableLayout(this);
                            TableRow tr = null;

                            for(int i = 0; i< dishes.size(); i++){
                                Dish dish = dishes.get(i);
                                if (i % DISPLAY_DISH_COLUMN_NUMBER == 0){
                                    tr = new TableRow(this);
                                    tl.addView(tr);
                                }
                                DishCellComponent fc = new DishCellComponent(this, dish);
                                tr.addView(fc.getDishCellView(), trlp);
                                mapDishCellComponents.put(dish.getId(), fc);
                            }
                            sv.addView(tl);
                        }

                        contentLayout.addView(sv);

                        frag.setView(view);
                        mapDishDisplayFragments.put(c2.getId(), frag);
                    }
                }
            }
        }
    }

    /**
     * dish 对象发生变化时, 替换缓存的对象, 并刷新dishcell,
     * 如果变化后状态为soldout, 将dish加入soldout列表并置顶
     * 如果变化后状态为on sale, 将dish从soldout列表中移出
     * @param d
     */
    public void afterDishStatusChange(Dish d){
        for (Category1 c1 : category1s) {
            if (c1.getCategory2s() != null) {
                for (Category2 c2 : c1.getCategory2s()) {
                    if (c2.getDishes() != null){
                        for (int i = 0; i < c2.getDishes().size(); i++){
                            if (c2.getDishes().get(i).getId() == d.getId()){
                                c2.getDishes().set(i, d);
                                mapDishCellComponents.get(d.getId()).changeSoldoutStatus(d);//修改显示效果
                                //刷新列表
                                if (d.isSoldOut()){
                                    soldoutDishList.add(0, d);
                                    soldoutDishAdapter.notifyDataSetChanged();
                                } else {
                                    for (int ik = 0; ik< soldoutDishList.size(); ik++){
                                        if (soldoutDishList.get(ik).getId() == d.getId()){
                                            soldoutDishList.remove(ik);
                                            soldoutDishAdapter.notifyItemChanged(ik);
//                                            soldoutDishAdapter.notifyItemRangeChanged(ik, soldoutDishList.size());
                                            soldoutDishAdapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * dishconfig 对象发生变化时, 替换缓存的对象, 替换后不用考虑其他dish下面的DishConfig, 因为再次打开界面, 会自动根据最新的对象soldout来显示状态
     * @param dc
     */
    public void afterDishConfigStatusChange(DishConfig dc){
        for (Category1 c1 : category1s) {
            if (c1.getCategory2s() != null) {
                for (Category2 c2 : c1.getCategory2s()) {
                    if (c2.getDishes() != null){
                        for (Dish dish : c2.getDishes()){
                            if (dish.getConfigGroups() != null){
                                for (DishConfigGroup group : dish.getConfigGroups()){
                                    if (group.getDishConfigs() != null){
                                        for (int i = 0; i < group.getDishConfigs().size(); i++){
                                            if (group.getDishConfigs().get(i).getId() == dc.getId()){
                                                group.getDishConfigs().set(i, dc);//由于多个dish会共用相同的DishConfig对象, 所以这里不能break, 要循环到结尾为止
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public UserData getLoginUser() {
        return loginUser;
    }

    public Handler getProgressDlgHandler(){
        return progressDlgHandler;
    }

    public void startProgressDialog(String title, String message){
        progressDlg = ProgressDialog.show(this, title, message);
    }

    public HttpOperator getHttpOperator(){
        return httpOperator;
    }

    @Override
    public void onClick(View v) {
        if (TAG_UPLOADERRORLOG.equals(v.getTag())){
            IOOperator.onUploadErrorLog(this);
        } else if (TAG_EXITSYSTEM.equals(v.getTag())){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm")
                    .setIcon(R.drawable.info)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", null);
            builder.create().show();
        }
    }

    public SparseArray<DishDisplayFragment> getMapDishDisplayFragments() {
        return mapDishDisplayFragments;
    }

    public void setMapDishDisplayFragments(SparseArray<DishDisplayFragment> mapDishDisplayFragments) {
        this.mapDishDisplayFragments = mapDishDisplayFragments;
    }

    public SparseArray<DishCellComponent> getMapDishCellComponents() {
        return mapDishCellComponents;
    }

    public void setMapDishCellComponents(SparseArray<DishCellComponent> mapDishCellComponents) {
        this.mapDishCellComponents = mapDishCellComponents;
    }

    public void setMenu(ArrayList<Category1> category1s){
        this.category1s = category1s;
    }

    public ArrayList<Category1> getMenu(){
        return this.category1s;
    }

    //屏蔽实体按键BACK
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //屏蔽recent task 按键, some pad devices are different with the virtual device, such as Sumsung Tab E
    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext() .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    /**
     * 客户现场经常发现点菜不能加入到右侧列表的现象; 初步推测, 是原有的listener对象绑定了老的MainActivity对象.
     * 尝试一下每次resume时, 重构这些listener并将其与控件绑定
     */
    protected void onResume() {
        super.onResume();
        DishSoldListener.rebuildInstance(this);
        DishConfigSoldListener.rebuildInstance(this);
        ShowDishConfigListener.rebuildInstance(this);
        soldoutDishAdapter.resetListener();
        for (int i = 0; i < mapDishCellComponents.size(); i++) {
            int key = mapDishCellComponents.keyAt(i);
            DishCellComponent cell = mapDishCellComponents.get(key);
            cell.setListener();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        ActivityManager activityManager = (ActivityManager) getApplicationContext() .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    /**
     * stop for Sumsung's Recent Task button
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    @Override
    protected void onDestroy() {
        //because the listener instance hold this activity, must release them before the activity destroy, otherwise throw WindowManager$BadTokenException
        DishConfigSoldListener.getInstance(this).release();
        DishSoldListener.getInstance(this).release();
        ShowDishConfigListener.getInstance(this).release();
        super.onDestroy();
    }
}

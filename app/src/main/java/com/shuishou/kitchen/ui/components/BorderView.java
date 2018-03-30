package com.shuishou.kitchen.ui.components;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shuishou.kitchen.R;
import com.shuishou.kitchen.ui.MainActivity;

/**
 * Created by Administrator on 18/02/2018.
 */

public class BorderView extends RelativeLayout{
//    protected View view;
    protected TextView txtTitle;
    protected FrameLayout contentLayout;
    protected MainActivity mainActivity;
    public BorderView(MainActivity mainActivity){
        this(mainActivity, null);
        this.mainActivity = mainActivity;
    }

    public BorderView(MainActivity mainActivity, String title) {
        super(mainActivity);
        LayoutInflater.from(mainActivity).inflate(R.layout.borderview_layout, this);
        this.mainActivity = mainActivity;
        txtTitle = (TextView)findViewById(R.id.txtBorderTitle);
        contentLayout = (FrameLayout) findViewById(R.id.contentLayout);
        txtTitle.setText(title);
    }

//    public View getView() {
//        return view;
//    }

    public void setTitle(String title){
        txtTitle.setText(title);
    }

    public void setContentView(View contentView){
        contentLayout.addView(contentView);
    }
}

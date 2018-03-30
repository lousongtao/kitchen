package com.shuishou.kitchen.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

/**
 * Created by Administrator on 2017/9/12.
 */

public class DishDisplayFragment extends Fragment {
//    private Category2 category2;
    private TableLayout contentLayout;
    private View view;
    private String logTag = "TestTime-DishFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return view;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
//        category2 = (Category2) args.get("category2");
    }

    public TableLayout getContentLayout() {
        return contentLayout;
    }

    public void setContentLayout(TableLayout contentLayout) {
        this.contentLayout = contentLayout;
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getMyView(){
        return view;
    }
}

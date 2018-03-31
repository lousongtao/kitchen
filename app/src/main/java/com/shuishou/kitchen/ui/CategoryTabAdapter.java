package com.shuishou.kitchen.ui;

import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.shuishou.kitchen.bean.Category1;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/25.
 */

public class CategoryTabAdapter extends ArrayAdapter<Category1> {
    private int resourceId;
//    private ArrayList<Category1> category1List;
//    private ChangeLanguageTextView tvName;
    private MainActivity mainActivity;
    private SparseArray<CategoryTabLayoutItem> mapCategory1LayoutItem = new SparseArray<>();

    public CategoryTabAdapter(MainActivity mainActivity, int resource, ArrayList<Category1> objects){
        super(mainActivity, resource, objects);
        this.mainActivity = mainActivity;
        resourceId = resource;
//        category1List = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Category1 c1 = getItem(position);

        CategoryTabLayoutItem view = (CategoryTabLayoutItem)LayoutInflater.from(mainActivity).inflate(resourceId, null);
        view.init(mainActivity, c1);
        return view;

        //TODO: 停止使用缓存对象, 在用缓存的过程中, 发现Category1数据过多, 超出屏幕限制后, 如果进行了上下滚动, 会有部分Category1(非全部)不能打开下面的二级菜单
//        if (mapCategory1LayoutItem.get(c1.getId()) == null){
//            CategoryTabLayoutItem view = (CategoryTabLayoutItem)LayoutInflater.from(mainActivity).inflate(resourceId, null);
//            view.init(mainActivity, c1);
//            mapCategory1LayoutItem.put(c1.getId(), view);
//        }
//        return mapCategory1LayoutItem.get(c1.getId());
    }
}

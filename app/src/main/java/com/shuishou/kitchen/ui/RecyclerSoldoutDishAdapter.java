package com.shuishou.kitchen.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shuishou.kitchen.R;
import com.shuishou.kitchen.bean.Dish;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/12/25.
 */

public class RecyclerSoldoutDishAdapter extends RecyclerView.Adapter<RecyclerSoldoutDishAdapter.ViewHolder> {

    private final int resourceId;
    private final ArrayList<Dish> listSoldoutDish;
    private final MainActivity mainActivity;
    private DishSoldListener listener;
    static class ViewHolder extends RecyclerView.ViewHolder{
        final TextView tvFoodName;
        final Button button;
        public ViewHolder(View view){
            super(view);
            tvFoodName = (TextView) view.findViewById(R.id.foodNameText);
            button = (Button) view.findViewById(R.id.btn);

        }
    }

    public RecyclerSoldoutDishAdapter(MainActivity mainActivity, int resourceId, ArrayList<Dish> objects){
        listSoldoutDish = objects;
        this.resourceId = resourceId;
        this.mainActivity = mainActivity;
        listener = DishSoldListener.getInstance(mainActivity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        Dish dish = listSoldoutDish.get(position);
        holder.tvFoodName.setText(dish.getFirstLanguageName());
        holder.button.setTag(dish);
        holder.button.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return listSoldoutDish.size();
    }

}

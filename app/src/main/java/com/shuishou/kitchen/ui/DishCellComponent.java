package com.shuishou.kitchen.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shuishou.kitchen.InstantValue;
import com.shuishou.kitchen.R;
import com.shuishou.kitchen.bean.Dish;
import com.shuishou.kitchen.ui.components.DishNameTextView;

/**
 * Created by Administrator on 2016/12/31.
 */

class DishCellComponent {
    private final View foodCellView;
    private Button btnSell;
    private Button btnFlavor;
    private Dish dish;
    private MainActivity mainActivity;
    private DishSoldListener dishSoldListener;
    private ShowDishConfigListener showDishConfigListener;
    public final static String TXT_ONSALE = "On Sale";
    public final static String TXT_SOLDOUT = "SOLD OUT";
    public DishCellComponent(final MainActivity mainActivity, Dish _dish){
        this.mainActivity = mainActivity;
        this.dish = _dish;
        dishSoldListener = DishSoldListener.getInstance(mainActivity);
        showDishConfigListener = ShowDishConfigListener.getInstance(mainActivity);
        foodCellView = LayoutInflater.from(mainActivity).inflate(R.layout.dishcell_layout, null);
        LinearLayout functionLayout = (LinearLayout) foodCellView.findViewById(R.id.function_layout);
        DishNameTextView foodNameText = (DishNameTextView) foodCellView.findViewById(R.id.foodNameText);
        foodNameText.setText(dish.getFirstLanguageName());
        btnSell = (Button)foodCellView.findViewById(R.id.btn_sale);
        btnSell.setText(dish.isSoldOut() ? TXT_SOLDOUT : TXT_ONSALE);
        btnSell.setBackgroundResource(dish.isSoldOut() ? R.color.color_soldout : R.color.color_onsale);
        btnSell.setTag(dish);
        btnSell.setOnClickListener(dishSoldListener);

        if (dish.getConfigGroups() != null && !dish.getConfigGroups().isEmpty()){
            btnFlavor = new Button(mainActivity);
            btnFlavor.setText("Flavor");
            btnFlavor.setTag(dish);
            btnFlavor.setOnClickListener(showDishConfigListener);
            functionLayout.addView(btnFlavor);
        }
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish){
        this.dish = dish;
    }

    public View getDishCellView() {
        return foodCellView;
    }

    public void changeSoldoutStatus(Dish dish){
        this.dish = dish;
        btnSell.setTag(dish);
        if (btnFlavor != null)
            btnFlavor.setTag(dish);
        btnSell.setText(dish.isSoldOut() ? TXT_SOLDOUT : TXT_ONSALE);
        btnSell.setBackgroundResource(dish.isSoldOut() ? R.color.color_soldout : R.color.color_onsale);
    }
}

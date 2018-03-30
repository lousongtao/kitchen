package com.shuishou.kitchen.db;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.shuishou.kitchen.bean.Category1;
import com.shuishou.kitchen.bean.Category2;
import com.shuishou.kitchen.bean.Dish;
import com.shuishou.kitchen.ui.MainActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/9.
 */

public class DBOperator {
//    private final MainActivity mainActivity;
    private static LiteOrm liteOrm;

    public DBOperator(MainActivity mainActivity){
//        this.mainActivity = mainActivity;
        if (liteOrm == null){
            liteOrm = LiteOrm.newCascadeInstance(mainActivity, "kitchen.db");
        }
        liteOrm.setDebugged(true);
    }

    public void saveObjectByCascade(Object o){
        liteOrm.cascade().save(o);
    }

    public void saveObjectsByCascade(ArrayList objects){
        liteOrm.cascade().save(objects);
    }

    public void updateObject(Object o){
        liteOrm.update(o);
    }

    public Object queryObjectById(int id, Class c){
        return liteOrm.queryById(id, c);
    }
    public void deleteAllData(Class c){
        liteOrm.deleteAll(c);
    }
    public void deleteObject(Object o){
        liteOrm.delete(o);
    }
    public ArrayList<Category1> queryAllMenu(){
        ArrayList<Category1> c1s = liteOrm.cascade().query(Category1.class);
        return c1s;
    }

    public Dish queryDishById(int dishId){
        Dish dish = liteOrm.queryById(dishId, Dish.class);
        return dish;
    }

    public ArrayList<Dish> queryDishByParentId(int category2Id){
        return liteOrm.query(new QueryBuilder<Dish>(Dish.class).where("category2Id = " + category2Id ));
    }

    public ArrayList<Category2> queryCategory2ByParentId(int category1Id){
        return liteOrm.query(new QueryBuilder<Category2>(Category2.class).where("category1Id = " + category1Id ));
    }

    public void clearMenu(){
        liteOrm.deleteAll(Dish.class);
        liteOrm.deleteAll(Category2.class);
        liteOrm.deleteAll(Category1.class);
    }

    public LiteOrm getLiteOrm(){
        return liteOrm;
    }
}

package com.shuishou.kitchen.bean;

import com.google.gson.annotations.SerializedName;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/22.
 */
@Table("category2")
public class Category2 implements Serializable{
    @SerializedName(value = "id", alternate={"objectid"})
    @PrimaryKey(value = AssignType.BY_MYSELF)
    private int id;

    @Column("first_language_name")
    private String firstLanguageName;

    @Column("second_language_name")
    private String secondLanguageName;

    @Column("sequence")
    private int sequence;

    @SerializedName(value = "children", alternate = {"dishes"})
    @Mapping(Relation.OneToMany)
    private ArrayList<Dish> dishes;

    @Mapping(Relation.ManyToOne)
    @Column("category1_id")
    private Category1 category1;

    public Category2(){

    }

    public Category2(int id, String firstLanguageName, String secondLanguageName, int sequence, Category1 category1){
        this.id = id;
        this.firstLanguageName = firstLanguageName;
        this.secondLanguageName = secondLanguageName;
        this.sequence = sequence;
        this.category1 = category1;
    }

    public Category1 getCategory1() {
        return category1;
    }

    public void setCategory1(Category1 category1) {
        this.category1 = category1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstLanguageName() {
        return firstLanguageName;
    }

    public void setFirstLanguageName(String firstLanguageName) {
        this.firstLanguageName = firstLanguageName;
    }

    public String getSecondLanguageName() {
        return secondLanguageName;
    }

    public void setSecondLanguageName(String secondLanguageName) {
        this.secondLanguageName = secondLanguageName;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes = dishes;
    }

    public void addDish(Dish dish){
        if (dishes == null)
            dishes = new ArrayList<Dish>();
        dishes.add(dish);
    }

    @Override
    public String toString() {
        return "Category2{" +
                "firstLanguageName='" + firstLanguageName + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Category2 other = (Category2) obj;
        if (id != other.id)
            return false;
        return true;
    }
}

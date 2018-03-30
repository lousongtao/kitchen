package com.shuishou.kitchen.bean;

import com.google.gson.annotations.SerializedName;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;
import com.shuishou.kitchen.InstantValue;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/22.
 */

@Table("dish")
public class Dish implements Serializable{
    @SerializedName(value = "id", alternate={"objectid"})
    @PrimaryKey(value = AssignType.BY_MYSELF)
    private int id;

    @Column("first_language_name")
    private String firstLanguageName;

    @Column("second_language_name")
    private String secondLanguageName;

    @Column("sequence")
    private int sequence;

    @Column("category2_id")
    @Mapping(Relation.ManyToOne)
    private Category2 category2;

    @Column("price")
    private double price;

    @Column("picture_name")
    private String pictureName;

    @Column("isNew")
    private boolean isNew = false;

    @Column("isSpecial")
    private boolean isSpecial = false;

    @Column("isSoldOut")
    private boolean isSoldOut;

    @Column("hotLevel")
    private int hotLevel;

    @Column("abbreviation")
    private String abbreviation;

    @Column("choose_mode")
    private int chooseMode = InstantValue.DISH_CHOOSEMODE_DEFAULT;



    @Column("subitem_amount")
    private int subitemAmount = 0;

    @Column("automerge_whilechoose")
    private boolean autoMergeWhileChoose = true;

    @Column("purchase_type")
    private int purchaseType = InstantValue.DISH_PURCHASETYPE_UNIT;

    @Column("allowFlavor")
    private boolean allowFlavor;

    @Column("description_1stlang")
    private String description_1stlang;
    @Column("description_2ndlang")
    private String description_2ndlang;

    @Column("isPromotion")
    private boolean isPromotion = false;

    @Column("originPrice")
    private double originPrice;

    @Mapping(Relation.ManyToMany)
    private ArrayList<DishConfigGroup> configGroups = new ArrayList<>();

    public Dish(){

    }

    public ArrayList<DishConfigGroup> getConfigGroups() {
        return configGroups;
    }

    public void setConfigGroups(ArrayList<DishConfigGroup> configGroups) {
        this.configGroups = configGroups;
    }

    public boolean isPromotion() {
        return isPromotion;
    }

    public void setPromotion(boolean promotion) {
        isPromotion = promotion;
    }

    public double getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(double originPrice) {
        this.originPrice = originPrice;
    }

    public String getDescription_1stlang() {
        return description_1stlang;
    }

    public void setDescription_1stlang(String description_1stlang) {
        this.description_1stlang = description_1stlang;
    }

    public String getDescription_2ndlang() {
        return description_2ndlang;
    }

    public void setDescription_2ndlang(String description_2ndlang) {
        this.description_2ndlang = description_2ndlang;
    }

    public int getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(int purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public int getHotLevel() {
        return hotLevel;
    }

    public void setHotLevel(int hotLevel) {
        this.hotLevel = hotLevel;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean soldOut) {
        isSoldOut = soldOut;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public Category2 getCategory2() {
        return category2;
    }

    public void setCategory2(Category2 category2) {
        this.category2 = category2;
    }

    public int getChooseMode() {
        return chooseMode;
    }

    public void setChooseMode(int chooseMode) {
        this.chooseMode = chooseMode;
    }


    public int getSubitemAmount() {
        return subitemAmount;
    }

    public void setSubitemAmount(int subitemAmount) {
        this.subitemAmount = subitemAmount;
    }

    public boolean isAutoMergeWhileChoose() {
        return autoMergeWhileChoose;
    }

    public void setAutoMergeWhileChoose(boolean autoMergeWhileChoose) {
        this.autoMergeWhileChoose = autoMergeWhileChoose;
    }

    public boolean isAllowFlavor() {
        return allowFlavor;
    }

    public void setAllowFlavor(boolean allowFlavor) {
        this.allowFlavor = allowFlavor;
    }

    @Override
    public String toString() {
        return "Dish{" +
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
        Dish other = (Dish) obj;
        if (id != other.id)
            return false;
        return true;
    }
}

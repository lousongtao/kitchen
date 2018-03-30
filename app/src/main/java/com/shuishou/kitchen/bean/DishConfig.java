package com.shuishou.kitchen.bean;

import com.google.gson.annotations.SerializedName;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.io.Serializable;

@Table("dishConfig")
public class DishConfig implements Serializable{

	@SerializedName(value = "id", alternate={"objectid"})
	@PrimaryKey(value = AssignType.BY_MYSELF)
	private int id;

	@Column("first_language_name")
	private String firstLanguageName;

	@Column("second_language_name")
	private String secondLanguageName;

	@Column("sequence")
	private int sequence;

	@Column("group_id")
	@Mapping(Relation.ManyToOne)
	private DishConfigGroup group;

	@Column("price")
	private double price;

	@Column("pictureName")
	private String pictureName;

	@Column("isSoldOut")
	private boolean isSoldOut = false;
	

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


	public DishConfigGroup getGroup() {
		return group;
	}

	public void setGroup(DishConfigGroup group) {
		this.group = group;
	}

	public boolean isSoldOut() {
		return isSoldOut;
	}

	public void setSoldOut(boolean isSoldOut) {
		this.isSoldOut = isSoldOut;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
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

	@Override
	public String toString() {
		return "DishConfig [firstLanguageName=" + firstLanguageName + ", secondLanguageName=" + secondLanguageName + "]";
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
		DishConfig other = (DishConfig) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}

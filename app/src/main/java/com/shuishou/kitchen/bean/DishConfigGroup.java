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

@Table("dishConfigGroup")
public class DishConfigGroup implements Serializable{
	@SerializedName(value = "id", alternate={"objectid"})
	@PrimaryKey(value = AssignType.BY_MYSELF)
	private int id;

	@Column("first_language_name")
	private String firstLanguageName;

	@Column("second_language_name")
	private String secondLanguageName;

	@Column("sequence")
	private int sequence;
	
	/**
	 * 必须选择的数量
	 */
	@Column("requiredQuantity")
	private int requiredQuantity;

	@Mapping(Relation.OneToMany)
	private ArrayList<DishConfig> dishConfigs;

	@Column("allowDuplicate")
	private boolean allowDuplicate = false;

	@Column("uniqueName")
	private String uniqueName;
	
	
	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public boolean isAllowDuplicate() {
		return allowDuplicate;
	}

	public void setAllowDuplicate(boolean allowDuplicate) {
		this.allowDuplicate = allowDuplicate;
	}

	public ArrayList<DishConfig> getDishConfigs() {
		return dishConfigs;
	}

	public void setDishConfigs(ArrayList<DishConfig> dishConfigs) {
		this.dishConfigs = dishConfigs;
	}

	public void addDishConfig(DishConfig dc){
		if (dishConfigs == null){
			dishConfigs = new ArrayList<>();
		}
		dishConfigs.add(dc);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRequiredQuantity() {
		return requiredQuantity;
	}

	public void setRequiredQuantity(int requiredQuantity) {
		this.requiredQuantity = requiredQuantity;
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
		return "DishConfigGroup [firstLanguageName=" + firstLanguageName + ", secondLanguageName=" + secondLanguageName + "]";
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
		DishConfigGroup other = (DishConfigGroup) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}

package com.yumu.hexie.model.commonsupport.info;

import com.yumu.hexie.model.BaseModel;

import javax.persistence.Entity;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-25 20:59
 */
@Entity
public class ProductDepotTags extends BaseModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8421208961849420850L;
	
	private String name;
    private String color = "#FF9333";
    private long ownerId; //拥有者

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

	@Override
	public String toString() {
		return "ProductDepotTags [name=" + name + ", color=" + color + ", ownerId=" + ownerId + "]";
	}

}

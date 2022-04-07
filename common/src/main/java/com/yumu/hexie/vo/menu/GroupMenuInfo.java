package com.yumu.hexie.vo.menu;

import java.io.Serializable;
import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-08-19 14:36
 */
public class GroupMenuInfo implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7357576203010142457L;
	
	private String groupName;
    private List<MenuInfo> menu;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<MenuInfo> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuInfo> menu) {
        this.menu = menu;
    }

    @Override
    public String toString() {
        return "GroupMenuInfo{" +
                "groupName='" + groupName + '\'' +
                ", menu=" + menu +
                '}';
    }
}

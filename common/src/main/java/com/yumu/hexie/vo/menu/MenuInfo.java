package com.yumu.hexie.vo.menu;

import java.io.Serializable;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-08-19 14:35
 */
public class MenuInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5019103844378622807L;
	
	private String code;
    private String menuName;
    private String menuIcon;
    private String menuPage;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getMenuPage() {
        return menuPage;
    }

    public void setMenuPage(String menuPage) {
        this.menuPage = menuPage;
    }

    @Override
    public String toString() {
        return "MenuInfo{" +
                "code='" + code + '\'' +
                ", menuName='" + menuName + '\'' +
                ", menuIcon='" + menuIcon + '\'' +
                ", menuPage='" + menuPage + '\'' +
                '}';
    }
}

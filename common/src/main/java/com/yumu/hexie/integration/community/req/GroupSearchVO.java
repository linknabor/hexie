package com.yumu.hexie.integration.community.req;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-12 14:02
 */
public class GroupSearchVO {
    private String queryName;
    private String groupStatus;

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public String getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(String groupStatus) {
        this.groupStatus = groupStatus;
    }

    @Override
    public String toString() {
        return "GroupSearchVO{" +
                "queryName='" + queryName + '\'' +
                ", groupStatus='" + groupStatus + '\'' +
                '}';
    }
}

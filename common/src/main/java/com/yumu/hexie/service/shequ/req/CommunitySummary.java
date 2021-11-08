package com.yumu.hexie.service.shequ.req;

import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-11-08 14:24
 */
public class CommunitySummary {
    private String dateCycle;

    private List<SectsVO.SectVO> sects;

    public String getDateCycle() {
        return dateCycle;
    }

    public void setDateCycle(String dateCycle) {
        this.dateCycle = dateCycle;
    }

    public List<SectsVO.SectVO> getSects() {
        return sects;
    }

    public void setSects(List<SectsVO.SectVO> sects) {
        this.sects = sects;
    }

    @Override
    public String toString() {
        return "CommunitySummary{" +
                "dateCycle='" + dateCycle + '\'' +
                ", sects=" + sects +
                '}';
    }
}

package com.yumu.hexie.service.shequ.req;

import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-11-05 16:21
 */
public class RatioSum {
    private String sect_id;
    private String sect_name;
    private List<RatioDetail> detail;

    public String getSect_id() {
        return sect_id;
    }

    public void setSect_id(String sect_id) {
        this.sect_id = sect_id;
    }

    public String getSect_name() {
        return sect_name;
    }

    public void setSect_name(String sect_name) {
        this.sect_name = sect_name;
    }

    public List<RatioDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<RatioDetail> detail) {
        this.detail = detail;
    }
}

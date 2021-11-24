package com.yumu.hexie.service.shequ.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-11-17 14:14
 */
public class Ratio implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 6684212562540030727L;

    @JsonProperty("sect_id")
    private String sectId;

    @JsonProperty("sect_name")
    private String sectName;

    @JsonProperty("ratio")
    private String ratio;

    public String getSectId() {
        return sectId;
    }

    public void setSectId(String sectId) {
        this.sectId = sectId;
    }

    public String getSectName() {
        return sectName;
    }

    public void setSectName(String sectName) {
        this.sectName = sectName;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }
}

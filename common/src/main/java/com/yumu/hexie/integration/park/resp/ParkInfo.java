package com.yumu.hexie.integration.park.resp;

import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-08-18 18:14
 */
public class ParkInfo {
    private String park_id;
    private String park_name;
    private String park_addr;
    private List<RuleInfo> ruleList;

    public static class RuleInfo {
        private String ruleName;

        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        @Override
        public String toString() {
            return "RuleInfo{" +
                    "ruleName='" + ruleName + '\'' +
                    '}';
        }
    }

    public String getPark_id() {
        return park_id;
    }

    public void setPark_id(String park_id) {
        this.park_id = park_id;
    }

    public String getPark_name() {
        return park_name;
    }

    public void setPark_name(String park_name) {
        this.park_name = park_name;
    }

    public String getPark_addr() {
        return park_addr;
    }

    public void setPark_addr(String park_addr) {
        this.park_addr = park_addr;
    }

    public List<RuleInfo> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<RuleInfo> ruleList) {
        this.ruleList = ruleList;
    }
}

package com.yumu.hexie.service.shequ.req;

import java.io.Serializable;
import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-04-26 16:12
 */
public class SectsVO implements Serializable {

    private List<SectVO> sects;
    private String appid;

    public List<SectVO> getSects() {
        return sects;
    }

    public void setSects(List<SectVO> sects) {
        this.sects = sects;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }
    public static class SectVO {
        private String id; //小区ID
        private String name; //小区名称

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "sectVO{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }


}

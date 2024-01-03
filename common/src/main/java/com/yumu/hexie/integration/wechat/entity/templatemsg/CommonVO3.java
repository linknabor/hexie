package com.yumu.hexie.integration.wechat.entity.templatemsg;

import java.io.Serializable;

/**
 * @Package : Staff
 * @Author :
 * @Date : 2023 7月 星期四
 * @Desc :
 */
public class CommonVO3 implements Serializable {
    private TemplateItem thing2;
    private TemplateItem thing3;
    private TemplateItem thing6;
    private TemplateItem thing9;
    private TemplateItem time4;
    private TemplateItem time5;

    public TemplateItem getThing2() {
        return thing2;
    }

    public void setThing2(TemplateItem thing2) {
        this.thing2 = thing2;
    }

    public TemplateItem getThing3() {
        return thing3;
    }

    public void setThing3(TemplateItem thing3) {
        this.thing3 = thing3;
    }

    public TemplateItem getThing6() {
        return thing6;
    }

    public void setThing6(TemplateItem thing6) {
        this.thing6 = thing6;
    }

    public TemplateItem getThing9() {
        return thing9;
    }

    public void setThing9(TemplateItem thing9) {
        this.thing9 = thing9;
    }

    public TemplateItem getTime4() {
        return time4;
    }

    public void setTime4(TemplateItem time4) {
        this.time4 = time4;
    }

    public TemplateItem getTime5() {
        return time5;
    }

    public void setTime5(TemplateItem time5) {
        this.time5 = time5;
    }

    @Override
    public String toString() {
        return "CommonVO3{" +
                "thing2=" + thing2 +
                ", thing3=" + thing3 +
                ", thing6=" + thing6 +
                ", thing9=" + thing9 +
                ", time4=" + time4 +
                ", time5=" + time5 +
                '}';
    }
}

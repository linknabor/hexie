package com.yumu.hexie.integration.renovation.resp;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2024 12月 星期五
 * @Desc :
 */
public class RenovationInfoResp {
    private String register_id;
    private String register_status;
    private String register_status_cn;
    private String create_date;
    private String start_date;
    private String end_date;
    private String attachment_urls;
    private String cell_addr;
    private String cust_name;
    private String tel_no;
    private String content;

    public String getRegister_id() {
        return register_id;
    }

    public void setRegister_id(String register_id) {
        this.register_id = register_id;
    }

    public String getRegister_status() {
        return register_status;
    }

    public void setRegister_status(String register_status) {
        this.register_status = register_status;
    }

    public String getRegister_status_cn() {
        return register_status_cn;
    }

    public void setRegister_status_cn(String register_status_cn) {
        this.register_status_cn = register_status_cn;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getAttachment_urls() {
        return attachment_urls;
    }

    public void setAttachment_urls(String attachment_urls) {
        this.attachment_urls = attachment_urls;
    }

    public String getCell_addr() {
        return cell_addr;
    }

    public void setCell_addr(String cell_addr) {
        this.cell_addr = cell_addr;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getTel_no() {
        return tel_no;
    }

    public void setTel_no(String tel_no) {
        this.tel_no = tel_no;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RenovationInfoResp{" +
                "register_id='" + register_id + '\'' +
                ", register_status='" + register_status + '\'' +
                ", register_status_cn='" + register_status_cn + '\'' +
                ", create_date='" + create_date + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", attachment_urls='" + attachment_urls + '\'' +
                ", cell_addr='" + cell_addr + '\'' +
                ", cust_name='" + cust_name + '\'' +
                ", tel_no='" + tel_no + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

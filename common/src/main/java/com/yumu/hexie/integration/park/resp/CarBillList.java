package com.yumu.hexie.integration.park.resp;

import java.util.List;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2024 1月 星期一
 * @Desc :
 */
public class CarBillList {

    private List<CarBillInfo> bills;
    private String permit_skip_car_pay;
    private String reduce_mode;

    static class CarBillInfo {
        private String bill_id;
        private String service_fee_name;
        private String pay_cell_addr;
        private String service_fee_cycle;
        private String fee_price;

        public String getBill_id() {
            return bill_id;
        }

        public void setBill_id(String bill_id) {
            this.bill_id = bill_id;
        }

        public String getService_fee_name() {
            return service_fee_name;
        }

        public void setService_fee_name(String service_fee_name) {
            this.service_fee_name = service_fee_name;
        }

        public String getPay_cell_addr() {
            return pay_cell_addr;
        }

        public void setPay_cell_addr(String pay_cell_addr) {
            this.pay_cell_addr = pay_cell_addr;
        }

        public String getService_fee_cycle() {
            return service_fee_cycle;
        }

        public void setService_fee_cycle(String service_fee_cycle) {
            this.service_fee_cycle = service_fee_cycle;
        }

        public String getFee_price() {
            return fee_price;
        }

        public void setFee_price(String fee_price) {
            this.fee_price = fee_price;
        }

        @Override
        public String toString() {
            return "CarBillInfo{" +
                    "bill_id='" + bill_id + '\'' +
                    ", service_fee_name='" + service_fee_name + '\'' +
                    ", pay_cell_addr='" + pay_cell_addr + '\'' +
                    ", service_fee_cycle='" + service_fee_cycle + '\'' +
                    ", fee_price='" + fee_price + '\'' +
                    '}';
        }
    }

    public List<CarBillInfo> getBills() {
        return bills;
    }

    public void setBills(List<CarBillInfo> bills) {
        this.bills = bills;
    }

    public String getPermit_skip_car_pay() {
        return permit_skip_car_pay;
    }

    public void setPermit_skip_car_pay(String permit_skip_car_pay) {
        this.permit_skip_car_pay = permit_skip_car_pay;
    }

    public String getReduce_mode() {
        return reduce_mode;
    }

    public void setReduce_mode(String reduce_mode) {
        this.reduce_mode = reduce_mode;
    }

    @Override
    public String toString() {
        return "CarBillList{" +
                "bills=" + bills +
                ", permit_skip_car_pay='" + permit_skip_car_pay + '\'' +
                ", reduce_mode='" + reduce_mode + '\'' +
                '}';
    }
}

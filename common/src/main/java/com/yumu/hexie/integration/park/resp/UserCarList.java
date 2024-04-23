package com.yumu.hexie.integration.park.resp;

import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-08-18 17:48
 */
public class UserCarList {

    private List<CarInfo> carList;
    private ParkInfo parkInfo;

    public List<CarInfo> getCarList() {
        return carList;
    }

    public void setCarList(List<CarInfo> carList) {
        this.carList = carList;
    }

    public ParkInfo getParkInfo() {
        return parkInfo;
    }

    public void setParkInfo(ParkInfo parkInfo) {
        this.parkInfo = parkInfo;
    }

    public static class CarInfo {
        private String car_no;
        private String is_default;

        public String getCar_no() {
            return car_no;
        }

        public void setCar_no(String car_no) {
            this.car_no = car_no;
        }

        public String getIs_default() {
            return is_default;
        }

        public void setIs_default(String is_default) {
            this.is_default = is_default;
        }

        @Override
        public String toString() {
            return "CarInfo{" +
                    "car_no='" + car_no + '\'' +
                    ", is_default='" + is_default + '\'' +
                    '}';
        }
    }
}

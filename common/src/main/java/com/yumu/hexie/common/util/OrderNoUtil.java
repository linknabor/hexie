package com.yumu.hexie.common.util;

import java.util.Date;
import java.util.Random;

public class OrderNoUtil {

	public static String generateServiceOrderNo() {
		return DateUtil.dtFormat(new Date(), "yyyyMMddHHmm") +"S" + (int)(1000+(Math.random()*9000)) + System.currentTimeMillis() % 10;
	}

	public static String generateGroupNo() {
		return DateUtil.dtFormat(new Date(), "yyyyMMddHHmm") +"G" + (int)(1000+(Math.random()*9000)) + System.currentTimeMillis() % 10;
	}
	public static String generateYuyueOrderNo() {
		return DateUtil.dtFormat(new Date(), "yyyyMMddHHmm") +"Y" + (int)(1000+(Math.random()*9000)) + System.currentTimeMillis() % 10;
	}


    public static String generateO2OOrderNo() {
        return DateUtil.dtFormat(new Date(), "yyyyMMddHHmm") +"O" + (int)(1000+(Math.random()*9000)) + System.currentTimeMillis() % 10;
    }
	public static String generatePaymentOrderNo() {
		return DateUtil.dtFormat(new Date(), "yyyyMMddHHmm") +"P" + (int)(1000+(Math.random()*9000)) + System.currentTimeMillis() % 10;
	}

	public static String generateRefundOrderNo() {
		return DateUtil.dtFormat(new Date(), "yyyyMMddHHmm") +"R" + (int)(1000+(Math.random()*9000)) + System.currentTimeMillis() % 10;
	}
	

    public static String generateRepairOrderNo() {
        return DateUtil.dtFormat(new Date(), "yyyyMMddHHmm") +"W" + (int)(1000+(Math.random()*9000)) + System.currentTimeMillis() % 10;
    }
    

    public static String generateSettleOrderNo() {
        return DateUtil.dtFormat(new Date(), "yyyyMMddHHmm") +"T" + (int)(1000+(Math.random()*9000)) + System.currentTimeMillis() % 1;
    }
    
    public static String generateEvoucherNo() {
        return DateUtil.dtFormat(new Date(), "yyyyMMddHHmm") + (int)(1000+(Math.random()*9000)) + System.currentTimeMillis() % 10;
    }
    
    public static String generateServiceNo() {
        return DateUtil.dtFormat(new Date(), "yyyyMMddHHmm") + (int)(1000+(Math.random()*9000));
    }
    
    /**
	 * 自定义订单编号
	 * @return
	 */
	public static String getOrderNum(){
		StringBuffer str=new StringBuffer();
		str.append(DateUtil.dtFormat(new Date(), "yyyyMMddHHmm"));
		str.append(getRandomStr());
		return str.toString();
	}
	/**
	 * 生成6位随机数
	 * @return
	 */
	public static String getRandomStr() {

		Random r = new Random();
		long i = r.nextInt(100000);
		long number = i + 900000L;
		return Long.toString(number);
	}
	
	public static void main(String[] args) {
		
		String ordnm = getOrderNum();
		System.out.println(ordnm.length());
	}
}

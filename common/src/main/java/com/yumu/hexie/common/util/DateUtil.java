package com.yumu.hexie.common.util;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtil {
    public static final String dSimple = "yyyy-MM-dd";
    public static final String dttmSimple = "yyyy-MM-dd HH:mm:ss";
    public static final String dateSimple = "EEE MMM dd HH:mm:ss z yyyy";

    public static Date addDate(Date current, int addedDays) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(current);
        calendar.add(Calendar.DATE, addedDays);//把日期往后增加一天.整数往后推,负数往前移动
        return calendar.getTime();
    }
    
    public static String getSendTime(long time) {
        Calendar cal = Calendar.getInstance();
        long l = cal.getTimeInMillis() - time;
        long month = l/ (24 * 60 * 60 * 1000) /30;
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

        StringBuffer sb = new StringBuffer();
        if (month > 0) {
        	sb.append(month + "个月");
		} else if (day > 0) {
            sb.append(day + "天");
		} else if (hour > 0) {
            sb.append(hour + "小时");
        } else if (min > 0) {
            sb.append(min + "分");
            sb.append(s + "秒");
        } else if (min == 0) {
            sb.append(s + "秒");
        }

        sb.append("前");
        return sb.toString();

    }


    public static Date parse(String timeText, String pattern) {
        if (timeText == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            if (dateSimple.equals(pattern)) {
                sdf = new SimpleDateFormat(pattern, Locale.US);
            }
            return sdf.parse(timeText);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * yyyy-MM-dd HH:mm
     *
     * @param date
     * @return
     */
    public static final String dttmFormat(Date date) {
        if (date == null) {
            return "";
        }

        return getFormat(dttmSimple).format(date);
    }

    public static final String dtFormat(long date, String format) {
        return dtFormat(new Date(date), format);
    }

    /**
     * @param date
     * @return
     */
    public static final String dtFormat(Date date, String format) {
        if (date == null) {
            return "";
        }

        return getFormat(format).format(date);
    }

    /**
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static final String dtFormat(Date date) {
        if (date == null) {
            return "";
        }

        return getFormat(dSimple).format(date);
    }

    /**
     * 优先格式化成yyyy-MM-dd形式的Date，如果不传时分秒，则取当天零点
     * description: yyyy-MM-dd 或者 yyyy-MM-dd hh:mm:ss
     *
     * @author rongnian.lu gfwu321@163.com
     * date： 2015-7-28
     * param:@param dateString
     * param:@return
     * return：Date
     */
    public static final Date getDateFromString(String dateString) {
        Date date = null;
        try {
            date = new SimpleDateFormat(dSimple).parse(dateString);
        } catch (Exception e) {
            try {
                date = new SimpleDateFormat(dttmSimple).parse(dateString);
            } catch (Exception e2) {
                date = null;
            }
        }

        return date;
    }

    /**
     * 优先格式化成yyyy-MM-dd HH:mm:ss形式的Date，如果不传时分秒，则取当天零点
     * description: yyyy-MM-dd 或者 yyyy-MM-dd hh:mm:ss
     *
     * @author rongnian.lu gfwu321@163.com
     * date： 2015-7-28
     * param:@param dateString
     * param:@return
     * return：Date
     */
    public static final Date getDateTimeFromString(String dateString) {
        Date date = null;
        try {
            date = new SimpleDateFormat(dttmSimple).parse(dateString);
        } catch (Exception e) {
            date = null;
        }
        return date;
    }

    /**
     * description: yyyy-MM-dd 或者 yyyy-MM-dd hh:mm:ss
     *
     * @author rongnian.lu gfwu321@163.com
     * date： 2015-7-28
     * param:@param dateString
     * param:@return Date yyyy-MM-dd
     * return：Date
     */
    public static final java.sql.Date getSqlDateFromString(String dateString) {

        return new java.sql.Date(getDateFromString(dateString).getTime());
    }


    /**
     * description: yyyy-MM-dd 或者 yyyy-MM-dd hh:mm:ss
     *
     * @author rongnian.lu gfwu321@163.com
     * date： 2015-7-28
     * param:@param dateString
     * param:@return Timestamp yyyy-MM-dd hh:mm:ss
     * return：Date
     */
    public static final java.sql.Timestamp getSqlTimestampFromString(String dateString) {
        return new java.sql.Timestamp(getDateFromString(dateString).getTime());
    }

    private static final DateFormat getFormat(String format) {
        return new SimpleDateFormat(format);
    }

    public static String convertL2DFeed(long l) {
        long between = (System.currentTimeMillis() - l) / 1000;// 除以1000是为了转换成秒

        long year = between / (24 * 3600 * 30 * 12);
        long month = between / (24 * 3600 * 30);
        long day = between / (24 * 3600);
        long hour = between % (24 * 3600) / 3600;
        long minute = between % 3600 / 60;
        long second = between % 60 / 60;

        StringBuffer sb = new StringBuffer();
        if (year != 0) {
            sb.append(year + "年");
            return sb.toString() + "前";
        }
        if (month != 0) {
            sb.append(month + "个月");
            return sb.toString() + "前";
        }
        /*
         * if (week != 0) { sb.append(week + "周"); return sb.toString() + "前"; }
         */
        if (day != 0) {
            sb.append(day + "天");
            return sb.toString() + "前";
        }
        if (hour != 0) {
            sb.append(hour + "小时");
            return sb.toString() + "前";
        }
        if (minute != 0) {
            sb.append(minute + "分钟");
            return sb.toString() + "前";
        }
        if (second != 0) {
            sb.append(second + "秒");
            return sb.toString() + "前";
        }

        return "刚刚";
    }

    public static int getDurationDays(long startTime, long endTime) {
        Date startDay = parse(dtFormat(new Date(startTime)), dSimple);
        Date endDay = parse(dtFormat(new Date(endTime)), dSimple);
        return (int) Math.round((endDay.getTime() - startDay.getTime()) / (3600000 * 24d));
    }

    /**********************数字转换，暂时先放这个类**********************/
    private static final String[] UNITS = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百",
            "千"};

    private static final String[] NUMS = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

    /**
     * 数字转换成中文汉字
     *
     * @param value 转换的数字
     * @return 返回数字转后的汉字字符串
     */
    public static String translate(int value) {
        if (value <= 0) {
            return "未出生";
        }
        String result = "";
        int year = value / 12;
        int month = value % 12;
        if (year > 0) {
            result = getHanValue(year) + "岁";
        }
        if (month > 0) {
            result = result + getHanValue(month) + "月";
        }
        return result;
    }

    private static String getHanValue(int value) {
        String result = "";

        for (int i = String.valueOf(value).length() - 1; i >= 0; i--) {
            int r = (int) (value / Math.pow(10, i));
            result += NUMS[r % 10] + UNITS[i];
        }

        result = result.replaceAll("零[十, 百, 千]", "零");
        result = result.replaceAll("零+", "零");
        result = result.replaceAll("零([万, 亿])", "$1");
        result = result.replaceAll("亿万", "亿"); //亿万位拼接时发生的特殊情况 
        if (result.startsWith("一十"))
            result = result.substring(1);
        if (result.endsWith("零"))
            result = result.substring(0, result.length() - 1);
        return result;

    }

    /**
     * 把yyyyMMdd格式日期字符串格式化成YYYY-MM-DD格式,把kkmmss格式时间字符串格式化成kk:mm:ss格式,中间加空格后拼接
     *
     * @param date yyyyMMdd格式日期字符串
     * @param time kkmmss格式时间字符串
     * @return
     */
    public static String formatDateTimeFromDB(String date, String time) {
        if (StringUtils.isEmpty(date) || date.length() < 8)
            date = "        ";
        if (StringUtils.isEmpty(time) || time.length() < 6)
            time = "      ";
        StringBuilder buf = new StringBuilder(date);
        buf.insert(6, '-').insert(4, '-');
        StringBuilder buf1 = new StringBuilder(time);
        buf1.insert(2, ':').insert(5, ':');
        return buf.toString() + " " + buf1.toString();
    }

    /**
     * yyyyMMdd HHmmss 转换成 yyyy-MM-dd HH:mm:ss
     *
     * @param createDate yyyyMMdd
     * @param createTime HHmmss
     * @return
     */
    public static String formatFromDB(String createDate, String createTime) {

        String dateStr = "";
        if (!StringUtils.isEmpty(createDate) && !StringUtils.isEmpty(createTime)) {
            Date date = DateUtil.parse(createDate + " " + createTime, "yyyyMMdd HHmmss");
            dateStr = DateUtil.dtFormat(date, DateUtil.dttmSimple);
        }
        return dateStr;
    }

    /**
     * 得到输入日期+i天以后的日期
     * @param s yyyyMMdd格式日期
     * @param i 可以是负数
     * @return
     */
    public static String getNextDateByNum(String s, int i) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
        java.util.Date date = simpledateformat.parse(s, new ParsePosition(0));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, i);
        date = calendar.getTime();
        s = simpledateformat.format(date);
        return s;
    }

    /**
     * 获取某个日期所在周的周一的日期
     * @param dataStr
     * @return
     */
    public static String getFirstOfWeek(String dataStr) {
        String firstDate = "";
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyyMMdd").parse(dataStr));
            int d;
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                d = -6;
            } else {
                d = 2 - cal.get(Calendar.DAY_OF_WEEK);
            }
            cal.add(Calendar.DAY_OF_WEEK, d);
            // 所在周开始日期
            firstDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return firstDate;
    }
    
    /**
     * 获取两个日期之间的差值
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int getDateDiff(String beginDate, String endDate) {
    	
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // 解析日期字符串为 LocalDate 对象
        LocalDate date1 = LocalDate.parse(beginDate, formatter);
        LocalDate date2 = LocalDate.parse(endDate, formatter);
        
        // 计算日期之间的差值
        Long daysDiff = ChronoUnit.DAYS.between(date1, date2);
        return daysDiff.intValue();
    }
    
    public static void main(String[] args) {
		
    	int diff = getDateDiff("20240701", "20240703");
    	System.out.println(diff);
	}

}

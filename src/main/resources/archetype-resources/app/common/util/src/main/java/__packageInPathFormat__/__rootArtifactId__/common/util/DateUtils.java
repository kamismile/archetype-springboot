#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期工具类
 * <p/>
 * <p/>
 * Created by haiyang.song on 15/10/19.
 */
public class DateUtils {
    //日期格式
    public static final String DATE_SIMPLE_FORMAT_STRING = "yyyyMMdd";
    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";

    //时间格式
    public static final String DATETIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_SIMPLE_FORMAT_STRING = "yyyyMMddHHmmss";

    /**
     * 格式日期yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String parseDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_STRING);
        return df.format(date);
    }

    /**
     * 格式化简单日期yyyyMMdd
     *
     * @param date
     * @return
     */
    public static String parseSimleDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat(DATE_SIMPLE_FORMAT_STRING);
        return df.format(date);
    }

    /**
     * 格式化时间yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String parseDateTime(Date date) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT_STRING);
        return df.format(date);
    }

    /**
     * 格式化时间yyyyMMddHHMMss
     *
     * @param date
     * @return
     */
    public static String parseSimpleDateTime(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat(DATETIME_SIMPLE_FORMAT_STRING);
        return df.format(date);
    }

    /**
     * 将yyyy-MM-dd字符串格式化成时间
     *
     * @param dt
     * @return
     */
    public static Date toDate(String dt) {
        if (dt == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING);
        try {
            return format.parse(dt);
        } catch (Exception e) {
            throw new RuntimeException("格式化时间错误");
        }
    }

    /**
     * 将yyyyMMddHHmmss字符串格式化成时间
     *
     * @param dt
     * @return
     */
    public static Date toSimpleDate(String dt) {
        if (dt == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT_STRING);
        try {
            return format.parse(dt);
        } catch (Exception e) {
            throw new RuntimeException("格式化时间错误");
        }
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss 字符串格式化成时间
     *
     * @param dt
     * @return
     */
    public static Date toDateTime(String dt) {
        if (dt == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT_STRING);
        try {
            return format.parse(dt);
        } catch (Exception e) {
            throw new RuntimeException("格式化时间错误");
        }
    }

    /**
     * 将yyyyMMddHHmmss字符串格式化成时间
     *
     * @param dt
     * @return
     */
    public static Date toSimpleDateTime(String dt) {
        if (dt == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(DATETIME_SIMPLE_FORMAT_STRING);
        try {
            return format.parse(dt);
        } catch (Exception e) {
            throw new RuntimeException("格式化时间错误");
        }
    }

    /**
     * 获取当天的00:00:00
     *
     * @return
     */
    public static Date getTodayStart() {
        return getDateStart(new Date());
    }

    /**
     * 获取当天的23:59:59
     *
     * @return
     */
    public static Date getTodayEnd() {
        return getDateStart(new Date());
    }

    /**
     * 获取指定日期的00:00:00
     *
     * @param date
     * @return
     */
    public static Date getDateStart(Date date) {
        Calendar currentDate = new GregorianCalendar();
        currentDate.setTime(date);

        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        return (Date) currentDate.getTime().clone();
    }

    /**
     * 获取指定日期的23:59:59
     *
     * @return
     */
    public static Date getDateEnd(Date date) {
        Calendar currentDate = new GregorianCalendar();
        currentDate.setTime(date);

        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        return (Date) currentDate.getTime().clone();

    }

    /**
     * 日期计算增加天数
     *
     * @param beginDate
     * @param days
     * @return
     */
    public static Date addDay(Date beginDate, int days) {
        Date date = beginDate;
        if (days != 0) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, days);
                date = calendar.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 日期计算增加月数
     *
     * @param beginDate
     * @param months
     * @return
     */
    public static Date addMonth(Date beginDate, int months) {
        Date date = beginDate;
        if (months != 0) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.MONTH, months);
                date = calendar.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 日期计算增加秒数
     *
     * @param beginDate
     * @param seconds
     * @return
     */
    public static Date addSecond(Date beginDate, int seconds) {
        Date date = beginDate;
        if (seconds != 0) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.SECOND, seconds);
                date = calendar.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return date;
    }
}

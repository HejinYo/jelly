package cn.hejinyo.jelly.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 日期处理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年12月21日 下午12:53:33
 */
public class DateUtils {
	/** 时间格式(yyyy-MM-dd) */
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	/** 时间格式(yyyy-MM-dd HH:mm:ss) */
	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    public static String format(Date date, String pattern) {
        if(date != null){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }
    
    /**
	 * @function:如果前者早于后者则返回true
	 * @param beforeTime
	 * @param afterTime
	 * @return
	 * @author: llh 2008-7-12 下午01:19:07
	 */
	public static boolean compareTime(Calendar beforeTime, Calendar afterTime)
	{
		boolean flag = false;
		if (beforeTime.before(afterTime))
		{
			flag = true;
		}
		return flag;
	}




	/**
	 * @function:将字符串转换为日期
	 * @param date
	 * @param format
	 * @return
	 * @author: lilh Jan 17, 2008 4:28:04 PM
	 */
	public static Date getDateByStr(String date, String format)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date d = new Date();
		try
		{
			d = dateFormat.parse(date);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * @function: 返回当前时间，格式为“yyyy-mm-dd”
	 * @return
	 * @author: lilh 2009-2-24 上午09:50:36
	 */
	public static String getSimpleFormatedDayNow()
	{
		Date now = new Date();
		return getFormatedDayStr(now);
	}

	/**
	 * 返回当前时间，格式为“yyyy-MM-dd HH:mm:ss”
	 *
	 * @author <a href="mailto:linhui.li@berheley.com">linhui.li </a>
	 * @return String
	 */
	public static String getSimpleFormatedDateNow()
	{
		Date now = new Date();
		return getFormatedDateStr(now);
	}
	
	/**
	 * @param now
	 * @return 返回格式为 'yyyy-MM-dd' 的字符串
	 */
	public static String getFormatedDayStr(Date now)
	{
		DateFormat df = new SimpleDateFormat(DATE_PATTERN);
		String str = (now == null ? null : df.format(now));
		return str;
	}

	/**
	 * 通过时间、日期差得到修正的时间
	 * 
	 * @author <a href="mailto:songran.li@berheley.com">songran.li </a>
	 * @param today Date 时间
	 * @param interval int 日期差
	 * @return Date 修正时间
	 */
	public static Date getInterval(Date today, int interval)
	{
		long time = today.getTime();
		time += interval * 24 * 3600 * 1000;
		return new Date(time);
	}

	/**
	 * @param now
	 * @return 返回格式为 'yyyy-MM-dd HH:mm:ss' 的字符串
	 */
	public static String getFormatedDateStr(Date now)
	{
		DateFormat df = new SimpleDateFormat(DATE_TIME_PATTERN);
		String str = (now == null ? null : df.format(now));
		return str;
	}
	
}

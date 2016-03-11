package zjoy.web.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateUtil {
	
	//锁对象
	private static final Object lockObj = new Object();
	//存放不同模版的map
	private static Map<String,ThreadLocal<SimpleDateFormat>> sfMap = new HashMap<String,ThreadLocal<SimpleDateFormat>>();
	
	private static SimpleDateFormat getSimpleDateFormat(final String pattern){
		ThreadLocal<SimpleDateFormat> tl = sfMap.get(pattern);
		if (tl == null) {
            synchronized (lockObj) {
                tl = sfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sfMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
	}
	
	
	public static Date theDate = new Date(0);
	public static String originDateFormat = "yyyy-MM-dd HH:mm:ss";
	public static String formaterWithOutTime = "yyyy/MM/dd";
	public static String formaterWithTime = "yyyy/MM/dd hh:mm";
	
	public static String getFormaterString(Date date,String formater){
		if(date==null){
			return null;
		}
		SimpleDateFormat sf = getSimpleDateFormat(formater);
		String str = sf.format(date);
		return str;
	}
	
	
	public static String getFormaterStringFromStr(String dateStr,String formater,String toDateFormat){
		if(dateStr==null){
			return null;
		}else{
			Date date = null;
			try {
				date = getSimpleDateFormat(toDateFormat).parse(dateStr);
			} catch (ParseException e) {
				return null;
			}
			return getFormaterString(date,formater);
		}
	}
	
	/**
	 * @author zhouyang
	 * @createDate 2015年7月24日
	 * @description 判断是不是同一天
	 * @param date
	 * @param dateStr
	 * @param formater
	 * @return
	 */
	public static boolean isSameDay(Date date,String dateStr,String formater){
		SimpleDateFormat sf = getSimpleDateFormat(formater);
		try {
			Date focusDate = sf.parse(dateStr);
			Calendar now = Calendar.getInstance();
			Calendar focusTime = Calendar.getInstance();
			now.setTime(date);
			focusTime.setTime(focusDate);
			if(now.get(Calendar.YEAR)==focusTime.get(Calendar.YEAR)){
				if(now.get(Calendar.MONTH)==focusTime.get(Calendar.MONTH)){
					if(now.get(Calendar.DAY_OF_MONTH) == focusTime.get(Calendar.DAY_OF_MONTH)){
						return true;
					}
					return false;
				}
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 判断和传入的日期是否已经过去了24小时
	 * @param dateStr
	 * @param formater
	 * @return
	 * @throws ParseException 
	 */
	public static boolean isOverTime(Date date,String dateStr,String formater){
		SimpleDateFormat sf = getSimpleDateFormat(formater);
		if(isSameDay(date,dateStr,formater)){
			return false;
		}else{
			Date focusDate = null;
			try {
				focusDate = sf.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar now = Calendar.getInstance();
			Calendar focusTime = Calendar.getInstance();
			now.setTime(date);
			focusTime.setTime(focusDate);
			focusTime.add(Calendar.DAY_OF_MONTH, +1);
			if(now.after(focusTime)){
				return true;
			}else{
				return false;
			}
		}
	}
	
	/**
	 * 获得前几天
	 * @param date
	 * @param number
	 * @return
	 * @throws ParseException
	 */
	public static String getBeforeDay(String date,int number) throws ParseException{
		SimpleDateFormat sf = null;
		if(date.indexOf("/")!=-1){
			sf = getSimpleDateFormat("yyyy/MM/dd");
		}else{
			sf = getSimpleDateFormat("yyyy-MM-dd");
		}
		Date day = sf.parse(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(day);
		calendar.add(Calendar.DAY_OF_MONTH, -number);
		Date d = calendar.getTime();
		return sf.format(d);
	}
	
	/**
	 * 获得前一天
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getBeforeDay(String date) throws ParseException{
		SimpleDateFormat sf = null;
		if(date.indexOf("/")!=-1){
			sf = getSimpleDateFormat("yyyy/MM/dd");
		}else{
			sf = getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(sf.parse(date));
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date dt = cal.getTime();
		String endDate = getSimpleDateFormat("yyyy/MM/dd").format(dt);
		return endDate;
	}
	
	/**
	 * 返回一天的开始时间
	 * @param date
	 * @return
	 */
	public static Date getFirstOfTheDay(Date date){
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date result = calendar.getTime();
		return result;
	}
	
	/**
	 * 返回传入日期字符串的当天最小时间字符串
	 * @author zhouyang
	 * @createDate 2016年1月12日
	 * @description
	 */
	public static Date getFirstOfTheDay(String date,String pattern) throws ParseException{
		SimpleDateFormat sf = getSimpleDateFormat(pattern);
		Date dt = sf.parse(date);
		return getFirstOfTheDay(dt);
	}
	
	
	/**
	 * 返回一天的结束时间
	 * @param date
	 * @return
	 */
	public static Date getEndOfTheDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date result = calendar.getTime();
		return result;
	}
	
	/**
	 * 返回传入日期字符串的当天最大时间字符串
	 * @author zhouyang
	 * @createDate 2016年1月12日
	 * @description
	 */
	public static Date getEndOfTheDay(String date,String pattern) throws ParseException{
		SimpleDateFormat sf = getSimpleDateFormat(pattern);
		Date dt = sf.parse(date);
		return getEndOfTheDay(dt);
	}
	
	/**
	 * 获取给定日期距现在多少年 
	 */
	public static int getYearsBetweenNow(String pattern,String time){
		SimpleDateFormat sf = getSimpleDateFormat(pattern);
		Date now = new Date();
		Date before = null;
		try {
			before = sf.parse(time);
		} catch (ParseException e) {
			return -1;
		}
		Calendar calendarNow = Calendar.getInstance();
		Calendar calendarBefore = Calendar.getInstance();
		calendarNow.setTime(now);
		calendarBefore.setTime(before);
		int year = calendarNow.get(Calendar.YEAR)-calendarBefore.get(Calendar.YEAR);
		if(year==0){
			return 0;
		}if(year==1){
			int month = calendarNow.get(Calendar.MONTH)-calendarBefore.get(Calendar.MONTH);
			if(month>=0){
				return 1;
			}else{
				return 0;
			}
		}
		return year;
	}
	
	/**
	 * @author zhouyang
	 * @createDate 2015年7月24日
	 * @description 把字符串按照给定的格式转化为Date类型
	 * @param timeStr  日期字符串
	 * @param formater 日期格式
	 * @return
	 */
	public static Date StringToDateWithFormater(String timeStr,String formater){
		SimpleDateFormat sf = getSimpleDateFormat(formater);
		Date date = null;
		try {
			date = sf.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * @author zhouyang
	 * @createDate 2015年9月21日
	 * @description 把其他类型的时间字符串 变为 yyyy/MM/dd
	 */
	public static String changeNormalDateStringToXieDateString(String timeStr){
		String result = "";
		SimpleDateFormat sf1 = getSimpleDateFormat(formaterWithOutTime);
		SimpleDateFormat sf = null;
		if(timeStr!=null&&!"".equals(timeStr)){
			if(timeStr.contains("年")){
				sf = getSimpleDateFormat("yyyy年MM月dd日");
			}else{
				if(timeStr.length()>12){
					if(timeStr.contains("-")){
						sf = getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					}
				}else{
					if(timeStr.contains("-")){
						if(RegExpValidator.isContaimLetter(timeStr)){
							sf = new SimpleDateFormat("dd-MMM-yy",Locale.US);
						}else{
							sf = getSimpleDateFormat("yyyy-MM-dd");
						}
					}else{
						return timeStr;
					}
				}
			}
			try {
				Date date = sf.parse(timeStr);
				result = sf1.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/*
	 * 更新的时间为：今天/昨天/两天前/三天前/四天前/五天前/六天前/一周前/两周前/一个月前
	 */
	public static String getCountStringUpdateTimeBetweenNow(Date updateTime){
		Date now = new Date();
		int day = daysBetween(updateTime, now);
		//如果传入时间为一特定的时间，直接返回暂无更新数据
		if(updateTime.equals(theDate)){
			return "暂无";
		}else{
			if(day==0){
				return "今天";
			}else if(day==1){
				return "1天前";
			}else if(day==2){
				return "2天前";
			}else if(day ==3){
				return "3天前";
			/*}else if(day ==4){
				return "4天前";
			}else if(day ==5){
				return "5天前";
			}else if(day ==6){
				return "6天前";*/
			}else if(4 <= day && day <=7){
				return "一周前";
			}if(8 <=day && day <= 14 ){
				return "两周前";
			}else /*if(14 <= day && day <31){*/
				return "一月前";
			/*}else 
				return "暂无";*/
		}
	}
	
	/**   
     * 计算两个日期之间相差的天数   
     * @param before   
     * @param after   
     * @return   
     */    
    public static int daysBetween(Date before,Date after)     
    {
        String beforeStr = DateUtil.getFormaterString(before, "yyyy-MM-dd");
        Date beforeDate = DateUtil.StringToDateWithFormater(beforeStr,"yyyy-MM-dd");
        
        String afterStr = DateUtil.getFormaterString(after, "yyyy-MM-dd");
        Date afterDate = DateUtil.StringToDateWithFormater(afterStr,"yyyy-MM-dd");
        
        long between_days = (afterDate.getTime() - beforeDate.getTime())/(1000*3600*24);
        return Integer.parseInt(String.valueOf(between_days));            
    }    
	
	/**
	 * 判断是否超过有效期
	 * @author zhouyang
	 * @createDate 2015年9月29日
	 * @description 计算时间是否过期 value 过期时间  CalendarField 时间单位
	 * 返回true 表示没有过期
	 */
	public static boolean isOverPeriod(Date now ,Date createTime,int CalendarField, int value){
		Calendar nowCalendar = Calendar.getInstance();
		Calendar beforeCalendar = Calendar.getInstance();
		nowCalendar.setTime(now);
		beforeCalendar.setTime(createTime);
		beforeCalendar.add(CalendarField, value);
		return nowCalendar.before(beforeCalendar);
	}
	
	/**
	 * 获取今天开始时间
	 * @return
	 */
	public static Date startOfTodDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date date=calendar.getTime();
		return new Date(date.getTime());
	}
}

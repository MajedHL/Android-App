package fr.utt.if26.tasksorganizer.Utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    private static String getFormattedDate(Date date){
        if(date==null) return"" ;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }
    private static String[] getYearMonthDay(String formattedDate){
        String [] ymdhms = formattedDate.split(" ");
        String [] ymd = ymdhms[0].split("-");
        return ymd;
    }
    private static String getHourMinute(String formattedDate){
        String [] ymdhms = formattedDate.split(" ");
        return ymdhms[1];
    }

    public static boolean sameDay(Date d1, Date d2){
        String d1_str = getFormattedDate(d1);
        String d2_str = getFormattedDate(d2);
        String [] ymd1 = getYearMonthDay(d1_str);
        String [] ymd2 = getYearMonthDay(d2_str);
        System.out.println("d1:"+ Arrays.toString(ymd1));
        System.out.println("d2:"+ Arrays.toString(ymd2));
        if(ymd1[0].equals(ymd2[0]) && ymd1[1].equals(ymd2[1]) && ymd1[2].equals(ymd2[2])) return true;

        return false;
    }


    public static String getAngDateFormat(int year, int month, int day){
        StringBuilder sb= new StringBuilder();
        sb.append(day);
        sb.append("/"+month);
        sb.append("/"+year);
        return sb.toString();
    }



    public static String getYearMonthDayFromDate(Date date){
       String[] ymd= getYearMonthDay(getFormattedDate(date));
       StringBuilder sb = new StringBuilder();
       sb.append(ymd[2]);
       sb.append("/"+ymd[1]);
       sb.append("/"+ymd[0]);
       return sb.toString();
    }

    public static String getHourMinuteFromDate(Date date){
         return getHourMinute(getFormattedDate(date));
    }

    public static Date computeDate(int year, int month, int day,int hour, int minute){
        if(year>0){
            if(hour>-1){
               return new GregorianCalendar(year,month,day,hour,minute).getTime();
            }
            else return new GregorianCalendar(year,month,day,23,55).getTime();
        }
        else return null;
    }

    public static int[] getYearMothDayValues(Date date){
        String[] ymd_str= getYearMonthDay(getFormattedDate(date));
        int [] ymd = {Integer.parseInt(ymd_str[0]), Integer.parseInt(ymd_str[1])-1, Integer.parseInt(ymd_str[2])};
    return ymd;
    }

    public static int[] getHourMinuteValues(Date date){
       String[] hm_str= getHourMinute(getFormattedDate(date)).split(":");
       int[] hm = {Integer.parseInt(hm_str[0]), Integer.parseInt(hm_str[1])};
       return hm;
    }
}

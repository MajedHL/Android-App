package fr.utt.if26.tasksorganizer.Utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class DateUtil {

    private static String getFormattedDate(Date date){
        if(date==null) return"" ;
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
    }
    private static String[] getYearMonthDay(String formattedDate){
        String [] ymdhms = formattedDate.split(" ");
        String [] ymd = ymdhms[0].split("-");
        return ymd;
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
}

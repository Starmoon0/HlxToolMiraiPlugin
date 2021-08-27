package com.starmoon.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

// 每日定点时间执行
public class TimerUtils
{
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
    public static Timer startTimer(TimerTask task, int hour, int minute, int second)
    {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        
        Date date=calendar.getTime();
        if(date.before(new Date())){
            date=addDay(date, 1);
        }
        
        Timer timer=new Timer();
        timer.schedule(task, date, PERIOD_DAY);
        return timer;
    }
    public static Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }
}

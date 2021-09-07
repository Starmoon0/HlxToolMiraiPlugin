package com.starmoon.util;

import java.util.Calendar;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import java.util.HashMap;


// 每日定点时间执行
public class TimerUtils {
    public static HashMap<String, a> mTimers=new HashMap<>();
    public static a startTimer(Runnable run, String name, int hour, int minute, int second) {
        a a=new a(run, hour, minute, second);
        a a1 = mTimers.getOrDefault(name, a);
        if (a1 != a || !a.isRunning()) {
            mTimers.put(name, a);
            new Thread(a).start();
        }
        return a;
    }
    public static class a implements Runnable {
        private boolean isRun;
        private int date;
        private final int hour;
        private final int minute;
        private final int second;

        private final Runnable runnable;
        private boolean isRunnable=true;

        a(Runnable run, int h, int m, int s) {
            date = -1;
            runnable = run;
            hour = h;
            minute = m;
            second = s;
        }


        @Override
        public void run() {
            while (isRunnable) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {}
                Calendar ca=Calendar.getInstance();
                if (isRun) {
                    if (date == ca.get(DAY_OF_MONTH)) {
                        if (second != ca.get(SECOND)) {
                            isRun = false;
                        }
                        continue;
                    }
                } else if (date != ca.get(DAY_OF_MONTH) && second == ca.get(SECOND) && minute == ca.get(MINUTE) && hour == ca.get(HOUR_OF_DAY)) {
                    isRun = true;
                    date = ca.get(DAY_OF_MONTH);
                    runnable.run();
                }
            }
        }
        public void cancel() {
            isRunnable = false;
        }
        public boolean isRunning() {
            return isRunnable;
        }
    }
}

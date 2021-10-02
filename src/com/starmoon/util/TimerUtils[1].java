package com.starmoon.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import net.mamoe.mirai.console.MiraiConsole;
import net.mamoe.mirai.utils.MiraiLogger;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import java.util.Date;


// 每日定点时间执行
public class TimerUtils {
    static MiraiLogger logger = MiraiConsole.INSTANCE.getMainLogger();

//    public static HashMap<String, a> mTimers=new HashMap<>();
    public static TimerUtils.a startTimer(Runnable run, int hour, int minute, int second) {
        a a=new a(run, hour, minute, second);
		Executors.newSingleThreadExecutor().execute(a);
        return a;
    }
    public static class a implements Runnable {
        private boolean isRun;
        private int date;
        private final int hour;
        private final int minute;
        private final int second;

        private final Runnable runnable;
        private boolean isRunnable;
		private long nextExecuteMillis;

        a(Runnable run, int h, int m, int s) {
            date = -1;
            runnable = run;
            hour = h;
            minute = m;
            second = s;
        }


		@Override
		public void run() {
			isRunnable=true;
			Date date=addDay();
			nextExecuteMillis=date.getTime();
			logger.info("执行时间："+new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(date));			
			while (isRunnable) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {}
				Calendar cl=Calendar.getInstance();
				if(System.currentTimeMillis()>=nextExecuteMillis&&!isRun)
				{
					logger.info("正在执行时间："+new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(cl.getTime()));
					Executors.newSingleThreadExecutor().execute(runnable);
					isRun=true;
				}
				if(isRun)
				{
					Date date2=addDay();
					nextExecuteMillis=date2.getTime();
					logger.info("下一次执行时间："+new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(date2));
					isRun=false;
				}
			}
		}
		public Date addDay()
		{
			Calendar cl2=Calendar.getInstance();
			cl2.add(DAY_OF_MONTH, 1);
			cl2.set(HOUR_OF_DAY, hour);
			cl2.set(MINUTE, minute);
			cl2.set(SECOND, second);
			cl2.set(Calendar.MILLISECOND, 0);
			return cl2.getTime();
		}
        public void cancel() {
            isRunnable = false;
			nextExecuteMillis=0;
			date=0;
			isRun=false;
        }
        public boolean isRunning() {
            return isRunnable;
        }
    }
}

package com.navercorp.rabbit.util.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Configuration
public class TaskScheduler extends ThreadPoolTaskScheduler {

    private Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    private static final long serialVersionUID = 1L;

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        if (period <= 0) {
            return null;
        }
        ScheduledFuture<?> future = super.scheduleAtFixedRate(task, period);
        return future;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        if (period <= 0) {
            return null;
        }
        ScheduledFuture<?> future = super.scheduleAtFixedRate(task, startTime, period);
        return future;
    }


    public void register(Runnable runnable, String scheduleId, int period) {
        ScheduledFuture<?> task = scheduleAtFixedRate(runnable, period);
        scheduledTasks.put(scheduleId, task);
    }

    public void remove(String scheduleId) {
        log.info(scheduleId + "를 종료합니다.");
        ScheduledFuture<?> scheduledFuture=scheduledTasks.get(scheduleId);
        if(scheduledFuture!=null) {
            scheduledFuture.cancel(true);
        }
    }

    public boolean isScheduling(String scheduleId){
        ScheduledFuture<?> scheduledFuture=scheduledTasks.get(scheduleId);
        if(scheduledFuture==null) return false;
        else {
            if (scheduledFuture.isCancelled()) return false;
            else return true;
        }
    }
}

package util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {

    private static final ExecutorService normalServiceExecutor = Executors.newCachedThreadPool(runnable -> {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setDaemon(true);
        return thread;
    });

    private static final ScheduledExecutorService timerServiceExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setDaemon(true);
        return thread;
    });

    public static void startThread(Runnable runnable) {
        normalServiceExecutor.execute(runnable);
    }

    public static void startDelayedThread(Runnable runnable, long milliseconds) {
        timerServiceExecutor.schedule(runnable, milliseconds, TimeUnit.MILLISECONDS);
    }



}

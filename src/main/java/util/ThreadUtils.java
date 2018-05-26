package util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {

    private static final ExecutorService normalServiceExecutor = Executors.newCachedThreadPool(runnable -> {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setDaemon(true);
        return thread;
    });

    public static void startThread(Runnable runnable) {
        normalServiceExecutor.execute(runnable);
    }

}

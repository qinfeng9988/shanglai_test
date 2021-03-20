package com.qjfcc.test20210308.common;

import java.text.ParseException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: qinjiangfeng
 * @Date: 2021/3/5 15:02
 * @Description:
 */
public class ThreadPoolUtil {
    private static final ThreadPoolExecutor threadPoolExecutor =
            new ThreadPoolExecutor(100, 300, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(300));


    public static <T> void execute(ThreadConsumer consumer) {
        threadPoolExecutor.execute(consumer::execute);
    }

    public interface ThreadConsumer {
        void execute();
    }
}

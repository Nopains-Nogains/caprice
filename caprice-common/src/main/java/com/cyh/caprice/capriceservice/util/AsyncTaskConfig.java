package com.cyh.caprice.capriceservice.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @Author: cuiyuhao
 * @Description: 多线程异步处理缓存队列
 * @Data: Created in 8:55 2021/10/13
 **/
@Configuration
@EnableAsync
public class AsyncTaskConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        //设置核心线程数
        threadPool.setCorePoolSize(10);
        //设置最大线程数
        threadPool.setMaxPoolSize(40);
        //线程池所使用的缓冲队列
        threadPool.setQueueCapacity(10);
        //等待任务在关机时完成--表明等待所有线程执行完
        threadPool.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间 （默认为0，此时立即停止），并没等待xx秒后强制停止
        threadPool.setAwaitTerminationSeconds(60);
        //  线程名称前缀
        threadPool.setThreadNamePrefix("Derry-Async");
        // 初始化线程
        threadPool.initialize();
        return threadPool;
    }

}

package cn.gtmap.ferry.config.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ShiYou
 * @date 2023年10月10日 9:02
 * Description: 驱动与连接线程池
 */
@Slf4j
@Configuration
public class InstanceThreadPool {

    @Bean(name = "InstanceThreadPool")
    public ThreadPoolExecutor createResourceCheckThreadPool() {
        return new ThreadPoolExecutor(
                5,
                50,
                1,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(30000),
                new CustomThreadFactory(),
                new CustomRejectedExecutionHandler());
    }

    private static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(@Nullable Runnable r) {
            Thread t = new Thread(r);
            String threadName = "InstanceThreadPool-" + count.addAndGet(1);
            t.setName(threadName);
            return t;
        }
    }

    private static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 报警处理等
            log.error("### 实例任务已满，请尽快检查！当前队列任务数量为：{} ###", executor.getQueue().size());
            try {
                //由blockingqueue的offer改成put阻塞方法
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

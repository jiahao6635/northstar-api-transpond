package com.northstar.northstarapitranspond.config;

import java.util.concurrent.Executor;

/**
 * @author 李嘉豪
 * @date 2024/11/11 上午12:17
 * @version 1.0
 */
public class VirtualThreadExecutor implements Executor {
    private final Executor executor;

    public VirtualThreadExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        // 使用虚拟线程池来执行任务
        executor.execute(() -> {
            try {
                command.run();
            } catch (Throwable e) {
                // 异常处理
                e.printStackTrace();
            }
        });
    }
}

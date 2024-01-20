package fuled.boot.example.dynamic.threadpool.config;

import com.fxz.fuled.dynamic.threadpool.ThreadPoolRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPoolConfig {

    @PostConstruct
    public void init() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()
                , Runtime.getRuntime().availableProcessors()
                , 0
                , TimeUnit.SECONDS
                , new ArrayBlockingQueue<>(1024));
        ThreadPoolRegistry.registerThreadPool("test", threadPoolExecutor);

        /**
         * 如下方式为打开线程池内ThreadLocal无感传递，如果不需要全部传递，也可以使用RpcContext.get() ,无论是否开启threadLocalSupport
         * RpcContext都可以在线程池内传递，但仅限于被ThreadPoolRegistry增强的线程池，默认线程池不支持
         */
//        ThreadPoolRegistry.registerThreadPool("test", threadPoolExecutor, new DefaultThreadExecuteHook() {
//            @Override
//            public boolean threadLocalSupport() {
//                return Boolean.TRUE;
//            }
//        });
    }
}

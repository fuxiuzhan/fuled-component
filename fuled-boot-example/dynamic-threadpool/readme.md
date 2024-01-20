在nacos上修改fuled.dynamic.threadpool.config.test.core-size=80 配置参数效果如下
```html
2024-01-20 00:47:57.590  INFO 67739 --- [           main] c.f.f.d.t.w.ThreadPoolExecutorWrapper    : update threadPool name->test oldCoreSize->8,currentCoreSize->80
2024-01-20 00:47:57.959  INFO 67739 --- [)-192.168.10.38] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2024-01-20 00:47:57.959  INFO 67739 --- [)-192.168.10.38] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2024-01-20 00:47:57.965  INFO 67739 --- [)-192.168.10.38] o.s.web.servlet.DispatcherServlet        : Completed initialization in 6 ms
INFO: Sentinel log output type is: file
INFO: Sentinel log charset is: utf-8
INFO: Sentinel log base directory is: /Users/fxz/logs/csp/
INFO: Sentinel log name use pid is: false
2024-01-20 00:48:09.414  INFO 67739 --- [10.201_8848-DEV] c.f.f.c.s.nacos.listener.NacosListener   : config changes ->ConfigChangeItem{key='fuled.dynamic.threadpool.config.test.core-size', oldValue='80', newValue='90', type=MODIFIED}
2024-01-20 00:48:09.416  INFO 67739 --- [10.201_8848-DEV] c.f.f.c.s.nacos.listener.NacosListener   : config changes key->fuled.dynamic.threadpool.config.test.core-size, newValue->90,oldValue->80
2024-01-20 00:48:09.425  INFO 67739 --- [10.201_8848-DEV] c.f.f.d.t.w.ThreadPoolExecutorWrapper    : update threadPool name->test oldCoreSize->80,currentCoreSize->90
2024-01-20 00:48:09.428  INFO 67739 --- [10.201_8848-DEV] c.f.f.e.e.s.ApplicationEventListener     : eventType->org.springframework.cloud.context.environment.EnvironmentChangeEvent
2024-01-20 00:48:09.503  INFO 67739 --- [10.201_8848-DEV] c.f.f.e.e.s.ApplicationEventListener     : eventType->org.springframework.cloud.context.environment.EnvironmentChangeEvent
2024-01-20 00:48:15.204  INFO 67739 --- [10.201_8848-DEV] c.f.f.c.s.nacos.listener.NacosListener   : config changes ->ConfigChangeItem{key='fuled.dynamic.threadpool.config.test.core-size', oldValue='90', newValue='100', type=MODIFIED}
2024-01-20 00:48:15.204  INFO 67739 --- [10.201_8848-DEV] c.f.f.c.s.nacos.listener.NacosListener   : config changes key->fuled.dynamic.threadpool.config.test.core-size, newValue->100,oldValue->90
2024-01-20 00:48:15.211  INFO 67739 --- [10.201_8848-DEV] c.f.f.d.t.w.ThreadPoolExecutorWrapper    : update threadPool name->test oldCoreSize->90,currentCoreSize->100
2024-01-20 00:48:15.211  INFO 67739 --- [10.201_8848-DEV] c.f.f.e.e.s.ApplicationEventListener     : eventType->org.springframework.cloud.context.environment.EnvironmentChangeEvent
2024-01-20 00:48:15.293  INFO 67739 --- [10.201_8848-DEV] c.f.f.e.e.s.ApplicationEventListener     : eventType->org.springframework.cloud.context.environment.EnvironmentChangeEvent
2024-01-20 00:48:22.109  INFO 67739 --- [10.201_8848-DEV] c.f.f.c.s.nacos.listener.NacosListener   : config changes ->ConfigChangeItem{key='fuled.dynamic.threadpool.config.test.core-size', oldValue='100', newValue='80', type=MODIFIED}
2024-01-20 00:48:22.109  INFO 67739 --- [10.201_8848-DEV] c.f.f.c.s.nacos.listener.NacosListener   : config changes key->fuled.dynamic.threadpool.config.test.core-size, newValue->80,oldValue->100
2024-01-20 00:48:22.117  INFO 67739 --- [10.201_8848-DEV] c.f.f.d.t.w.ThreadPoolExecutorWrapper    : update threadPool name->test oldCoreSize->100,currentCoreSize->80
2024-01-20 00:48:22.117  INFO 67739 --- [10.201_8848-DEV] c.f.f.e.e.s.ApplicationEventListener     : eventType->org.springframework.cloud.context.environment.EnvironmentChangeEvent
2024-01-20 00:48:22.199  INFO 67739 --- [10.201_8848-DEV] c.f.f.e.e.s.ApplicationEventListener     : eventType->org.springframework.cloud.context.environment.EnvironmentChangeEvent

```

prometheus 指标

需要配合prometheus-repoter使用，具体指标的含义参见 PrometheusReporter 实现
```html
       <dependency>
            <groupId>com.fxz.component.plugin.dynamic.threadpool.reporter</groupId>
            <artifactId>reporter-prometheus</artifactId>
            <version>1.0.0.WaterDrop</version>
        </dependency>
```
```html
# HELP fuled_dynamic_thread_pool_exec_count ThreadPoolExecCount
20# TYPE fuled_dynamic_thread_pool_exec_count gauge
29fuled_dynamic_thread_pool_exec_count{queue_type="java.util.concurrent.ArrayBlockingQueue",reject_handler_type="java.util.concurrent.ThreadPoolExecutor$AbortPolicy",thread_pool_app_name="fuled-example-dynamic-threadpool",thread_pool_ipv4="192.168.10.38",thread_pool_ipv6="0:0:0:0:0:0:0:1%lo0,2408:820c:8f3c:361:7d16:b506:2e5a:c6bb%en0,2408:820c:8f3c:361:864:a4bd:465:4ab7%en0,fe80:0:0:0:0:0:0:1%lo0,fe80:0:0:0:1ae0:cf19:8747:686b%utun2,fe80:0:0:0:25b0:65f5:9d5d:6151%utun1,fe80:0:0:0:2ce4:41ff:feee:b9f1%ap1,fe80:0:0:0:74ae:10ff:fea1:ed3%awdl0,fe80:0:0:0:74ae:10ff:fea1:ed3%llw0,fe80:0:0:0:c26:a7f7:ee0e:f334%en0,fe80:0:0:0:c794:8082:78b:e931%utun0,fe80:0:0:0:ce81:b1c:bd2c:69e%utun3",thread_pool_name="test",type="java.util.concurrent.ThreadPoolExecutor",} 0.0
9    0     0  1368k      0 --:-# HELP fuled_dynamic_thread_pool_max_queue_size ThreadPoolMaxQueueSize
-# TYPE fuled_dynamic_thread_pool_max_queue_size gauge
fuled_dynamic_thread_pool_max_queue_size{queue_type="java.util.concurrent.ArrayBlockingQueue",reject_handler_type="java.util.concurrent.ThreadPoolExecutor$AbortPolicy",thread_pool_app_name="fuled-example-dynamic-threadpool",thread_pool_ipv4="192.168.10.38",thread_pool_ipv6="0:0:0:0:0:0:0:1%lo0,2408:820c:8f3c:361:7d16:b506:2e5a:c6bb%en0,2408:820c:8f3c:361:864:a4bd:465:4ab7%en0,fe80:0:0:0:0:0:0:1%lo0,fe80:0:0:0:1ae0:cf19:8747:686b%utun2,fe80:0:0:0:25b0:65f5:9d5d:6151%utun1,fe80:0:0:0:2ce4:41ff:feee:b9f1%ap1,fe80:0:0:0:74ae:10ff:fea1:ed3%awdl0,fe80:0:0:0:74ae:10ff:fea1:ed3%llw0,fe80:0:0:0:c26:a7f7:ee0e:f334%en0,fe80:0:0:0:c794:8082:78b:e931%utun0,fe80:0:0:0:ce81:b1c:bd2c:69e%utun3",thread_pool_name="test",type="java.util.concurrent.ThreadPoolExecutor",} 1024.0
:-# HELP fuled_dynamic_thread_pool_task_count ThreadPoolTaskCount
-# TYPE fuled_dynamic_thread_pool_task_count gauge
fuled_dynamic_thread_pool_task_count{queue_type="java.util.concurrent.ArrayBlockingQueue",reject_handler_type="java.util.concurrent.ThreadPoolExecutor$AbortPolicy",thread_pool_app_name="fuled-example-dynamic-threadpool",thread_pool_ipv4="192.168.10.38",thread_pool_ipv6="0:0:0:0:0:0:0:1%lo0,2408:820c:8f3c:361:7d16:b506:2e5a:c6bb%en0,2408:820c:8f3c:361:864:a4bd:465:4ab7%en0,fe80:0:0:0:0:0:0:1%lo0,fe80:0:0:0:1ae0:cf19:8747:686b%utun2,fe80:0:0:0:25b0:65f5:9d5d:6151%utun1,fe80:0:0:0:2ce4:41ff:feee:b9f1%ap1,fe80:0:0:0:74ae:10ff:fea1:ed3%awdl0,fe80:0:0:0:74ae:10ff:fea1:ed3%llw0,fe80:0:0:0:c26:a7f7:ee0e:f334%en0,fe80:0:0:0:c794:8082:78b:e931%utun0,fe80:0:0:0:ce81:b1c:bd2c:69e%utun3",thread_pool_name="test",type="java.util.concurrent.ThreadPoolExecutor",} 0.0
 -# HELP fuled_dynamic_thread_pool_thread_pool_timestamp Last Execute TimeStamp
-# TYPE fuled_dynamic_thread_pool_thread_pool_timestamp gauge
:--:fuled_dynamic_thread_pool_thread_pool_timestamp{queue_type="java.util.concurrent.ArrayBlockingQueue",reject_handler_type="java.util.concurrent.ThreadPoolExecutor$AbortPolicy",thread_pool_app_name="fuled-example-dynamic-threadpool",thread_pool_ipv4="192.168.10.38",thread_pool_ipv6="0:0:0:0:0:0:0:1%lo0,2408:820c:8f3c:361:7d16:b506:2e5a:c6bb%en0,2408:820c:8f3c:361:864:a4bd:465:4ab7%en0,fe80:0:0:0:0:0:0:1%lo0,fe80:0:0:0:1ae0:cf19:8747:686b%utun2,fe80:0:0:0:25b0:65f5:9d5d:6151%utun1,fe80:0:0:0:2ce4:41ff:feee:b9f1%ap1,fe80:0:0:0:74ae:10ff:fea1:ed3%awdl0,fe80:0:0:0:74ae:10ff:fea1:ed3%llw0,fe80:0:0:0:c26:a7f7:ee0e:f334%en0,fe80:0:0:0:c794:8082:78b:e931%utun0,fe80:0:0:0:ce81:b1c:bd2c:69e%utun3",thread_pool_name="test",type="java.util.concurrent.ThreadPoolExecutor",} 1.705736254361E12
-- --:--:-- 1# HELP fuled_dynamic_thread_pool_queue_capacity ThreadPoolQueueCapacity
415k# TYPE fuled_dynamic_thread_pool_queue_capacity gauge

fuled_dynamic_thread_pool_queue_capacity{queue_type="java.util.concurrent.ArrayBlockingQueue",reject_handler_type="java.util.concurrent.ThreadPoolExecutor$AbortPolicy",thread_pool_app_name="fuled-example-dynamic-threadpool",thread_pool_ipv4="192.168.10.38",thread_pool_ipv6="0:0:0:0:0:0:0:1%lo0,2408:820c:8f3c:361:7d16:b506:2e5a:c6bb%en0,2408:820c:8f3c:361:864:a4bd:465:4ab7%en0,fe80:0:0:0:0:0:0:1%lo0,fe80:0:0:0:1ae0:cf19:8747:686b%utun2,fe80:0:0:0:25b0:65f5:9d5d:6151%utun1,fe80:0:0:0:2ce4:41ff:feee:b9f1%ap1,fe80:0:0:0:74ae:10ff:fea1:ed3%awdl0,fe80:0:0:0:74ae:10ff:fea1:ed3%llw0,fe80:0:0:0:c26:a7f7:ee0e:f334%en0,fe80:0:0:0:c794:8082:78b:e931%utun0,fe80:0:0:0:ce81:b1c:bd2c:69e%utun3",thread_pool_name="test",type="java.util.concurrent.ThreadPoolExecutor",} 1024.0
# HELP fuled_dynamic_thread_pool_current_core_size ThreadPoolCoreSize
# TYPE fuled_dynamic_thread_pool_current_core_size gauge
fuled_dynamic_thread_pool_current_core_size{queue_type="java.util.concurrent.ArrayBlockingQueue",reject_handler_type="java.util.concurrent.ThreadPoolExecutor$AbortPolicy",thread_pool_app_name="fuled-example-dynamic-threadpool",thread_pool_ipv4="192.168.10.38",thread_pool_ipv6="0:0:0:0:0:0:0:1%lo0,2408:820c:8f3c:361:7d16:b506:2e5a:c6bb%en0,2408:820c:8f3c:361:864:a4bd:465:4ab7%en0,fe80:0:0:0:0:0:0:1%lo0,fe80:0:0:0:1ae0:cf19:8747:686b%utun2,fe80:0:0:0:25b0:65f5:9d5d:6151%utun1,fe80:0:0:0:2ce4:41ff:feee:b9f1%ap1,fe80:0:0:0:74ae:10ff:fea1:ed3%awdl0,fe80:0:0:0:74ae:10ff:fea1:ed3%llw0,fe80:0:0:0:c26:a7f7:ee0e:f334%en0,fe80:0:0:0:c794:8082:78b:e931%utun0,fe80:0:0:0:ce81:b1c:bd2c:69e%utun3",thread_pool_name="test",type="java.util.concurrent.ThreadPoolExecutor",} 0.0
# HELP fuled_dynamic_thread_pool_core_size ThreadPoolCoreSize
# TYPE fuled_dynamic_thread_pool_core_size gauge
fuled_dynamic_thread_pool_core_size{queue_type="java.util.concurrent.ArrayBlockingQueue",reject_handler_type="java.util.concurrent.ThreadPoolExecutor$AbortPolicy",thread_pool_app_name="fuled-example-dynamic-threadpool",thread_pool_ipv4="192.168.10.38",thread_pool_ipv6="0:0:0:0:0:0:0:1%lo0,2408:820c:8f3c:361:7d16:b506:2e5a:c6bb%en0,2408:820c:8f3c:361:864:a4bd:465:4ab7%en0,fe80:0:0:0:0:0:0:1%lo0,fe80:0:0:0:1ae0:cf19:8747:686b%utun2,fe80:0:0:0:25b0:65f5:9d5d:6151%utun1,fe80:0:0:0:2ce4:41ff:feee:b9f1%ap1,fe80:0:0:0:74ae:10ff:fea1:ed3%awdl0,fe80:0:0:0:74ae:10ff:fea1:ed3%llw0,fe80:0:0:0:c26:a7f7:ee0e:f334%en0,fe80:0:0:0:c794:8082:78b:e931%utun0,fe80:0:0:0:ce81:b1c:bd2c:69e%utun3",thread_pool_name="test",type="java.util.concurrent.ThreadPoolExecutor",} 80.0
# HELP fuled_dynamic_thread_pool_max_core_size ThreadPoolMaxCoreSize
# TYPE fuled_dynamic_thread_pool_max_core_size gauge
fuled_dynamic_thread_pool_max_core_size{queue_type="java.util.concurrent.ArrayBlockingQueue",reject_handler_type="java.util.concurrent.ThreadPoolExecutor$AbortPolicy",thread_pool_app_name="fuled-example-dynamic-threadpool",thread_pool_ipv4="192.168.10.38",thread_pool_ipv6="0:0:0:0:0:0:0:1%lo0,2408:820c:8f3c:361:7d16:b506:2e5a:c6bb%en0,2408:820c:8f3c:361:864:a4bd:465:4ab7%en0,fe80:0:0:0:0:0:0:1%lo0,fe80:0:0:0:1ae0:cf19:8747:686b%utun2,fe80:0:0:0:25b0:65f5:9d5d:6151%utun1,fe80:0:0:0:2ce4:41ff:feee:b9f1%ap1,fe80:0:0:0:74ae:10ff:fea1:ed3%awdl0,fe80:0:0:0:74ae:10ff:fea1:ed3%llw0,fe80:0:0:0:c26:a7f7:ee0e:f334%en0,fe80:0:0:0:c794:8082:78b:e931%utun0,fe80:0:0:0:ce81:b1c:bd2c:69e%utun3",thread_pool_name="test",type="java.util.concurrent.ThreadPoolExecutor",} 80.0
# HELP fuled_dynamic_thread_pool_largest_pool_size ThreadLargestPoolSize
# TYPE fuled_dynamic_thread_pool_largest_pool_size gauge
fuled_dynamic_thread_pool_largest_pool_size{queue_type="java.util.concurrent.ArrayBlockingQueue",reject_handler_type="java.util.concurrent.ThreadPoolExecutor$AbortPolicy",thread_pool_app_name="fuled-example-dynamic-threadpool",thread_pool_ipv4="192.168.10.38",thread_pool_ipv6="0:0:0:0:0:0:0:1%lo0,2408:820c:8f3c:361:7d16:b506:2e5a:c6bb%en0,2408:820c:8f3c:361:864:a4bd:465:4ab7%en0,fe80:0:0:0:0:0:0:1%lo0,fe80:0:0:0:1ae0:cf19:8747:686b%utun2,fe80:0:0:0:25b0:65f5:9d5d:6151%utun1,fe80:0:0:0:2ce4:41ff:feee:b9f1%ap1,fe80:0:0:0:74ae:10ff:fea1:ed3%awdl0,fe80:0:0:0:74ae:10ff:fea1:ed3%llw0,fe80:0:0:0:c26:a7f7:ee0e:f334%en0,fe80:0:0:0:c794:8082:78b:e931%utun0,fe80:0:0:0:ce81:b1c:bd2c:69e%utun3",thread_pool_name="test",type="java.util.concurrent.ThreadPoolExecutor",} 0.0
# HELP fuled_dynamic_thread_pool_current_queue_size ThreadPoolCurrentQueueSize
# TYPE fuled_dynamic_thread_pool_current_queue_size gauge
fuled_dynamic_thread_pool_current_queue_size{queue_type="java.util.concurrent.ArrayBlockingQueue",reject_handler_type="java.util.concurrent.ThreadPoolExecutor$AbortPolicy",thread_pool_app_name="fuled-example-dynamic-threadpool",thread_pool_ipv4="192.168.10.38",thread_pool_ipv6="0:0:0:0:0:0:0:1%lo0,2408:820c:8f3c:361:7d16:b506:2e5a:c6bb%en0,2408:820c:8f3c:361:864:a4bd:465:4ab7%en0,fe80:0:0:0:0:0:0:1%lo0,fe80:0:0:0:1ae0:cf19:8747:686b%utun2,fe80:0:0:0:25b0:65f5:9d5d:6151%utun1,fe80:0:0:0:2ce4:41ff:feee:b9f1%ap1,fe80:0:0:0:74ae:10ff:fea1:ed3%awdl0,fe80:0:0:0:74ae:10ff:fea1:ed3%llw0,fe80:0:0:0:c26:a7f7:ee0e:f334%en0,fe80:0:0:0:c794:8082:78b:e931%utun0,fe80:0:0:0:ce81:b1c:bd2c:69e%utun3",thread_pool_name="test",type="java.util.concurrent.ThreadPoolExecutor",} 0.0
# HELP fuled_dynamic_thread_pool_reject_count ThreadPoolRejectCount
# TYPE fuled_dynamic_thread_pool_reject_count gauge
fuled_dynamic_thread_pool_reject_count{queue_type="java.util.concurrent.ArrayBlockingQueue",reject_handler_type="java.util.concurrent.ThreadPoolExecutor$AbortPolicy",thread_pool_app_name="fuled-example-dynamic-threadpool",thread_pool_ipv4="192.168.10.38",thread_pool_ipv6="0:0:0:0:0:0:0:1%lo0,2408:820c:8f3c:361:7d16:b506:2e5a:c6bb%en0,2408:820c:8f3c:361:864:a4bd:465:4ab7%en0,fe80:0:0:0:0:0:0:1%lo0,fe80:0:0:0:1ae0:cf19:8747:686b%utun2,fe80:0:0:0:25b0:65f5:9d5d:6151%utun1,fe80:0:0:0:2ce4:41ff:feee:b9f1%ap1,fe80:0:0:0:74ae:10ff:fea1:ed3%awdl0,fe80:0:0:0:74ae:10ff:fea1:ed3%llw0,fe80:0:0:0:c26:a7f7:ee0e:f334%en0,fe80:0:0:0:c794:8082:78b:e931%utun0,fe80:0:0:0:ce81:b1c:bd2c:69e%utun3",thread_pool_name="test",type="java.util.concurrent.ThreadPoolExecutor",} 0.0
# HELP fuled_dynamic_thread_pool_active_count ThreadPoolActiveCount
# TYPE fuled_dynamic_thread_pool_active_count gauge
fuled_dynamic_thread_pool_active_count{queue_type="java.util.concurrent.ArrayBlockingQueue",reject_handler_type="java.util.concurrent.ThreadPoolExecutor$AbortPolicy",thread_pool_app_name="fuled-example-dynamic-threadpool",thread_pool_ipv4="192.168.10.38",thread_pool_ipv6="0:0:0:0:0:0:0:1%lo0,2408:820c:8f3c:361:7d16:b506:2e5a:c6bb%en0,2408:820c:8f3c:361:864:a4bd:465:4ab7%en0,fe80:0:0:0:0:0:0:1%lo0,fe80:0:0:0:1ae0:cf19:8747:686b%utun2,fe80:0:0:0:25b0:65f5:9d5d:6151%utun1,fe80:0:0:0:2ce4:41ff:feee:b9f1%ap1,fe80:0:0:0:74ae:10ff:fea1:ed3%awdl0,fe80:0:0:0:74ae:10ff:fea1:ed3%llw0,fe80:0:0:0:c26:a7f7:ee0e:f334%en0,fe80:0:0:0:c794:8082:78b:e931%utun0,fe80:0:0:0:ce81:b1c:bd2c:69e%utun3",thread_pool_name="test",type="java.util.concurrent.ThreadPoolExecutor",} 0.0
```
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
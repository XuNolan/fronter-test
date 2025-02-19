package project.xunolan.websocket.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BeanUtils implements ApplicationContextAware {
    //主要用于，每次调用实例化后的实体对象里面autowire一个全局唯一的服务
    private static ApplicationContext applicationContext;
    /**
     * Spring容器会在创建该Bean之后，自动调用该Bean的setApplicationContext方法
     *
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (BeanUtils.applicationContext == null) {
            BeanUtils.applicationContext = applicationContext;
        }
    }

    //通过name获取 Bean.
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

}
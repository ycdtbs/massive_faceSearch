package com.tangcheng.face_search.common.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description: SpringContext  获取 Spring 上下文信息
 * @Author: mingtian
 * @CreateDate: 2020/6/8 14:59
 * @Version: 1.0
 */
@Component
@Log4j2
public class SpringContext implements ApplicationContextAware {

    /**
     * 打印日志
     */

    /**
     * 获取上下文对象
     */
    private static ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContext.applicationContext = applicationContext;
        log.info("set applicationContext");
    }

    /**
     * 获取 applicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过 name 获取 bean 对象
     *
     * @param name
     * @return
     */
    public static Object getBean(String name) {

        return getApplicationContext().getBean(name);
    }

    /**
     * 通过 class 获取 bean 对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过 name，clazz  获取指定的 bean 对象
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

}
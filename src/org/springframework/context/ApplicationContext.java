package org.springframework.context;

import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.InvocationTargetException;

public class ApplicationContext {
    BeanFactory beanFactory = new BeanFactory();

    public ApplicationContext(String basePackage) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        System.out.println("*************** Context is under construction **********************");

        beanFactory.instantiate(basePackage);
        beanFactory.populateProperties();
        beanFactory.injectBeanNames();
        beanFactory.initializeBeans();
    }

    public void close() {
        beanFactory.close();
    }
}

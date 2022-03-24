package com.sander;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.CustomPostProcessor;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.instantiate("com.sander");

        ProductService productService = (ProductService) beanFactory.getBean("productService");
        System.out.println(productService);

        System.out.println(productService.getPromotionsService());


        beanFactory.populateProperties();
        beanFactory.injectBeanNames();

        PromotionsService promotionsService = productService.getPromotionsService();

        System.out.println("Bean name = " + promotionsService.getBeanName());

        beanFactory.initializeBeans();

        beanFactory.addPostProcessor(new CustomPostProcessor());
    }
}

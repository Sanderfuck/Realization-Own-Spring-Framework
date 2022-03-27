package com.sander;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.CustomPostProcessor;
import org.springframework.context.ApplicationContext;


public class Main {
    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        try {
            testBeanFactory();
            testContext();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    void testBeanFactory() throws ReflectiveOperationException {

        BeanFactory beanFactory = new BeanFactory();
        beanFactory.addPostProcessor(new CustomPostProcessor());

        beanFactory.instantiate("com.sander");
        beanFactory.populateProperties();
        beanFactory.injectBeanNames();
        beanFactory.initPostConstructMethod();
        beanFactory.initializeBeans();


        ProductService productService = (ProductService) beanFactory.getBean("productService");
        System.out.println(productService);


        PromotionsService promotionsService = productService.getPromotionsService();
        System.out.println(productService);
        System.out.println("Bean name = " + promotionsService.getBeanName());

        System.out.println(productService.getClass());

        beanFactory.close();
    }

    void testContext() throws ReflectiveOperationException {
        ApplicationContext applicationContext = new ApplicationContext("com.sander");
        applicationContext.close();
    }
}

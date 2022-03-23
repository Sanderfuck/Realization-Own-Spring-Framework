package com.sander;

import org.springframework.beans.factory.BeanFactory;

public class Main {
    public static void main(String[] args) {
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.instatiate("com.sander");

        ProductService productService = (ProductService) beanFactory.getBean("productService");
        System.out.println(productService);

        System.out.println(productService.getPromotionsService());
    }
}

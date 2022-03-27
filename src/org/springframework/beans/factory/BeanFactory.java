package org.springframework.beans.factory;

import org.springframework.beans.factory.anotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.stereotype.Component;
import org.springframework.beans.factory.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class BeanFactory {
    private Map<String, Object> singletons = new HashMap<>();
    private List<BeanPostProcessor> postProcessors = new ArrayList<>();

    public Object getBean(String beanName) {
        return singletons.get(beanName);
    }

    public void addPostProcessor(BeanPostProcessor postProcessor) {
        postProcessors.add(postProcessor);
    }

    public Map<String, Object> getSingletons() {
        return singletons;
    }

    public void instantiate(String basePackage) {
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            String path = basePackage.replace('.', '/');

            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                File file = new File(resource.toURI());
                for (File classFile : file.listFiles()) {
                    String fileName = classFile.getName();

                    if (fileName.endsWith(".class")) {
                        String className = fileName.substring(0, fileName.lastIndexOf("."));
                        Class classObject = Class.forName(basePackage + "." + className);
                        if (classObject.isAnnotationPresent(Component.class) || classObject.isAnnotationPresent(Service.class)) {
                            System.out.println("Component: " + classObject);

                            Object instance = classObject.getDeclaredConstructor().newInstance();

                            String beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
                            singletons.put(beanName, instance);
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void populateProperties() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        System.out.println("==populateProperties==");

        for (Object object : singletons.values()) {
            for (Field field : object.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    for (Object dependency : singletons.values()) {
                        if (dependency.getClass().equals(field.getType())) {
                            String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                            System.out.println("Setter name = " + setterName);
                            Method setter = object.getClass().getMethod(setterName, dependency.getClass());
                            setter.invoke(object, dependency);
                        }
                    }
                }
            }
        }
    }

    public void injectBeanNames() {
        singletons.forEach((name, bean) -> {
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(name);
            }
        });
    }

    public void initializeBeans() {
        for (String name : singletons.keySet()) {
            Object bean = singletons.get(name);
            for (BeanPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessorBeforeInitialization(bean, name);
            }

            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }

            for (BeanPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessorAfterInitialization(bean, name);
            }
        }
    }

    public void initPostConstructMethod() {
        for (Object bean : singletons.values()) {
            for (Method method : bean.getClass().getMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    try {
                        method.invoke(bean);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (bean instanceof PostConstruction) {
                ((PostConstruction) bean).init();
            }
        }
    }

    public void close() {
        for (Object bean : singletons.values()) {
            for (Method method : bean.getClass().getMethods()) {
                if (method.isAnnotationPresent(PreDestroy.class)) {
                    try {
                        method.invoke(bean);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (bean instanceof DisposableBean) {
                ((DisposableBean)bean).destroy();
            }
        }
    }
}

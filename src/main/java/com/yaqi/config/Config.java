package com.yaqi.config;

import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author wangyq26022@yunrong.cn
 * @version V3.0.1
 * @since 2022/4/21 23:20
 */
public class Config {

    private static ArrayList<String> configs = new ArrayList<>();

    static {
        try {
            FileInputStream fileInputStream = new FileInputStream("C:\\help-bean-init.properties");
            Properties properties = new Properties();
            properties.load(fileInputStream);
            System.out.println("-----------help-bean-init--------------");
            properties.values().forEach((value) -> {
                System.out.println(value);
                configs.add((String) value);
            });
            System.out.println("-----------help-bean-init--------------");
            fileInputStream.close();
        } catch (Exception e) {
            System.out.println("急速启动器获取配置失败 : " + e.getMessage());
            throw new RuntimeException("急速启动器获取配置失败", e);
        }

    }


    public static boolean isIntercept(String string) {
        return configs.contains(string);
    }

    public static boolean isNotLazy(BeanDefinition beanDefinition) {
        String beanClassName = null;
        if (!(beanDefinition instanceof AnnotatedGenericBeanDefinition) && !(beanDefinition instanceof ScannedGenericBeanDefinition)) {
            if (beanDefinition instanceof RootBeanDefinition) {
                beanClassName = beanDefinition.getBeanClassName();
                if (null == beanClassName) {
                    String factoryMethodName = beanDefinition.getFactoryMethodName();
                    return configs.contains(factoryMethodName);
                } else {
                    if (beanClassName.contains("$$")) {
                        beanClassName = beanClassName.substring(0, beanClassName.indexOf("$"));
                    }
                    return configs.contains(beanClassName);
                }
            } else {
                return false;
            }
        } else {
            beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName.contains("$$")) {
                beanClassName = beanClassName.substring(0, beanClassName.indexOf("$"));
            }

            boolean notLazy = configs.contains(beanClassName);
            return notLazy;
        }
    }
}

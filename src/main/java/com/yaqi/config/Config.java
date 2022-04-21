package com.yaqi.config;

import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.JarFile;

/**
 * @author wangyq26022@yunrong.cn
 * @version V3.0.1
 * @since 2022/4/21 23:20
 */
public class Config {
    private static ArrayList<String> notLazyBeans = new ArrayList();

    public Config() {
    }

    public static boolean isNotLazy(BeanDefinition beanDefinition) {
        String beanClassName;
        if (!(beanDefinition instanceof AnnotatedGenericBeanDefinition) && !(beanDefinition instanceof ScannedGenericBeanDefinition)) {
            if (beanDefinition instanceof RootBeanDefinition) {
                beanClassName = beanDefinition.getBeanClassName();
                if (null == beanClassName) {
                    String factoryMethodName = beanDefinition.getFactoryMethodName();
                    return notLazyBeans.contains(factoryMethodName);
                } else {
                    if (beanClassName.contains("$$")) {
                        beanClassName = beanClassName.substring(0, beanClassName.indexOf("$"));
                    }

                    return notLazyBeans.contains(beanClassName);
                }
            } else {
                return false;
            }
        } else {
            beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName.contains("$$")) {
                beanClassName = beanClassName.substring(0, beanClassName.indexOf("$"));
            }

            boolean notLazy = notLazyBeans.contains(beanClassName);
            return notLazy;
        }
    }

    static {
        URL d = Config.class.getResource("");
        InputStream resourceAsStream = null;
        if ("jar".equals(d.getProtocol())) {
            try {
                JarURLConnection urlConnection = (JarURLConnection)d.openConnection();
                JarFile jarFile = urlConnection.getJarFile();
                String jarnamepath = jarFile.getName().substring(0, jarFile.getName().lastIndexOf("\\"));
                System.out.println("jarnamepath:" + jarnamepath);
                resourceAsStream = new FileInputStream(new File(jarnamepath) + "\\help-bean-init.properties");
            } catch (Exception var6) {
                var6.printStackTrace();
                System.out.println("急速启动器获取配置失败 : " + var6.getMessage());
            }
        } else {
            resourceAsStream = Config.class.getClassLoader().getResourceAsStream("help-bean-init.properties");
        }

        try {
            Properties properties = new Properties();
            properties.load((InputStream)resourceAsStream);
            System.out.println("-----------help-bean-init--------------");
            properties.values().forEach((value) -> {
                System.out.println(value);
                notLazyBeans.add((String)value);
            });
            System.out.println("-----------help-bean-init--------------");
            ((InputStream)resourceAsStream).close();
        } catch (IOException var5) {
            var5.printStackTrace();
            System.out.println("急速启动器获取配置失败 : " + var5.getMessage());
        }

    }
}

package cn.gtmap.ferry.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;

/**
 * @author ShiYou
 * @date 2023年10月09日 14:40
 * Description:
 */
@Slf4j
public class JarHelper {
    /**
     * 加载驱动程序（类隔离方式）
     *
     * @param file            文件
     * @param driverClassName 驱动程序类名称
     * @return {@link Driver}
     */
    public static Driver loadDriver(File file, String driverClassName) {
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, null);
            return (Driver) classLoader.loadClass(driverClassName).newInstance();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            log.error("加载驱动文件异常：{}", e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error("加载驱动类{}失败：{}", driverClassName, e.getMessage());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            log.error("驱动实例化异常：{}", e.getMessage());
        }

        return null;
    }

    /**
     * 卸载驱动程序
     *
     * @param driver 驱动
     */
    public static void unloadDriver(Driver driver) {
        try {
            URLClassLoader classLoader = (URLClassLoader) driver.getClass().getClassLoader();
            // 关闭类加载器并情况状态
            classLoader.close();
            classLoader.clearAssertionStatus();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("移除驱动失败：{}", e.getMessage());
        }
    }
}

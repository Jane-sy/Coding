package cn.gtmap.ferry.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author ShiYou
 * @date 2023年10月09日 14:56
 * Description:
 */
public interface DriverService {

    /**
     * 添加驱动
     *
     * @param file      驱动文件
     * @param className 驱动类名称
     * @return boolean
     */
    boolean addDriver(MultipartFile file, String className);

    /**
     * 获取所有加载的驱动
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> getDrivers();

    /**
     * 删除数据库驱动
     *
     * @param id 唯一标识
     * @return boolean
     */
    boolean deleteDriver(String id) throws Throwable;
}

package cn.gtmap.ferry.service;

import cn.gtmap.ferry.entity.ConnectDTO;

import java.util.Map;

/**
 * @author ShiYou
 * @date 2023年10月10日 15:17
 * Description:
 */
public interface ConnectService {

    /**
     * 创建数据库连接
     *
     * @param connectDTO 连接信息
     * @return boolean
     */
    boolean createConnect(ConnectDTO connectDTO);

    /**
     * 获取所有连接信息
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> getConnects();

    /**
     * 删除数据库连接
     *
     * @param id 唯一标识
     * @return boolean
     */
    boolean deleteConnect(String id);
}

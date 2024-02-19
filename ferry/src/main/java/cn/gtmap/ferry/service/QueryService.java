package cn.gtmap.ferry.service;

import cn.gtmap.ferry.entity.PageOut;

import java.util.List;
import java.util.Map;

/**
 * @author ShiYou
 * @date 2023年10月10日 15:25
 * Description:
 */
public interface QueryService {

    /**
     * 简单查询
     *
     * @param connectId 数据库连接唯一标识
     * @param sql       查询语句
     * @return {@link List}<{@link Map}<{@link String}, {@link Object}>>
     */
    List<Map<String, Object>> query(String connectId, String sql);

    /**
     * 简单分页查询
     *
     * @param connectId 数据库连接唯一标识
     * @param sql       查询语句
     * @param page      页码
     * @param size      页数
     * @return {@link PageOut}<{@link Map}<{@link String}, {@link Object}>>
     */
    PageOut<Map<String, Object>> pageQuery(String connectId, String sql, int page, int size);
}

package cn.gtmap.ferry.service.impl;

import cn.gtmap.ferry.entity.PageOut;
import cn.gtmap.ferry.service.QueryService;

import java.util.List;
import java.util.Map;

/**
 * @author ShiYou
 * @date 2023年10月10日 15:30
 * Description:
 */
public class QueryServiceImpl implements QueryService {
    @Override
    public List<Map<String, Object>> query(String connectId, String sql) {
        return null;
    }

    @Override
    public PageOut<Map<String, Object>> pageQuery(String connectId, String sql, int page, int size) {
        return null;
    }
}

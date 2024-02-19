package cn.gtmap.ferry.utils;


import cn.gtmap.ferry.constant.Constant;
import cn.gtmap.ferry.entity.PageOut;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ShiYou
 * @date 2023年08月15日 10:30
 * Description: 查询工具
 */
@Slf4j
public class QueryHelper {

    /**
     * 获取所有表的基本信息
     *
     * @param cnn 连接
     * @return {@link List}<{@link Map}<{@link String}, {@link Object}>>
     */
    public static List<Map<String, Object>> getAllTables(Connection cnn) {
        List<Map<String, Object>> finalResult = new ArrayList<>();

        try {
            // 获取连接的表名和元信息
            String catalog = cnn.getCatalog();
            DatabaseMetaData metaData = cnn.getMetaData();

            // 获取所有表信息
            ResultSet tables = metaData.getTables(catalog, null, null, null);

            while (tables.next()) {
                Map<String, Object> temp = new HashMap<>(3);

                // 填充表信息
                temp.put(Constant.TABLE_NAME, tables.getString(Constant.TABLE_NAME));
                temp.put(Constant.TABLE_TYPE, tables.getString(Constant.TABLE_TYPE));
                temp.put(Constant.REMARKS, tables.getString(Constant.REMARKS));

                finalResult.add(temp);
            }

        } catch (SQLException e) {
            log.error("获取数据库下所有表信息失败：{}", e.getMessage());
        }

        return finalResult;
    }

    /**
     * 获取表信息
     *
     * @param cnn       连接
     * @param tableName 表名
     * @return {@link List}<{@link Map}<{@link String}, {@link Object}>>
     */
    public static List<Map<String, Object>> getTableInfo(Connection cnn, String tableName) {

        List<Map<String, Object>> finalResult = new ArrayList<>();

        try {
            // 获取连接的表名和元信息
            String catalog = cnn.getCatalog();
            DatabaseMetaData metaData = cnn.getMetaData();
            String userName = metaData.getUserName();

            // 获取所有字段信息
            ResultSet columns = metaData.getColumns(catalog, userName, tableName, null);

            while (columns.next()) {
                Map<String, Object> temp = new HashMap<>(3);

                // 填充字段信息
                temp.put(Constant.COLUMN_NAME, columns.getString(Constant.COLUMN_NAME));
                temp.put(Constant.TYPE_NAME, columns.getString(Constant.TYPE_NAME));
                temp.put(Constant.COLUMN_SIZE, columns.getString(Constant.COLUMN_SIZE));
                temp.put(Constant.COLUMN_DEF, columns.getString(Constant.COLUMN_DEF));
                temp.put(Constant.REMARKS, columns.getString(Constant.REMARKS));
                temp.put(Constant.IS_AUTOINCREMENT, columns.getString(Constant.IS_AUTOINCREMENT));

                finalResult.add(temp);
            }

        } catch (SQLException e) {
            log.error("获取{}表字段信息失败：{}", tableName, e.getMessage());
        }

        return finalResult;
    }

    /**
     * 得到sql执行结果
     *
     * @param cnn 连接
     * @param sql sql语句
     * @return {@link List}<{@link Map}<{@link String}, {@link Object}>>
     */
    public static List<Map<String, Object>> executeQuery(Connection cnn, String sql) {
        List<Map<String, Object>> finalResult = new ArrayList<>();

        // sql合法性校验
        validSql(sql);

        try {
            // 创建执行语句
            Statement statement = cnn.createStatement();
            // 获取执行结果
            ResultSet resultSet = statement.executeQuery(sql);
            List<Map<String, String>> metaData = getMetaData(resultSet);

            while (resultSet.next()) {
                Map<String, Object> temp = new HashMap<>(metaData.size());

                // 获取单行每个字段值
                for (Map<String, String> meta : metaData) {
                    String label = meta.get(Constant.COLUMN_LABEL);
                    Object value = resultSet.getObject(label);

                    temp.put(label, value);
                }

                finalResult.add(temp);
            }

        } catch (SQLException e) {
            log.error("解析返回结果失败：{}", e.getMessage());
            return new ArrayList<>();
        }

        return finalResult;
    }

    /**
     * 分页查询
     *
     * @param cnn  连接
     * @param sql  sql
     * @param size 大小
     * @param page 页面（从1开始）
     * @return {@link PageOut}<{@link Map}<{@link String}, {@link Object}>>
     */
    public static PageOut<Map<String, Object>> pageQuery(Connection cnn, String sql, int size, int page) {
        PageOut<Map<String, Object>> finalResult = new PageOut<>(size, page);

        // 获取分页总量
        try {
            // 使用count统计总量（查询表需要给别名）
            List<Map<String, Object>> count = executeQuery(cnn, "SELECT COUNT(*) \"TOTAL\" FROM (" + sql + ") T");
            String total = String.valueOf(count.get(0).get("TOTAL"));
            finalResult.setTotal(Integer.parseInt(total));
        } catch (Exception e) {
            log.error("获取数据总量失败：{}", e.getMessage());
        }

        // 起始索引
        int index = size * (page - 1);
        // 获取分页数据
        try {
            List<Map<String, Object>> content = executeQuery(cnn,
                    sql.concat(" LIMIT ").concat(String.valueOf(size)).concat(" OFFSET ").concat(String.valueOf(index)));
            finalResult.setContent(content);
        } catch (Exception e) {
            log.error("获取数据结果失败：{}", e.getMessage());
        }

        return finalResult;
    }

    /**
     * 得到元数据
     *
     * @param resultSet 结果集
     * @return {@link List}<{@link Map}<{@link String}, {@link String}>>
     */
    private static List<Map<String, String>> getMetaData(ResultSet resultSet) {
        List<Map<String, String>> metaInfo = new ArrayList<>();

        try {
            // 获取结果的元信息
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                Map<String, String> meta = new HashMap<>(3);

                // 分别获取返回字段的列名
                meta.put(Constant.COLUMN_LABEL, metaData.getColumnLabel(i));

                metaInfo.add(meta);
            }
        } catch (SQLException e) {
            log.error("解析返回元数据失败：{}", e.getMessage());
            return new ArrayList<>();
        }

        return metaInfo;
    }

    /**
     * 校验sql的有效性
     * <br>TODO 过滤查询以为的非法操作并验证sql语句的正确性
     *
     * @param sql sql
     */
    private static void validSql(String sql) {
        // 定义非法字符
        String invalid = "INSERT,insert,UPDATE,update,DELETE,delete,DROP,drop";

        for (String s : invalid.split(Constant.SYMBOL_COMMA)) {
            if (sql.contains(s)) {
                throw new RuntimeException("不支持的操作：" + s);
            }
        }
    }
}

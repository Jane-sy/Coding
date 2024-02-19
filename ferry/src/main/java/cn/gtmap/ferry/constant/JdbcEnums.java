package cn.gtmap.ferry.constant;

/**
 * 常用的数据库信息枚举
 *
 * @author gt
 * @date 2023/08/22
 */
public enum JdbcEnums {

    /**
     * MySQL连接类型
     */
    MYSQL("mysql", "jdbc:mysql://{ip}:{port}/{table}", "com.mysql.cj.jdbc.Driver", "3306"),
    /**
     * Oracle连接类型
     */
    ORACLE("oracle", "jdbc:oracle:thin:@{host}:{port}/{table}", "oracle.jdbc.driver.OracleDriver", "1521"),
    /**
     * PostgreSQL连接类型
     */
    POSTGRESQL("postgresql", "jdbc:postgresql://{ip}:{port}/{table}", "org.postgresql.Driver", "5432"),
    ;

    /**
     * 类型
     */
    private final String type;
    /**
     * 连接地址
     */
    private final String url;
    /**
     * 驱动类名
     */
    private final String driverClass;
    /**
     * 端口
     */
    private final String port;

    /**
     * 构造器
     *
     * @param type        类型
     * @param url         连接地址
     * @param driverClass 驱动类名
     * @param port        端口
     */
    JdbcEnums(String type, String url, String driverClass, String port) {
        this.type = type;
        this.url = url;
        this.driverClass = driverClass;
        this.port = port;
    }

    /**
     * 获取连接地址
     *
     * @param type 类型
     * @return {@link String}
     */
    public static String getUrl(String type) {
        for (JdbcEnums jdbcEnums : JdbcEnums.values()) {
            if (jdbcEnums.type.equals(type)) {
                return jdbcEnums.url;
            }
        }
        throw new RuntimeException("不支持的数据库类型");
    }

    /**
     * 获取驱动程序类
     *
     * @param type 类型
     * @return {@link String}
     */
    public static String getDriverClass(String type) {
        for (JdbcEnums jdbcEnums : JdbcEnums.values()) {
            if (jdbcEnums.type.equals(type)) {
                return jdbcEnums.driverClass;
            }
        }
        throw new RuntimeException("不支持的数据库类型");
    }

    /**
     * 得到端口号
     *
     * @param type 类型
     * @return {@link String}
     */
    public static String getPort(String type) {
        for (JdbcEnums jdbcEnums : JdbcEnums.values()) {
            if (jdbcEnums.type.equals(type)) {
                return jdbcEnums.port;
            }
        }
        throw new RuntimeException("不支持的数据库类型");
    }

    /**
     * 得到类型
     *
     * @return {@link String}
     */
    public String getType() {
        return this.type;
    }
}

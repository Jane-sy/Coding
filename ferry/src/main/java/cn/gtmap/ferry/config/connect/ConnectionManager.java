package cn.gtmap.ferry.config.connect;

import cn.gtmap.ferry.config.driver.DriverWrapper;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author ShiYou
 * @date 2023年07月27日 9:59
 * Description: 数据库连接池（创建连接、超时关闭）
 * TODO 改造方案：
 * 1. 接入DriverWrapper实现类隔离；
 * 2.1 方式一：每个数据库连接补充uuid字段作为区分，根据uuid获取数据库连接、移除数据库连接（便于入库记录）
 * 2.2 方式二：沿用旧的线程池管理方式，改造线程的key值，同一库使用同一连接
 */
@Slf4j
public class ConnectionManager extends Thread {
    /**
     * 连接超时中断时长
     */
    public static final long CONN_TIMEOUT = 3 * 60 * 1000L;
    /**
     * 连接检查周期
     */
    private static final long CONN_CHECK_TIME = 5 * 60 * 1000L;
    /**
     * 最大连接数
     */
    private static final Integer MAX_CONNECTION_NUM = 10;
    /**
     * jdbc池
     */
    private static ConcurrentHashMap<String, LinkedBlockingQueue<ConnectionWrapper>> JDBC_POOL = new ConcurrentHashMap<>();
    /**
     * 单连接最大限制数
     */
    private static Map<String, Integer> CONNECTION_NUM_LIMIT = new HashMap<>();

    /**
     * 获取jdbc连接
     *
     * @param driverName 驱动名
     * @param dbType     数据库类型
     * @param jdbcUrl    jdbc url
     * @param username   用户名
     * @param password   密码
     * @return {@link ConnectionWrapper}
     */
    public static ConnectionWrapper getConnection(String driverName, String dbType, String jdbcUrl, String username, String password) {
        try {
            ConnectionWrapper connectionWrapper = null;
            String key = dbType + "-" + jdbcUrl;
            // 从总的连接池获取
            LinkedBlockingQueue<ConnectionWrapper> queue = JDBC_POOL.get(key);
            synchronized (ConnectionManager.class) {
                if (queue == null) {
                    log.info("***初始化key为【{}】的连接队列***", key);
                    queue = new LinkedBlockingQueue<>();
                    JDBC_POOL.put(key, queue);
                    CONNECTION_NUM_LIMIT.put(key, 0);
                }
                Integer limitNum = CONNECTION_NUM_LIMIT.get(key);
                log.info("***key为【{}】对应的limitNum = 【{}】***", key, limitNum);
                if (!queue.isEmpty()) {
                    log.info("***key为【{}】的连接池中有可用的连接，直接获取***", key);
                    return queue.take();
                }
                if (limitNum >= MAX_CONNECTION_NUM) {
                    log.info("***key为【{}】超过了最大连接数，进入等待队列***", key);
                    connectionWrapper = JDBC_POOL.get(key).take();
                } else {
                    log.info("***key为【{}】无可用的连接，开始创建连接***", key);
                    connectionWrapper = createConnection(driverName, dbType, jdbcUrl, username, password);
                    CONNECTION_NUM_LIMIT.put(key, CONNECTION_NUM_LIMIT.get(key) + 1);
                }
            }
            return connectionWrapper;
        } catch (Exception e) {
            log.error("getConnection error", e);
        }
        return null;
    }

    /**
     * 创建jdbc连接
     *
     * @param driverName 驱动名
     * @param dbType     数据库类型
     * @param jdbcUrl    jdbc url
     * @param username   用户名
     * @param password   密码
     * @return {@link ConnectionWrapper}
     * @throws SQLException           sqlexception异常
     * @throws ClassNotFoundException 类没有发现异常
     */
    private static ConnectionWrapper createConnection(String driverName, String dbType, String jdbcUrl, String username, String password) throws SQLException, ClassNotFoundException {
        String key = dbType + "-" + jdbcUrl;
        // 加载驱动
        Class.forName(driverName);
        // 创建连接所需的用户名密码等参数
        Properties properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        properties.put("remarksReporting", "true");
        // 创建连接
        Connection conn = DriverManager.getConnection(jdbcUrl, properties);
        // 连接放入线程池
        return new ConnectionWrapper(conn, JDBC_POOL.get(key));
    }

    /**
     * 创建jdbc连接
     *
     * @param driverWrapper 驱动
     * @param jdbcUrl       jdbc url
     * @param username      用户名
     * @param password      密码
     * @return {@link ConnectionWrapper}
     * @throws SQLException sql异常
     */
    private static ConnectionWrapper createConnection(DriverWrapper driverWrapper, String jdbcUrl, String username, String password) throws SQLException {
        // 创建连接所需的用户名密码等参数
        Properties properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        properties.put("remarksReporting", "true");
        // 获取数据库连接
        Connection conn = driverWrapper.getDriver().connect(jdbcUrl, properties);
        // 连接放入线程池
        return new ConnectionWrapper(conn, JDBC_POOL.get(jdbcUrl));
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(CONN_CHECK_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("sleep error", e);
            }
            int size = 0;
            for (String key : JDBC_POOL.keySet()) {
                LinkedBlockingQueue<ConnectionWrapper> queue = JDBC_POOL.get(key);
                log.info("***开始校验连接池中是否有过期连接，size：【{}】 key：【{}】***", queue.size(), key);
                synchronized (ConnectionManager.class) {
                    Integer num = 0;
                    while (queue.peek() != null && queue.peek().checkTimeout()) {
                        ConnectionWrapper connectionWrapper = queue.poll();
                        try {
                            log.info("***关闭空闲jdbc连接：{}***", key);
                            connectionWrapper.closeConnection();
                        } catch (SQLException e) {
                            log.error("关闭连接失败", e);
                        }
                        num--;
                    }
                    CONNECTION_NUM_LIMIT.put(key, CONNECTION_NUM_LIMIT.get(key) + num);
                }
                size += queue.size();
            }
            log.info("***本次校验完毕，连接池总数量为【{}】***", size);
        }
    }

}

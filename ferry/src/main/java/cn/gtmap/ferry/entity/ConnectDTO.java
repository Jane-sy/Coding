package cn.gtmap.ferry.entity;

import lombok.Data;

/**
 * @author ShiYou
 * @date 2023年10月10日 15:18
 * Description:
 */
@Data
public class ConnectDTO {

    /**
     * 数据库驱动标识
     */
    private String driverId;

    /**
     * 用户
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 数据库连接地址
     */
    private String jdbcUrl;
}

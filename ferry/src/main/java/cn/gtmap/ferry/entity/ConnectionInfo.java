package cn.gtmap.ferry.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ShiYou
 * @date 2023年10月10日 15:43
 * Description:
 */
@Data
public class ConnectionInfo implements Serializable {

    private Date createAt;

    private String username;

    private String password;

    private String jdbcUrl;

    private String driverId;
}

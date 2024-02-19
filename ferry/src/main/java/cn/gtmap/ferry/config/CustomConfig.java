package cn.gtmap.ferry.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ShiYou
 * @date 2023年10月10日 9:34
 * Description:
 */
@Data
@ConfigurationProperties(prefix = "custom")
public class CustomConfig {

    String driverFolder;
}

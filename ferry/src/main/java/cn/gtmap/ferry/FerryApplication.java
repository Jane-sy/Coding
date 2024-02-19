package cn.gtmap.ferry;

import cn.gtmap.ferry.config.CustomConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author gt
 */
@SpringBootApplication
@ComponentScan(basePackages = {"cn.gtmap.ferry.*"})
@EnableConfigurationProperties({CustomConfig.class})
public class FerryApplication {

    public static void main(String[] args) {
        SpringApplication.run(FerryApplication.class, args);
    }

}

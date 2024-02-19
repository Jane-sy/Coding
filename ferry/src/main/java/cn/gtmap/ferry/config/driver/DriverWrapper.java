package cn.gtmap.ferry.config.driver;

import cn.gtmap.ferry.utils.JarHelper;
import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.sql.Driver;

/**
 * @author ShiYou
 * @date 2023年10月09日 14:48
 * Description:
 */
@Data
public class DriverWrapper implements Serializable {

    private String filePath;
    private String className;
    private Driver driver;

    /**
     * 生成驱动实例
     *
     * @param filePath
     * @param className
     */
    public DriverWrapper(String filePath, String className) {
        this.filePath = filePath;
        this.className = className;
        this.driver = JarHelper.loadDriver(new File(this.filePath), this.className);
    }

    /**
     * 销毁驱动实例
     *
     * @throws Throwable 异常
     */
    public void unload() throws Throwable {
        JarHelper.unloadDriver(this.driver);
        super.finalize();
    }
}

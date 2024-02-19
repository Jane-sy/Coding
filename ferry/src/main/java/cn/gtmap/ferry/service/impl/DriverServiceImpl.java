package cn.gtmap.ferry.service.impl;

import cn.gtmap.ferry.config.CustomConfig;
import cn.gtmap.ferry.config.driver.DriverWrapper;
import cn.gtmap.ferry.service.DriverService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ShiYou
 * @date 2023年10月10日 8:54
 * Description:
 */
@Slf4j
@Service
public class DriverServiceImpl implements DriverService {

    private Map<String, DriverWrapper> drivers = new HashMap<>();

    @Resource
    private CustomConfig config;

    @Override
    public boolean addDriver(MultipartFile file, String className) {

        // 文件保存路径
        String filePath = config.getDriverFolder() + "/" + +System.currentTimeMillis() + "/" + file.getName();

        // 保存驱动文件
        try {
            FileUtil.writeFromStream(file.getInputStream(), filePath);
        } catch (IOException e) {
            log.error("### 保存驱动文件失败 ###");
            throw new RuntimeException("添加驱动失败");
        }

        // 加载驱动
        DriverWrapper driverWrapper = new DriverWrapper(filePath, className);
        drivers.put(String.valueOf(System.currentTimeMillis()), driverWrapper);

        return true;
    }

    @Override
    public Map<String, Object> getDrivers() {
        Map<String, Object> info = new HashMap<>();

        if (CollUtil.isNotEmpty(drivers)) {
            for (Map.Entry<String, DriverWrapper> item : drivers.entrySet()) {
                // 把创建时间作为唯一标识
                info.put("id", item.getKey());

                DriverWrapper value = item.getValue();
                info.put("className", value.getClassName());
                info.put("file", value.getFilePath());
            }
        }

        return info;
    }

    @Override
    public boolean deleteDriver(String id) throws Throwable {
        DriverWrapper driverWrapper = drivers.get(id);

        // 卸载驱动
        Assert.notNull(driverWrapper, "为获取到数据库驱动");
        driverWrapper.unload();

        // 删除驱动文件
        File file = new File(driverWrapper.getFilePath());
        return FileUtil.del(file.getParentFile());
    }

}

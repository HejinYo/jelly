package cn.hejinyo.jelly.modules.oss.cloud;

import cn.hejinyo.jelly.common.consts.ConfigConstant;
import cn.hejinyo.jelly.modules.sys.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文件上传Factory
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-26 10:18
 */
@Component
public final class OSSFactory {
    private static SysConfigService sysConfigService;

    @Autowired
    public void setDatastore(SysConfigService sysConfigService) {
        OSSFactory.sysConfigService = sysConfigService;
    }

    public static QiniuCloudStorageService build() {
        //获取云存储配置信息
        CloudStorage config = sysConfigService.getConfigObject(ConfigConstant.CLOUD_STORAGE_CONFIG_KEY, CloudStorage.class);
        return new QiniuCloudStorageService(config);
    }

    /**
     * 覆盖上传
     *
     * @param fileKey 原文件key
     */
    public static QiniuCloudStorageService build(String fileKey) {
        //获取云存储配置信息
        CloudStorage config = sysConfigService.getConfigObject(ConfigConstant.CLOUD_STORAGE_CONFIG_KEY, CloudStorage.class);
        config.setKey(fileKey);
        return new QiniuCloudStorageService(config);
    }

}

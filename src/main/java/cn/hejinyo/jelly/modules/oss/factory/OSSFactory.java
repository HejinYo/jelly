package cn.hejinyo.jelly.modules.oss.factory;

import cn.hejinyo.jelly.common.consts.ConfigConstant;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.modules.oss.model.CloudStorageDTO;
import cn.hejinyo.jelly.modules.oss.service.CloudStorageService;
import cn.hejinyo.jelly.modules.oss.service.impl.QcloudCloudStorageService;
import cn.hejinyo.jelly.modules.oss.service.impl.QiniuCloudStorageService;
import cn.hejinyo.jelly.modules.sys.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文件上传Factory
 *
 * @author hejinyo
 * @date 2017/9/26 11:00
 */
@Component
public final class OSSFactory {
    private static SysConfigService sysConfigService;

    @Autowired
    public void setDatastore(SysConfigService sysConfigService) {
        OSSFactory.sysConfigService = sysConfigService;
    }

    public static CloudStorageService build() {
        //获取云存储配置信息
        CloudStorageDTO config = sysConfigService.getConfig(ConfigConstant.CLOUD_STORAGE_CONFIG_KEY, CloudStorageDTO.class);
        if (config.getType() == Constant.CloudService.QCLOUD.getValue()) {
            return new QcloudCloudStorageService(config);
        } else if (config.getType() == Constant.CloudService.QINIU.getValue()) {
            return new QiniuCloudStorageService(config);
        }
        return new QcloudCloudStorageService(config);
    }
}

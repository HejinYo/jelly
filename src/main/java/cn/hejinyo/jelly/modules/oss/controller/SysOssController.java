package cn.hejinyo.jelly.modules.oss.controller;

import cn.hejinyo.jelly.common.consts.ConfigConstant;
import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.utils.ValidatorUtils;
import cn.hejinyo.jelly.modules.oss.cloud.CloudStorage;
import cn.hejinyo.jelly.modules.oss.cloud.OSSFactory;
import cn.hejinyo.jelly.modules.oss.model.SysOss;
import cn.hejinyo.jelly.modules.oss.service.SysOssService;
import cn.hejinyo.jelly.modules.sys.service.SysConfigService;
import com.google.gson.Gson;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;


/**
 * 文件上传
 *
 * @author :heshuangshuang
 * @date :2018/1/20 10:10
 */
@RestController
@RequestMapping("sys/oss")
public class SysOssController {
    @Autowired
    private SysOssService sysOssService;
    @Autowired
    private SysConfigService sysConfigService;

    private final static String KEY = ConfigConstant.CLOUD_STORAGE_CONFIG_KEY;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:oss:all")
    public Result list(@RequestParam HashMap<String, Object> params) {
        //查询列表数据
        PageInfo<SysOss> ossPageInfo = new PageInfo<>(sysOssService.findPage(new PageQuery(params)));
        return Result.ok(ossPageInfo);
    }


    /**
     * 云存储配置信息
     */
    @RequestMapping("/config")
    @RequiresPermissions("sys:oss:all")
    public Result config() {
        CloudStorage config = sysConfigService.getConfigObject(KEY, CloudStorage.class);

        return Result.ok().put("config", config);
    }


    /**
     * 保存云存储配置信息
     */
    @RequestMapping("/saveConfig")
    @RequiresPermissions("sys:oss:all")
    public Result saveConfig(@RequestBody CloudStorage config) {
        //校验类型
        ValidatorUtils.validate(config);

        sysConfigService.updateValueByKey(KEY, new Gson().toJson(config));

        return Result.ok();
    }


    /**
     * 上传文件
     */
    @RequestMapping("/upload")
    @RequiresPermissions("sys:oss:all")
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new InfoException("上传文件不能为空");
        }

        //上传文件
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String url = OSSFactory.build().uploadSuffix(file.getBytes(), suffix);

        //保存文件信息
        SysOss ossEntity = new SysOss();
        ossEntity.setUrl(url);
        ossEntity.setCreateDate(new Date());
        sysOssService.save(ossEntity);

        return Result.ok().put("url", url);
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:oss:all")
    public Result delete(@RequestBody Long[] ids) {
        sysOssService.deleteArray(ids);
        return Result.ok();
    }

}

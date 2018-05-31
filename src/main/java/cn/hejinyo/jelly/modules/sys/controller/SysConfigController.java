package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.SysConfig;
import cn.hejinyo.jelly.modules.sys.service.SysConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 参数配置
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/3/6 21:39
 */
@RestController
@RequestMapping("/config")
public class SysConfigController extends BaseController {
    @Autowired
    private SysConfigService sysConfigService;


    /**
     * 获得一个配置信息
     */
    @GetMapping(value = "/{sysConfigId}")
    @RequiresPermissions("config:view")
    public Result get(@PathVariable(value = "sysConfigId") Integer id) {
        SysConfig sysConfig = sysConfigService.findOne(id);
        if (sysConfig == null) {
            return Result.error("配置不存在");
        }
        return Result.ok(sysConfig);
    }

    /**
     * 分页查询配置信息
     */
    @RequestMapping("/listPage")
    @RequiresPermissions("config:view")
    public Result list(@RequestParam Map<String, Object> param) {
        //查询列表数据
        PageInfo<SysConfig> sysConfigPageInfo = new PageInfo<>(sysConfigService.findPage(PageQuery.build(param)));
        return Result.ok(sysConfigPageInfo);
    }

    /**
     * 保存配置
     */
    @SysLogger("增加配置")
    @PostMapping
    @RequiresPermissions("config:create")
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysConfig config) {
        sysConfigService.save(config);
        return Result.ok();
    }

    /**
     * 修改配置
     */
    @SysLogger("修改配置")
    @PutMapping(value = "/{sysConfigId}")
    @RequiresPermissions("config:update")
    public Result update(@Validated(RestfulValid.PUT.class) @RequestBody SysConfig sysConfig, @PathVariable("sysConfigId") Integer sysConfigId) {
        sysConfig.setId(sysConfigId);
        int result = sysConfigService.update(sysConfig);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error("未作任何修改");
    }

    /**
     * 删除配置
     */
    @SysLogger("删除配置")
    @RequiresPermissions("config:delete")
    @DeleteMapping(value = "/{sysConfigIdList}")
    public Result delete(@PathVariable("sysConfigIdList") Integer[] ids) {
        int result = sysConfigService.deleteBatch(ids);
        if (result > 0) {
            return Result.ok("删除成功");
        }
        return Result.error("删除失败");
    }
}
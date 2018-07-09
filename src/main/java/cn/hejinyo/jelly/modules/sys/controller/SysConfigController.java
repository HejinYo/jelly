package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.SysConfigEntity;
import cn.hejinyo.jelly.modules.sys.model.SysConfigOptionEntity;
import cn.hejinyo.jelly.modules.sys.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 系统配置
 *
 * @author : heshuangshuang
 * @date : 2018/4/10 17:39
 */
@Api(tags = "系统配置", description = "SysConfigController")
@RestController
@RequestMapping("/sys/config")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 配置信息
     */
    @ApiOperation(value = "配置信息", notes = "配置信息")
    @GetMapping("/{configId}")
    public Result info(@PathVariable("configId") Integer configId) {
        SysConfigEntity config = sysConfigService.findOne(configId);
        return Result.ok(config);
    }

    /**
     * 配置分页查询
     */
    @ApiOperation(value = "配置分页查询", notes = "配置分页查询")
    @RequestMapping(value = "/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public Result list(@RequestParam HashMap<String, Object> pageParam, @RequestBody(required = false) HashMap<String, Object> queryParam) {
        //查询列表数据
        PageInfo<SysConfigEntity> userConfigInfo = new PageInfo<>(sysConfigService.findPage(PageQuery.build(pageParam, queryParam)));
        return Result.ok(userConfigInfo);
    }

    /**
     * 保存配置
     */
    @ApiOperation(value = "保存配置", notes = "保存配置")
    @SysLogger("保存配置")
    @PostMapping
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysConfigEntity config) {
        int count = sysConfigService.save(config);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_SAVE_FAILURE);
    }

    /**
     * 修改配置
     */
    @ApiOperation(value = "修改配置", notes = "修改配置")
    @SysLogger("修改配置")
    @PutMapping(value = "/{configId}")
    public Result update(@PathVariable("configId") Integer configId, @Validated(RestfulValid.PUT.class) @RequestBody SysConfigEntity config) {
        int count = sysConfigService.update(configId, config);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_UPDATE_FAILURE);
    }

    /**
     * 更新配置选项
     *
     */
    @ApiOperation(value = "更新配置选项", notes = "更新配置选项")
    @SysLogger("更新配置选项")
    @PutMapping(value = "/{configId}/{optionId}")
    public Result updateOptionId(@PathVariable("configId") Integer configId, @PathVariable("optionId") Integer optionId) {
        int count = sysConfigService.updateOptionId(configId, optionId);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_UPDATE_FAILURE);
    }

    /**
     * 删除配置
     */
    @ApiOperation(value = "删除配置", notes = "删除配置")
    @SysLogger("删除配置")
    @DeleteMapping("/{configId}")
    public Result delete(@PathVariable("configId") Integer configId) {
        int count = sysConfigService.delete(configId);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_DELETE_FAILURE);
    }

    /**
     * 根据配置code获取配置值列表
     */
    @ApiOperation(value = "根据配置code获取配置值列表", notes = "根据配置code获取配置值列表")
    @GetMapping("/option/{code}")
    public Result getOptionList(@PathVariable("code") String code) {
        return Result.ok(sysConfigService.getOptionListByCode(code));
    }

    /**
     * 保存配置属性
     */
    @ApiOperation(value = "保存配置属性", notes = "保存配置属性")
    @SysLogger("保存配置属性")
    @PostMapping("/option")
    public Result saveOption(@Validated(RestfulValid.POST.class) @RequestBody SysConfigOptionEntity option) {
        int count = sysConfigService.saveOption(option);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_SAVE_FAILURE);
    }

    /**
     * 修改配置属性
     */
    @ApiOperation(value = "修改配置属性", notes = "修改配置属性")
    @SysLogger("修改配置属性")
    @PutMapping("/option/{optionId}")
    public Result updateOption(@PathVariable("optionId") Integer optionId, @Validated(RestfulValid.PUT.class) @RequestBody SysConfigOptionEntity option) {
        int count = sysConfigService.updateOption(optionId, option);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_UPDATE_FAILURE);
    }

    /**
     * 删除配置属性
     */
    @ApiOperation(value = "删除配置属性", notes = "删除配置属性")
    @SysLogger("删除配置属性")
    @DeleteMapping("/option/{optionId}")
    public Result deleteOption(@PathVariable("optionId") Integer optionId) {
        int count = sysConfigService.deleteBatchOption(optionId);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_DELETE_FAILURE);
    }
}

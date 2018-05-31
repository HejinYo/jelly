package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.SysLog;
import cn.hejinyo.jelly.modules.sys.service.SysLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/16 22:14
 */
@RestController
@RequestMapping("/log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 获得一个日志信息
     */
    @GetMapping(value = "/{sysLogId}")
    @RequiresPermissions("log:view")
    public Result get(@PathVariable(value = "sysLogId") Integer sysLogId) {
        SysLog sysLog = sysLogService.findOne(sysLogId);
        if (sysLog == null) {
            return Result.error("日志不存在");
        }
        return Result.ok(sysLog);
    }

    /**
     * 分页查询日志信息
     */
    @GetMapping(value = "/listPage")
    @RequiresPermissions("log:view")
    public Result list(@RequestParam HashMap<String, Object> param) {
        PageInfo<SysLog> sysLogPageInfo = new PageInfo<>(sysLogService.findPage(PageQuery.build(param)));
        return Result.ok(sysLogPageInfo);
    }

    /**
     * 增加一个日志
     */
    @SysLogger("增加日志")
    @PostMapping
    @RequiresPermissions("log:create")
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysLog sysLog) {
        int result = sysLogService.save(sysLog);
        if (result == 0) {
            return Result.error();
        }
        return Result.ok();
    }

    /**
     * 更新一个日志
     */
    @SysLogger("更新日志")
    @RequiresPermissions("log:update")
    @PutMapping(value = "/{sysLogId}")
    public Result update(@Validated(RestfulValid.PUT.class) @RequestBody SysLog sysLog, @PathVariable("sysLogId") Integer sysLogId) {
        sysLog.setId(sysLogId);
        int result = sysLogService.update(sysLog);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error("未作任何修改");
    }

    /**
     * 删除
     */
    @SysLogger("删除日志")
    @RequiresPermissions("log:delete")
    @DeleteMapping(value = "/{sysLogIdList}")
    public Result delete(@PathVariable("sysLogIdList") Integer[] ids) {
        int result = sysLogService.deleteBatch(ids);
        if (result > 0) {
            return Result.ok("删除成功");
        }
        return Result.error("删除失败");
    }
}

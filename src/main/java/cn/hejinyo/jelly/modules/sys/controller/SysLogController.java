package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.modules.sys.model.SysLogEntity;
import cn.hejinyo.jelly.modules.sys.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/16 22:14
 */
@RestController
@RequestMapping("/sys/log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 获得一个日志信息
     */
    @GetMapping(value = "/{id}")
    public Result get(@PathVariable(value = "id") Integer id) {
        SysLogEntity sysLog = sysLogService.findOne(id);
        if (sysLog != null) {
            return Result.ok(sysLog);
        }
        return Result.error(StatusCode.DATABASE_SELECT_FAILURE);
    }

    /**
     * 分页查询日志信息
     */
    @RequestMapping(value = "/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public Result list(@RequestParam HashMap<String, Object> pageParam, @RequestBody(required = false) HashMap<String, Object> queryParam) {
        PageInfo<SysLogEntity> sysLogPageInfo = new PageInfo<>(sysLogService.findPage(PageQuery.build(pageParam, queryParam)));
        return Result.ok(sysLogPageInfo);
    }

    /**
     * 删除
     */
    @SysLogger("删除日志")
    @DeleteMapping(value = "/{ids}")
    public Result delete(@PathVariable("ids") Integer[] ids) {
        int result = sysLogService.deleteBatch(ids);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_DELETE_FAILURE);
    }
}

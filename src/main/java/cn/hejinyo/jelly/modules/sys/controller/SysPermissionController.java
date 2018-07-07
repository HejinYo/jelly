package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.SysPermissionEntity;
import cn.hejinyo.jelly.modules.sys.service.SysPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date 2017/9/17 16:46
 */
@Api(tags = "权限管理", description = "SysPermissionController")
@RestController
@RequestMapping("/sys/permission")
public class SysPermissionController extends BaseController {

    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 授权树
     */
    @GetMapping("/authTree")
    public Result authTree() {
        return Result.ok(sysPermissionService.getAuthTree(false));
    }

    /**
     * 获得一个权限信息
     */
    @ApiOperation(value = "权限信息", notes = "权限信息")
    @GetMapping(value = "/{permId}")
    @RequiresPermissions("sys:permission:view")
    public Result get(@PathVariable(value = "permId") int permId) {
        SysPermissionEntity sysPermission = sysPermissionService.findOne(permId);
        if (sysPermission != null) {
            return Result.ok(sysPermission);
        }
        return Result.error(StatusCode.DATABASE_SELECT_FAILURE);
    }

    /**
     * 分页查询
     */
    @ApiOperation(value = "分页查询权限信息", notes = "支持分页，排序和高级查询")
    @GetMapping(value = "/listPage")
    @RequiresPermissions("sys:permission:view")
    public Result list(@RequestParam HashMap<String, Object> paramers) {
        PageInfo<SysPermissionEntity> userPageInfo = new PageInfo<>(sysPermissionService.findPage(PageQuery.build(paramers)));
        return Result.ok(userPageInfo);
    }

    /**
     * 增加
     */
    @SysLogger("增加权限")
    @ApiOperation(value = "增加一个权限", notes = "增加一个权限")
    @PostMapping
    @RequiresPermissions("sys:permission:save")
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysPermissionEntity sysPermission) {
        int result = sysPermissionService.save(sysPermission);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_SAVE_FAILURE);
    }

    /**
     * 更新
     */
    @SysLogger("更新权限")
    @ApiOperation(value = "更新权限信息", notes = "更新权限详细信息")
    @PutMapping(value = "/{permId}")
    @RequiresPermissions("sys:permission:update")
    public Result update(@PathVariable("permId") int permId, @Validated(RestfulValid.PUT.class) @RequestBody SysPermissionEntity sysPermission) {
        int result = sysPermissionService.update(permId, sysPermission);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_UPDATE_FAILURE);
    }

    /**
     * 删除
     */
    @SysLogger("删除权限")
    @ApiOperation(value = "删除权限", notes = "删除权限：/delete/1,2,3,4")
    @DeleteMapping(value = "/{permId}")
    @RequiresPermissions("sys:permission:delete")
    public Result delete(@PathVariable("permId") int permId) {
        int result = sysPermissionService.delete(permId);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_DELETE_FAILURE);
    }

}

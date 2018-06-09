package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.SysPermissionEntity;
import cn.hejinyo.jelly.modules.sys.service.SysPermissionService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date 2017/9/17 16:46
 */
@RestController
@RequestMapping("/permission")
public class SysPermissionController extends BaseController {

    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 获得一个权限信息
     */
    @GetMapping(value = "/{permId}")
    @RequiresPermissions("resource:view")
    public Result get(@PathVariable(value = "permId") int permId) {
        SysPermissionEntity sysPermission = sysPermissionService.findOne(permId);
        if (sysPermission == null) {
            return Result.error("资源权限不存在");
        }
        return Result.ok(sysPermission);
    }

    /**
     * 分页查询
     */
    @GetMapping(value = "/listPage")
    @RequiresPermissions("resource:view")
    public Result list(@RequestParam HashMap<String, Object> paramers) {
        PageInfo<SysPermissionEntity> userPageInfo = new PageInfo<>(sysPermissionService.findPage(PageQuery.build(paramers)));
        return Result.ok(userPageInfo);
    }

    /**
     * 增加
     */
    @SysLogger("增加权限")
    @PostMapping
    @RequiresPermissions("resource:create")
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysPermissionEntity sysPermission) {
        if (sysPermission.getResId() == 0) {
            return Result.error("资源根节点不允许添加权限");
        }
        if (sysPermissionService.isExist(sysPermission)) {
            return Result.error("资源权限已经存在");
        }
        int result = sysPermissionService.save(sysPermission);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error();
    }

    /**
     * 更新
     */
    @SysLogger("更新权限")
    @PutMapping(value = "/{permId}")
    @RequiresPermissions("resource:update")
    public Result update(@Validated(RestfulValid.PUT.class) @RequestBody SysPermissionEntity sysPermission, @PathVariable("permId") int permId) {
        sysPermission.setPermId(permId);
        int result = sysPermissionService.update(sysPermission);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error("未作任何修改");
    }

    /**
     * 删除
     */
    @SysLogger("删除权限")
    @DeleteMapping(value = "/{permId}")
    @RequiresPermissions("resource:delete")
    public Result delete(@PathVariable("permId") int permId) {
        SysPermissionEntity sysPermission = sysPermissionService.findOne(permId);
        if (sysPermission == null) {
            return Result.error("资源权限不存在");
        }
        int result = sysPermissionService.delete(sysPermission.getPermId());
        if (result > 0) {
            return Result.ok("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 获得角色已授权权限
     */
    @GetMapping("/granted/{roleId}")
    public Result grantedPermission(@PathVariable("roleId") int roleId) {
        return Result.ok(sysPermissionService.getRolePermissionSet(roleId));
    }

    /**
     * 授权树
     */
    @GetMapping("/authTree")
    public Result authTree() {
        return Result.ok(sysPermissionService.getResourcePermissionTree());
    }


}

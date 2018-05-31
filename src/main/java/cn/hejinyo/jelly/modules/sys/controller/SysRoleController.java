package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.SysRole;
import cn.hejinyo.jelly.modules.sys.model.dto.RolePermissionTreeDTO;
import cn.hejinyo.jelly.modules.sys.service.SysRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date 2017/9/27 19:03
 */
@RestController
@RequestMapping("/role")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 获得一个角色信息
     */
    @GetMapping(value = "/{roleId}")
    @RequiresPermissions("role:view")
    public Result get(@PathVariable(value = "roleId") Integer roleId) {
        SysRole sysRole = sysRoleService.findOne(roleId);
        if (sysRole == null) {
            return Result.error("角色不存在");
        }
        return Result.ok(sysRole);
    }

    /**
     * 分页查询角色列表
     */
    @GetMapping(value = "/listPage")
    @RequiresPermissions("role:view")
    public Result list(@RequestParam HashMap<String, Object> paramers) {
        PageInfo<SysRole> rolePageInfo = new PageInfo<>(sysRoleService.findPage(PageQuery.build(paramers)));
        return Result.ok(rolePageInfo);
    }

    /**
     * 增加
     */
    @SysLogger("增加角色")
    @PostMapping
    @RequiresPermissions("role:create")
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysRole sysRole) {
        int result = sysRoleService.save(sysRole);
        if (result == 0) {
            return Result.error();
        }
        return Result.ok();
    }

    /**
     * 更新
     */
    @SysLogger("更新角色")
    @PutMapping(value = "/{roleId}")
    @RequiresPermissions("role:update")
    public Result update(@Validated(RestfulValid.PUT.class) @RequestBody SysRole sysRole, @PathVariable("roleId") Integer roleId) {
        sysRole.setRoleId(roleId);
        int result = sysRoleService.update(sysRole);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error("未作任何修改");
    }

    /**
     * 删除
     */
    @SysLogger("删除角色")
    @DeleteMapping(value = "/{roleIdList}")
    @RequiresPermissions("role:delete")
    public Result delete(@PathVariable("roleIdList") Integer[] ids) {
        int result = sysRoleService.deleteBatch(ids);
        if (result > 0) {
            return Result.ok("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 授权
     */
    @SysLogger("角色授权")
    @PostMapping(value = "/authorization/{roleId}")
    @RequiresPermissions("role:auth")
    public Result operationPermission(@PathVariable("roleId") Integer roleId, @RequestBody List<RolePermissionTreeDTO> rolePermissionList) {
        int result = sysRoleService.operationPermission(roleId, rolePermissionList);
        //新增加授权
        if (result > 0) {
            return Result.ok("授权成功");
        }
        return Result.error("没有修改任何权限");
    }

    /**
     * 角色列表下拉选择select
     */
    @GetMapping(value = "/select")
    public Result roleSelect() {
        return Result.ok(sysRoleService.roleSelect());
    }

}

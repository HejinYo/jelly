package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.SysRole;
import cn.hejinyo.jelly.modules.sys.model.dto.RolePermissionTreeDTO;
import cn.hejinyo.jelly.modules.sys.model.dto.RoleResourceDTO;
import cn.hejinyo.jelly.modules.sys.service.SysRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
     * 分页查询角色列表
     */
    @GetMapping(value = "/listPage")
    @RequiresPermissions("role:view")
    public Result list(@RequestParam HashMap<String, Object> paramers) {
        PageInfo<SysRole> rolePageInfo = new PageInfo<>(sysRoleService.findPage(PageQuery.build(paramers)));
        return Result.ok(rolePageInfo);
    }

    /**
     * 查询角色权限
     */
    @GetMapping(value = "/roleResourceListPage")
    @RequiresPermissions("role:view")
    public Result roleResourceList(@RequestParam HashMap<String, Object> paramers) {
        PageInfo<RoleResourceDTO> roleResourcePageInfo = new PageInfo<>(sysRoleService.findPageForRoleResource(PageQuery.build(paramers)));
        return Result.ok(roleResourcePageInfo);
    }

    /**
     * 增加
     */
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
    @DeleteMapping(value = "/{roleId}")
    @RequiresPermissions("role:delete")
    public Result delete(@PathVariable("roleId") Integer roleId) {
        SysRole SysRole = sysRoleService.findOne(roleId);
        if (SysRole == null) {
            return Result.error("资源不存在");
        }
        int result = sysRoleService.delete(SysRole.getRoleId());
        if (result > 0) {
            return Result.ok("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 授权
     */
    @PostMapping(value = "/authorization/{roleId}")
    @Transactional
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

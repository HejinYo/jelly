package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.SysRoleEntity;
import cn.hejinyo.jelly.modules.sys.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date 2017/9/27 19:03
 */
@Api(tags = "角色管理", description = "SysRoleController")
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;


    /**
     * 角色列表下拉选择
     */
    @GetMapping(value = "/dropList")
    @ApiOperation(value = "角色列表下拉选择", notes = "角色列表下拉选择")
    public Result roleSelect() {
        return Result.ok(sysRoleService.getDropList());
    }

    /**
     * 分页查询角色列表
     */
    @ApiOperation(value = "分页查询角色列表", notes = "分页查询角色列表")
    @RequestMapping(value = "/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "pageParam", value = "分页查询参数", required = true, dataType = "object"),
    })
    public Result list(@RequestParam HashMap<String, Object> pageParam, @RequestBody(required = false) HashMap<String, Object> queryParam) {
        //查询列表数据
        PageInfo<SysRoleEntity> rolePageInfo = new PageInfo<>(sysRoleService.findPage(PageQuery.build(pageParam, queryParam)));
        return Result.ok(rolePageInfo);
    }

    /**
     * 获得一个角色信息
     */
    @ApiOperation(value = "查询角色信息", notes = "查询角色信息")
    @ApiImplicitParam(paramType = "path", name = "roleId", value = "角色ID", required = true, dataType = "int")
    @GetMapping(value = "/{roleId}")
    public Result get(@PathVariable(value = "roleId") Integer roleId) {
        SysRoleEntity sysRole = sysRoleService.findOne(roleId);
        if (sysRole != null) {
            return Result.ok(sysRole);
        }
        return Result.error(StatusCode.DATABASE_SELECT_FAILURE);
    }

    /**
     * 增加
     */
    @SysLogger("增加角色")
    @ApiOperation(value = "增加角色", notes = "增加角色")
    @ApiImplicitParam(paramType = "body", name = "SysRoleEntity", value = "角色参数", required = true, dataType = "SysRoleEntity")
    @PostMapping
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysRoleEntity sysRole) {
        int result = sysRoleService.save(sysRole);
        if (result > 0) {
            return Result.ok(result);
        }
        return Result.error(StatusCode.DATABASE_SAVE_FAILURE);
    }

    /**
     * 更新
     */
    @SysLogger("修改角色")
    @ApiOperation(value = "修改角色", notes = "修改角色")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "SysRoleEntity", value = "角色详细实体", required = true, dataType = "SysRoleEntity"),
            @ApiImplicitParam(paramType = "path", name = "roleId", value = "角色ID", required = true, dataType = "roleId")
    })
    @PutMapping(value = "/{roleId}")
    public Result update(@PathVariable("roleId") Integer roleId, @Validated(RestfulValid.PUT.class) @RequestBody SysRoleEntity sysRole) {
        int count = sysRoleService.update(roleId, sysRole);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_UPDATE_FAILURE);
    }

    /**
     * 删除
     */
    @SysLogger("删除角色")
    @DeleteMapping(value = "/{roleIds}")
    @ApiOperation(value = "删除角色", notes = "删除角色，支持批量删除\n 示例： /1,2,3,4,5")
    @ApiImplicitParam(paramType = "path", name = "roleIds", value = "角色ID数组", required = true, dataType = "array")
    public Result delete(@PathVariable("roleIds") Integer[] roleIds) {
        int count = sysRoleService.deleteBatch(roleIds);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_DELETE_FAILURE);
    }

}

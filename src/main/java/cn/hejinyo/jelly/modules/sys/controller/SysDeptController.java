package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.SysDeptEntity;
import cn.hejinyo.jelly.modules.sys.service.SysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/13 20:22
 */
@Api(tags = "部门管理", description = "SysDeptController")
@RestController
@RequestMapping("/sys/dept")
public class SysDeptController {

    @Autowired
    private SysDeptService sysDeptService;

    /**
     * 部门管理树数据
     */
    @ApiOperation(value = "部门管理树数据", notes = "用户部门包含子部门")
    @GetMapping("/operateTree")
    @RequiresPermissions("sys:dept:view")
    public Result operateTree() {
        //显示根节点
        return Result.ok(sysDeptService.getDeptListTree(false, true));
    }

    /**
     * 部门选择树数据
     */
    @ApiOperation(value = "部门选择树数据", notes = "用户部门包含子部门")
    @GetMapping("/select")
    public Result select() {
        return Result.ok(sysDeptService.getDeptListTree(true, false));
    }

    /**
     * 分页查询部门列表
     */
    @ApiOperation(value = "分页查询部门列表", notes = "分页查询部门列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "pageParam", value = "分页查询参数", required = true, dataType = "object"),
    })
    @RequiresPermissions("sys:dept:view")
    @RequestMapping(value = "/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public Result list(@RequestParam HashMap<String, Object> pageParam, @RequestBody(required = false) HashMap<String, Object> queryParam) {
        //查询列表数据
        PageInfo<SysDeptEntity> deptPageInfo = new PageInfo<>(sysDeptService.findPage(PageQuery.build(pageParam, queryParam)));
        return Result.ok(deptPageInfo);
    }

    /**
     * 查询部门信息
     */
    @ApiOperation(value = "查询部门信息", notes = "查询部门信息")
    @ApiImplicitParam(paramType = "path", name = "deptId", value = "部门ID", required = true, dataType = "int")
    @GetMapping("/{deptId}")
    @RequiresPermissions("sys:dept:view")
    public Result info(@PathVariable("deptId") Integer deptId) {
        SysDeptEntity dept = sysDeptService.findOne(deptId);
        if (dept != null) {
            return Result.ok(dept);
        }
        return Result.error(StatusCode.DATABASE_SELECT_FAILURE);
    }

    /**
     * 添加部门
     */
    @SysLogger("添加部门")
    @ApiOperation(value = "添加部门", notes = "添加部门")
    @ApiImplicitParam(paramType = "body", name = "SysDeptEntity", value = "部门参数", required = true, dataType = "SysDeptEntity")
    @PostMapping
    @RequiresPermissions("sys:dept:save")
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysDeptEntity dept) {
        int result = sysDeptService.save(dept);
        if (result > 0) {
            return Result.ok(result);
        }
        return Result.error(StatusCode.DATABASE_SAVE_FAILURE);
    }

    /**
     * 修改部门
     */
    @SysLogger("修改部门")
    @ApiOperation(value = "修改部门", notes = "修改部门")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "SysDeptEntity", value = "部门详细实体", required = true, dataType = "SysDeptEntity"),
            @ApiImplicitParam(paramType = "path", name = "deptId", value = "部门ID", required = true, dataType = "Integer")
    })
    @PutMapping(value = "/{deptId}")
    @RequiresPermissions("sys:dept:update")
    public Result update(@PathVariable("deptId") Integer deptId, @Validated(RestfulValid.PUT.class) @RequestBody SysDeptEntity dept) {
        int count = sysDeptService.update(deptId, dept);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_UPDATE_FAILURE);
    }

    /**
     * 删除部门
     */
    @SysLogger("删除部门")
    @ApiOperation(value = "删除部门", notes = "删除部门\n 示例： /1")
    @ApiImplicitParam(paramType = "path", name = "deptIds", value = "部门ID", required = true, dataType = "Integer")
    @DeleteMapping("/{deptId}")
    @RequiresPermissions("sys:dept:delete")
    public Result delete(@PathVariable("deptId") Integer  deptId) {
        int count = sysDeptService.delete(deptId);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_DELETE_FAILURE);
    }
    
    /**
     * 节点排序,改变父节点
     */
    @SysLogger("节点拖动")
    @ApiOperation(value = "节点拖动：节点排序,改变父节点",
            notes = "/{type}被拖拽节点的放置位置（before、after、inner}" +
                    "/{deptId}被拖拽节点ID" +
                    "/{inDeptId}进入节点ID")
    @PutMapping(value = "/drop/{location}/{deptId}/{inDeptId}")
    @RequiresPermissions("sys:dept:update")
    public Result nodeDrop(@PathVariable("location") String location, @PathVariable("deptId") Integer deptId, @PathVariable("inDeptId") Integer inDeptId) {
        int result = sysDeptService.nodeDrop(location, deptId, inDeptId);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_UPDATE_FAILURE);
    }
}

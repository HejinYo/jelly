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
    @GetMapping("/tree")
    //@RequiresPermissions("sys:dept:list")
    public Result editTree() {
        //显示根节点
        return Result.ok(sysDeptService.getDeptListTree(false, true));
    }

    /**
     * 部门选择树数据
     */
    @ApiOperation(value = "部门选择树数据", notes = "用户部门包含子部门")
    @GetMapping("/select")
    public Result selectTree() {
        return Result.ok(sysDeptService.getDeptListTree(true, true));
    }

    /**
     * 分页查询部门列表
     */
    @ApiOperation(value = "分页查询部门列表", notes = "分页查询部门列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "qyery", name = "pageParam", value = "分页查询参数", required = true, dataType = "object"),
    })
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
    @ApiOperation(value = "删除部门", notes = "删除部门，支持批量删除\n 示例： /1,2,3,4,5")
    @ApiImplicitParam(paramType = "path", name = "deptIds", value = "部门ID数组", required = true, dataType = "array")
    @DeleteMapping("/{deptIds}")
    public Result delete(@PathVariable("deptIds") Integer[] deptIds) {
        int count = sysDeptService.deleteBatch(deptIds);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_DELETE_FAILURE);
    }
}

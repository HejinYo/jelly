package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.SysResourceEntity;
import cn.hejinyo.jelly.modules.sys.service.SysResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date 2017/9/26 11:00
 */
@Api(tags = "资源管理", description = "SysResourceController")
@RestController
@RequestMapping("/sys/resource")
public class SysResourceController extends BaseController {

    @Autowired
    private SysResourceService sysResourceService;

    /**
     * 资源选择树数据
     */
    @ApiOperation(value = "资源选择树数据", notes = "资源选择树数据")
    @GetMapping("/selectTree")
    public Result selectTree() {
        return Result.ok(sysResourceService.getResourceListTree(true, true));
    }

    /**
     * 资源管理树数据
     */
    @ApiOperation(value = "资源管理树数据", notes = "资源管理树数据")
    @GetMapping("/operateTree")
    @RequiresPermissions("sys:resource:view")
    public Result editTree() {
        return Result.ok(sysResourceService.getResourceListTree(false, true));
    }

    /**
     * 获得一个资源信息
     */
    @ApiOperation(value = "资源信息", notes = "资源信息")
    @ApiImplicitParam(paramType = "path", name = "resId", value = "资源ID", required = true, dataType = "int")
    @GetMapping(value = "/{resId}")
    @RequiresPermissions("sys:resource:view")
    public Result get(@PathVariable(value = "resId") Integer resId) {
        SysResourceEntity sysResource = sysResourceService.findOne(resId);
        if (sysResource != null) {
            return Result.ok(sysResource);
        }
        return Result.error(StatusCode.DATABASE_SELECT_FAILURE);
    }

    /**
     * 分页查询
     */
    @ApiOperation(value = "分页查询资源信息", notes = "支持分页，排序和高级查询")
    @RequestMapping(value = "/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    @RequiresPermissions("sys:resource:view")
    public Result list(@RequestParam HashMap<String, Object> pageParam, @RequestBody(required = false) HashMap<String, Object> queryParam) {
        PageInfo<SysResourceEntity> resourcePageInfo = new PageInfo<>(sysResourceService.findPage(PageQuery.build(pageParam, queryParam)));
        return Result.ok(resourcePageInfo);
    }

    /**
     * 增加
     */
    @SysLogger("增加资源")
    @ApiOperation(value = "增加一个资源", notes = "增加一个资源")
    @PostMapping
    @RequiresPermissions("sys:resource:save")
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysResourceEntity sysResource) {
        int result = sysResourceService.save(sysResource);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_SAVE_FAILURE);
    }

    /**
     * 更新
     */
    @SysLogger("更新资源")
    @ApiOperation(value = "更新资源信息", notes = "更新资源详细信息")
    @PutMapping(value = "/{resId}")
    @RequiresPermissions("sys:resource:update")
    public Result update(@PathVariable("resId") Integer resId, @Validated(RestfulValid.PUT.class) @RequestBody SysResourceEntity sysResource) {
        int result = sysResourceService.update(resId, sysResource);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_UPDATE_FAILURE);
    }

    /**
     * 删除
     */
    @SysLogger("删除资源")
    @ApiOperation(value = "删除资源", notes = "删除资源：/delete/1,2,3,4")
    @DeleteMapping(value = "/{resId}")
    @RequiresPermissions("sys:resource:delete")
    public Result delete(@PathVariable("resId") Integer resId) {
        int result = sysResourceService.delete(resId);
        if (result > 0) {
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
                    "/{resId}被拖拽节点ID" +
                    "/{inResId}进入节点ID")
    @PutMapping(value = "/drop/{location}/{resId}/{inResId}")
    @RequiresPermissions("sys:resource:update")
    public Result nodeDrop(@PathVariable("location") String location, @PathVariable("resId") Integer resId, @PathVariable("inResId") Integer inResId) {
        int result = sysResourceService.nodeDrop(location, resId, inResId);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_UPDATE_FAILURE);
    }

}

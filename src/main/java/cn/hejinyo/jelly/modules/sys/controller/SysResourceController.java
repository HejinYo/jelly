package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.annotation.SysLogger;
import cn.hejinyo.jelly.modules.sys.model.SysResource;
import cn.hejinyo.jelly.modules.sys.service.SysResourceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date 2017/9/26 11:00
 */
@RestController
@RequestMapping("/resource")
public class SysResourceController extends BaseController {
    @Autowired
    private SysResourceService sysResourceService;

    /**
     * 获得一个资源信息
     */
    @GetMapping(value = "/{roleId}")
    @RequiresPermissions("role:view")
    public Result get(@PathVariable(value = "roleId") Integer roleId) {
        SysResource sysResource = sysResourceService.findOne(roleId);
        if (sysResource == null) {
            return Result.error("资源不存在");
        }
        return Result.ok(sysResource);
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "/listPage")
    @RequiresPermissions("resource:view")
    public Result list(@RequestParam HashMap<String, Object> pageParam) {
        PageInfo<SysResource> resourcePageInfo = new PageInfo<>(sysResourceService.findPage(PageQuery.build(pageParam)));
        return Result.ok(resourcePageInfo);
    }

    /**
     * 所有资源树
     */
    @GetMapping("/tree")
    @RequiresPermissions("resource:view")
    public Result test() {
        return Result.ok(sysResourceService.getRecursionTree());
    }

    /**
     * 增加
     */
    @PostMapping
    @Transactional
    @RequiresPermissions("resource:create")
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysResource sysResource) {
        if (sysResourceService.isExistResCode(sysResource.getResCode())) {
            return Result.error("资源编码已经存在");
        }
        int result = sysResourceService.save(sysResource);
        if (result == 0) {
            return Result.error();
        }
        return Result.ok();
    }

    /**
     * 更新
     */
    @PutMapping(value = "/{resId}")
    @Transactional
    @RequiresPermissions("resource:update")
    public Result update(@Validated(RestfulValid.PUT.class) @RequestBody SysResource sysResource, @PathVariable("resId") Integer resId) {
        if (resId == 0 && sysResource.getResPid() != -1) {
            return Result.error("资源根节点不允许修改所属资源");
        }
        sysResource.setResId(resId);
        int result = sysResourceService.update(sysResource);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error("未作任何修改");
    }

    /**
     * 删除
     */
    @SysLogger("删除资源")
    @DeleteMapping(value = "/{resId}")
    @RequiresPermissions("resource:delete")
    @Transactional
    public Result delete(@PathVariable("resId") Integer resId) {
        if (resId == 0) {
            return Result.error("资源根节点不允许删除");
        }
        int result = sysResourceService.delete(resId);
        if (result > 0) {
            return Result.ok("删除成功");
        }
        return Result.error("删除失败");
    }

}

package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.SysResource;
import cn.hejinyo.jelly.modules.sys.model.dto.ResourceTreeDTO;
import cn.hejinyo.jelly.modules.sys.service.SysResourceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 分页查询
     */
    @GetMapping(value = "/listPage")
    @RequiresPermissions("resource:view")
    public Result list(@RequestParam HashMap<String, Object> paramers) {
        PageInfo<SysResource> resourcePageInfo = new PageInfo<>(sysResourceService.findPage(new PageQuery(paramers)));
        return Result.ok(resourcePageInfo);
    }

    /**
     * 所有资源树
     */
    @GetMapping("/tree")
    @RequiresPermissions("resource:view")
    public Result test() {
        List<ResourceTreeDTO> list = sysResourceService.getRecursionTree();
        return Result.ok(list);
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
    @DeleteMapping(value = "/{resId}")
    @Transactional
    @RequiresPermissions("resource:delete")
    public Result delete(@PathVariable("resId") Integer resId) {
        SysResource sysResource = sysResourceService.findOne(resId);
        if (sysResource == null) {
            return Result.error("资源不存在");
        }
        int result = sysResourceService.delete(sysResource.getResId());
        if (result > 0) {
            return Result.ok("删除成功");
        }
        return Result.error("删除失败");
    }

}

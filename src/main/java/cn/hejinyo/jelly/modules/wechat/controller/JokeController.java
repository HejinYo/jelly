package cn.hejinyo.jelly.modules.wechat.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.wechat.model.WechatJoke;
import cn.hejinyo.jelly.modules.wechat.service.WechatJokeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/13 10:48
 * @Description :
 */
@RestController
@RequestMapping(value = "/joke")
public class JokeController {

    @Autowired
    private WechatJokeService wechatJokeService;

    @RequestMapping("/findOne")
    public Result joke() {
        return Result.ok(wechatJokeService.getRandomWechatJoke());
    }

    /**
     * 获得一个笑话信息
     */
    @GetMapping(value = "/{sysConfigId}")
    @RequiresPermissions("joke:view")
    public Result get(@PathVariable(value = "sysConfigId") Integer id) {
        WechatJoke sysConfig = wechatJokeService.findOne(id);
        if (sysConfig == null) {
            return Result.error("笑话不存在");
        }
        return Result.ok(sysConfig);
    }

    /**
     * 分页查询笑话信息
     */
    @RequestMapping("/listPage")
    @RequiresPermissions("joke:view")
    public Result list(@RequestParam Map<String, Object> param) {
        //查询列表数据
        PageInfo<WechatJoke> sysConfigPageInfo = new PageInfo<>(wechatJokeService.findPage(PageQuery.build(param)));
        return Result.ok(sysConfigPageInfo);
    }

    /**
     * 保存笑话
     */
    @SysLogger("增加笑话")
    @PostMapping
    @RequiresPermissions("joke:create")
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody WechatJoke config) {
        wechatJokeService.save(config);
        return Result.ok();
    }

    /**
     * 修改笑话
     */
    @SysLogger("修改笑话")
    @PutMapping(value = "/{sysConfigId}")
    @RequiresPermissions("joke:update")
    public Result update(@Validated(RestfulValid.PUT.class) @RequestBody WechatJoke sysConfig, @PathVariable("sysConfigId") Integer sysConfigId) {
        sysConfig.setId(sysConfigId);
        int result = wechatJokeService.update(sysConfig);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error("未作任何修改");
    }

    /**
     * 删除笑话
     */
    @SysLogger("删除笑话")
    @RequiresPermissions("joke:delete")
    @DeleteMapping(value = "/{sysConfigIdList}")
    public Result delete(@PathVariable("sysConfigIdList") Integer[] ids) {
        int result = wechatJokeService.deleteBatch(ids);
        if (result > 0) {
            return Result.ok("删除成功");
        }
        return Result.error("删除失败");
    }

}

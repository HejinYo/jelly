package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.oss.cloud.OSSFactory;
import cn.hejinyo.jelly.modules.sys.annotation.SysLogger;
import cn.hejinyo.jelly.modules.sys.model.SysUser;
import cn.hejinyo.jelly.modules.sys.service.SysUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 22:29
 */
@RestController
@RequestMapping("/user")
public class SysUserController extends BaseController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 获得一个用户信息
     */
    @GetMapping(value = "/{userId}")
    @RequiresPermissions("user:view")
    public Result get(@PathVariable(value = "userId") Integer userId) {
        SysUser sysUser = sysUserService.findOne(userId);
        if (sysUser == null) {
            return Result.error("用户不存在");
        }
        return Result.ok(sysUser);
    }

    /**
     * 分页查询用户信息
     */
    @GetMapping(value = "/listPage")
    @RequiresPermissions("user:view")
    public Result list(@RequestParam HashMap<String, Object> paramers) {
        PageInfo<SysUser> userPageInfo = new PageInfo<>(sysUserService.findPage(PageQuery.build(paramers)));
        return Result.ok(userPageInfo);
    }

    /**
     * 增加一个用户
     */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    @RequiresPermissions("user:create")
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysUser sysUser) {
        if (sysUserService.isExistUserName(sysUser.getUserName())) {
            return Result.error("用户名已经存在");
        }
        int result = sysUserService.save(sysUser);
        if (result == 0) {
            return Result.error();
        }
        return Result.ok();
    }

    /**
     * 用户名是否已经存在
     */
    @RequestMapping(value = "/isExistUserName/{userName}", method = RequestMethod.GET)
    public Result isExistUserName(@PathVariable("userName") String userName) {
        if (sysUserService.isExistUserName(userName)) {
            return Result.error("用户名已经存在");
        }
        return Result.ok();
    }

    /**
     * 更新一个用户
     */
    @SysLogger("更新用户")
    @Transactional(rollbackFor = Exception.class)
    @RequiresPermissions("user:update")
    @PutMapping(value = "/{userId}")
    public Result update(@Validated(RestfulValid.PUT.class) @RequestBody SysUser sysUser, @PathVariable("userId") Integer userId) {
        if (1 == userId && getUserId() != Constant.SUPER_ADMIN) {
            return Result.error("admin不允许修改");
        }
        sysUser.setUserId(userId);
        int result = sysUserService.update(sysUser);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error("未作任何修改");
    }

    /**
     * 删除
     */
    @SysLogger("删除用户")
    @RequiresPermissions("user:delete")
    @DeleteMapping(value = "/{userIdList}")
    @Transactional(rollbackFor = Exception.class)
    public Result delete(@PathVariable("userIdList") Integer[] ids) {
        for (int userId : ids) {
            if (Constant.SUPER_ADMIN == userId) {
                return Result.error("admin不允许被删除");
            }
        }
        int result = sysUserService.deleteBatch(ids);
        if (result > 0) {
            return Result.ok("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 头像上传
     */
    @PostMapping(value = "/avatar")
    public Result avatarUpload(@RequestParam("file") MultipartFile file) {
        // 获得原始文件名
        String fileName = file.getOriginalFilename();
        System.out.println("fileName:" + fileName);
        String key = "avatar/" + getCurrentUser().getUserName() + ".png";
        if (!file.isEmpty()) {
            try {
                String result = OSSFactory.build(key).upload(file.getInputStream(), key);
                return Result.ok(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.error("上传失败");
    }
}

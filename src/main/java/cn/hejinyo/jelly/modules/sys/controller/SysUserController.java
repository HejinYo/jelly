package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.oss.cloud.OSSFactory;
import cn.hejinyo.jelly.modules.sys.model.SysUser;
import cn.hejinyo.jelly.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/17 22:29
 */
@Api(tags = "用户管理", description = "SysUserController")
@RestController
@RequestMapping("/user")
public class SysUserController extends BaseController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 获得一个用户信息
     */
    @ApiOperation(value = "获得一个用户信息", notes = "根据路徑參數来获得一个用户信息")
    @ApiImplicitParam(paramType = "path", name = "userId", value = "用户ID", required = true, dataType = "int")
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
    @ApiOperation(value = "分页查询用户信息", notes = "支持分页，排序和查询；POST请求高级查询，GET请求普通查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "qyery", name = "pageParam", value = "分页查询参数", required = true, dataType = "object"),
    })
    @GetMapping(value = "/listPage")
    @RequiresPermissions("user:view")
    public Result getList(@RequestParam HashMap<String, Object> pageParam) {
        PageInfo<SysUser> userPageInfo = new PageInfo<>(sysUserService.findPage(PageQuery.build(pageParam)));
        return Result.ok(userPageInfo);
    }

    @ApiOperation(value = "分页查询用户信息", notes = "支持分页，排序和查询；POST请求高级查询，GET请求普通查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "qyery", name = "pageParam", value = "分页查询参数", required = true, dataType = "object"),
            @ApiImplicitParam(paramType = "body", name = "queryParam", value = "高级查询参数", dataType = "object")
    })
    @PostMapping(value = "/listPage")
    @RequiresPermissions("user:view")
    public Result postList(@RequestParam HashMap<String, Object> pageParam, @RequestBody(required = false) HashMap<String, Object> queryParam) {
        PageInfo<SysUser> userPageInfo = new PageInfo<>(sysUserService.findPage(PageQuery.build(pageParam, queryParam)));
        return Result.ok(userPageInfo);
    }

    /**
     * 增加一个用户
     */
    @ApiOperation(value = "增加一个用户", notes = "增加一个用户")
    @ApiImplicitParam(paramType = "body", name = "sysUser", value = "用户参数", required = true, dataType = "SysUser")
    @PostMapping
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
    @ApiOperation(value = "用户名是否已经存在", notes = "用户名是否已经存在")
    @ApiImplicitParam(paramType = "path", name = "userName", value = "用户名", required = true, dataType = "String")
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
    @ApiOperation(value = "更新用户详细信息", notes = "根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "sysUser", value = "用户详细实体user", required = true, dataType = "SysUser"),
            @ApiImplicitParam(paramType = "path", name = "userId", value = "用户ID", required = true, dataType = "Integer")
    })
    @SysLogger("更新用户")
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
    @ApiOperation(value = "删除用户", notes = "删除用户")
    @ApiImplicitParam(paramType = "path", name = "userIdList", value = "用户ID数组", required = true, dataType = "array")
    @SysLogger("删除用户")
    @RequiresPermissions("user:delete")
    @DeleteMapping(value = "/{userIdList}")
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
     * 查询个人信息
     */
    @GetMapping(value = "/info")
    public Result getUserInfo() {
        return Result.ok(sysUserService.findOne(getUserId()));
    }

    /**
     * 修改密码
     */
    @PutMapping(value = "/updatePassword")
    public Result updatePassword(@RequestBody HashMap<String, Object> param) {
        int result = sysUserService.updatePassword(param);
        if (result > 0) {
            sysUserService.updateUserRedisInfo();
            return Result.ok("密码修改成功");
        }
        return Result.error("密码修改失败");
    }

    /**
     * 修改用户信息
     */
    @PutMapping(value = "/updateUserInfo")
    public Result updateUserInfo(@RequestBody SysUser sysUser) {
        int result = sysUserService.updateUserInfo(sysUser);
        if (result > 0) {
            sysUserService.updateUserRedisInfo();
            return Result.ok("修改成功");
        }
        return Result.error("未作任何修改");
    }

    /**
     * 头像上传
     */
    @PostMapping(value = "/avatar")
    public Result avatarUpload(@RequestParam("file") MultipartFile file) {
        // 获得原始文件名
        String fileName = file.getOriginalFilename();
        System.out.println("fileName:" + fileName);
        String key = "avatar/" + getCurrentUser().getUserName() + "/" + LocalDateTime.now().toString() + ".png";
        if (!file.isEmpty()) {
            try {
                String avatarUrl = OSSFactory.build(key).upload(file.getInputStream(), key);
                SysUser sysUser = new SysUser();
                sysUser.setUserId(getUserId());
                sysUser.setAvatar(avatarUrl);
                int count = sysUserService.updateUserAvatar(sysUser);
                if (count > 0) {
                    sysUserService.updateUserRedisInfo();
                    return Result.ok(avatarUrl);
                }
                return Result.error("上传成功，但是修改失败");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.error("上传失败");
    }
}

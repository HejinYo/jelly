package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.utils.PageInfo;
import cn.hejinyo.jelly.common.utils.PageQuery;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.SysDictEntity;
import cn.hejinyo.jelly.modules.sys.model.SysDictOptionEntity;
import cn.hejinyo.jelly.modules.sys.service.SysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 数据字典管理
 *
 * @author : heshuangshuang
 * @date : 2018/4/10 17:39
 */
@Api(tags = "数据字典管理", description = "SysDictController")
@RestController
@RequestMapping("/sys/dict")
public class SysDictController {

    @Autowired
    private SysDictService sysDictService;

    /**
     * 字典目录列表
     */
    @ApiOperation(value = "字典目录列表", notes = "字典目录列表")
    @GetMapping("/list")
    public Result confiList() {
        return Result.ok(sysDictService.getDictList());
    }

    /**
     * 字典目录信息
     */
    @ApiOperation(value = "字典目录信息", notes = "字典目录信息")
    @GetMapping("/{id}")
    public Result dirInfo(@PathVariable("id") Integer id) {
        SysDictEntity dict = sysDictService.findOne(id);
        if (dict != null) {
            return Result.ok(dict);
        }
        return Result.error(StatusCode.DATABASE_SELECT_FAILURE);
    }

    /**
     * 保存字典目录
     */
    @ApiOperation(value = "保存字典目录", notes = "保存字典目录")
    @SysLogger("保存字典目录")
    @PostMapping
    public Result saveConfig(@Validated(RestfulValid.POST.class) @RequestBody SysDictEntity dict) {
        int count = sysDictService.save(dict);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_SAVE_FAILURE);
    }

    /**
     * 修改字典目录
     */
    @ApiOperation(value = "修改字典目录", notes = "修改字典目录")
    @SysLogger("修改字典目录")
    @PutMapping("/{id}")
    public Result update(@PathVariable("id") int id, @Validated(RestfulValid.PUT.class) @RequestBody SysDictEntity dict) {
        int count = sysDictService.update(id, dict);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_UPDATE_FAILURE);
    }

    /**
     * 删除字典目录
     */
    @ApiOperation(value = "删除字典目录", notes = "删除字典目录")
    @SysLogger("删除字典目录")
    @DeleteMapping("/{id}")
    public Result deleteConfig(@PathVariable("id") Integer id) {
        int count = sysDictService.delete(id);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_DELETE_FAILURE);
    }

    /**************************/


    /**
     * 字典属性信息
     */
    @ApiOperation(value = "字典属性信息", notes = "字典属性信息")
    @GetMapping("/option/{id}")
    public Result info(@PathVariable("id") Integer id) {
        SysDictEntity dict = sysDictService.findOne(id);
        return Result.ok(dict);
    }

    /**
     * 字典属性分页查询
     */
    @ApiOperation(value = "字典属性分页查询", notes = "字典属性分页查询")
    @RequestMapping(value = "/option/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public Result list(@RequestParam HashMap<String, Object> pageParam, @RequestBody(required = false) HashMap<String, Object> queryParam) {
        //查询列表数据
        PageInfo<SysDictOptionEntity> userConfigInfo = new PageInfo<>(sysDictService.optionFindPage(PageQuery.build(pageParam, queryParam)));
        return Result.ok(userConfigInfo);
    }

    /**
     * 保存字典属性
     */
    @ApiOperation(value = "保存字典属性", notes = "保存字典属性")
    @SysLogger("保存字典属性")
    @PostMapping("/option")
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysDictOptionEntity dict) {
        int count = sysDictService.saveOption(dict);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_SAVE_FAILURE);
    }

    /**
     * 修改字典
     */
    @ApiOperation(value = "修改字典属性", notes = "修改字典属性")
    @SysLogger("修改字典属性")
    @PutMapping("/option/{code}")
    public Result update(@Validated(RestfulValid.PUT.class) @RequestBody SysDictOptionEntity dict) {
        int count = sysDictService.updateOption(dict);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_UPDATE_FAILURE);
    }

    /**
     * 删除字典
     */
    @ApiOperation(value = "删除字典属性", notes = "删除字典属性")
    @SysLogger("删除字典属性")
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable("ids") Integer[] ids) {
        int count = sysDictService.deleteBatch(ids);
        if (count > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_DELETE_FAILURE);
    }

    /**
     * 获取数据字典项
     */
    @ApiOperation(value = "获取数据字典项", notes = "获取数据字典项")
    @GetMapping("/find/{code}")
    public Result getDictByCode(@PathVariable("code") String code) {
        return Result.ok(sysDictService.getDictOptionByCode(code));
    }

}

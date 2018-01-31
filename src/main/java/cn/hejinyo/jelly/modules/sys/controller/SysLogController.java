package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.modules.sys.model.SysLog;
import cn.hejinyo.jelly.modules.sys.service.SysLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/16 22:14
 */
@RestController
@RequestMapping("/")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @GetMapping(value = "/syslog")
    public Result get() {
        PageHelper.startPage(1, 10);
        List<SysLog> sysLogList = sysLogService.list();
        PageInfo<SysLog> userPageInfo = new PageInfo<>(sysLogList, 3);
        return Result.ok(userPageInfo);
    }
}

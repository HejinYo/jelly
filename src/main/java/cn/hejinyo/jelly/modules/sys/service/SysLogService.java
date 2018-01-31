package cn.hejinyo.jelly.modules.sys.service;


import cn.hejinyo.jelly.modules.sys.model.SysLog;

import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/12 22:21
 */

public interface SysLogService {

    /**
     * 保存系统日志
     *
     * @param sysLog
     */
    void save(SysLog sysLog);

    List<SysLog> list();


}

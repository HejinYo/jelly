package cn.hejinyo.jelly.modules.sys.service.impl;

import cn.hejinyo.jelly.common.base.BaseServiceImpl;
import cn.hejinyo.jelly.modules.sys.dao.SysLogDao;
import cn.hejinyo.jelly.modules.sys.model.SysLog;
import cn.hejinyo.jelly.modules.sys.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/6/12 22:31
 */
@Service
public class SysLogServiceImpl extends BaseServiceImpl<SysLogDao, SysLog, Integer> implements SysLogService {

}

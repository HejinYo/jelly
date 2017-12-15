package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.modules.sys.model.dto.CurrentUserDTO;
import cn.hejinyo.jelly.modules.sys.utils.ShiroUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获得当前用户
     *
     * @return
     */
    protected CurrentUserDTO getCurrentUser() {
        return ShiroUtils.getCurrentUser();
    }

    /**
     * 获得当前用户编号
     *
     * @return
     */
    protected int getUserId() {
        return ShiroUtils.getUserId();
    }
}
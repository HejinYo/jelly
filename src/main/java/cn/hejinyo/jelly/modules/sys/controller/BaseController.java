package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.modules.sys.model.dto.CurrentUserDTO;
import cn.hejinyo.jelly.modules.sys.utils.ShiroUtils;

/**
 * @author HejinYo hejinyo@gmail.com
 */
public abstract class BaseController {
    /**
     * 获得当前用户
     */
    protected CurrentUserDTO getCurrentUser() {
        return ShiroUtils.getCurrentUser();
    }

    /**
     * 获得当前用户编号
     */
    protected int getUserId() {
        return ShiroUtils.getUserId();
    }
}
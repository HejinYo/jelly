package cn.hejinyo.jelly.modules.sys.shiro.realm;

import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;
import cn.hejinyo.jelly.modules.sys.service.ShiroService;
import cn.hejinyo.jelly.modules.sys.shiro.token.SysAuthcToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/7/29 18:09
 */
@Component
public class SysAuthcRealm extends AuthorizingRealm {
    @Autowired
    private ShiroService shiroService;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public boolean supports(AuthenticationToken token) {
        // 仅支持StatelessAuthcToken类型的Token
        return token instanceof SysAuthcToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        SysAuthcToken statelessToken = (SysAuthcToken) token;
        // 签证信息
        LoginUserDTO userDTO = statelessToken.getLoginUser();
        // 使用缓存中的userToken和当前验证token进行对比
        return new SimpleAuthenticationInfo(userDTO, userDTO.getUserToken(), getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        LoginUserDTO userDTO = (LoginUserDTO) principals.getPrimaryPrincipal();
        int userId = userDTO.getUserId();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //获得角色信息
        Set<String> roleSet = shiroService.getUserRoleSet(userId);
        //获得授权信息
        Set<String> permissionsSet = shiroService.getUserPermSet(userId);
        authorizationInfo.addRoles(roleSet);
        authorizationInfo.addStringPermissions(permissionsSet);
        return authorizationInfo;
    }
}

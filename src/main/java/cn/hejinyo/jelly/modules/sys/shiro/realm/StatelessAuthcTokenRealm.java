package cn.hejinyo.jelly.modules.sys.shiro.realm;

import cn.hejinyo.jelly.common.utils.RedisKeys;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.modules.sys.model.dto.CurrentUserDTO;
import cn.hejinyo.jelly.modules.sys.service.ShiroService;
import cn.hejinyo.jelly.modules.sys.shiro.token.StatelessAuthcToken;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/7/29 18:09
 */
@Component
public class StatelessAuthcTokenRealm extends AuthorizingRealm {

    @Autowired
    private ShiroService shiroService;
    @Autowired
    private RedisUtils redisUtils;

    private final static Gson GSON = new Gson();

    @Override
    public boolean supports(AuthenticationToken token) {
        //仅支持StatelessAuthcToken类型的Token
        return token instanceof StatelessAuthcToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        StatelessAuthcToken statelessToken = (StatelessAuthcToken) token;
        //签证信息
        CurrentUserDTO userDTO = (CurrentUserDTO) statelessToken.getCurrentUser();
        //使用缓存中的userToken和当前验证token进行对比
        return new SimpleAuthenticationInfo(userDTO, userDTO.getUserToken(), getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        CurrentUserDTO currentUserDTO = (CurrentUserDTO) principals.getPrimaryPrincipal();
        int userId = currentUserDTO.getUserId();
        String username = currentUserDTO.getUserName();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //获得角色信息
        String json = redisUtils.get(RedisKeys.getAuthCacheKey(username));
        Set<String> roleSet;
        Set<String> permissionsSet;
        Map<String, Set<String>> list;
        if (null != json) {
            list = GSON.fromJson(json, new TypeToken<Map<String, Set<String>>>() {
            }.getType());
            roleSet = list.get("role");
            permissionsSet = list.get("permissions");
        } else {
            list = new HashMap<>();
            roleSet = shiroService.getUserRoleSet(userId);
            list.put("role", roleSet);
            //获得权限信息
            permissionsSet = shiroService.getUserPermisSet(userId);
            list.put("permissions", permissionsSet);
            redisUtils.set(RedisKeys.getAuthCacheKey(username), list, 600);
        }
        roleSet.removeIf(Objects::isNull);
        permissionsSet.removeIf(Objects::isNull);
        authorizationInfo.addRoles(roleSet);
        authorizationInfo.addStringPermissions(permissionsSet);
        return authorizationInfo;
    }
}

package cc.mrbird.service.helper;

import cc.mrbird.common.domain.FebsConstant;
import cc.mrbird.common.function.CacheSelector;
import cc.mrbird.common.utils.EncryptUtil;
import cc.mrbird.common.utils.SpringContextUtil;
import cc.mrbird.service.redis.CacheService;
import cc.mrbird.domain.system.User;
import cc.mrbird.service.system.UserService;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;

import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * FEBS工具类
 */
@Slf4j
public class FebsUtil {

    /**
     * 缓存查询摸板，先查缓存，如果缓存查询失败再从数据库查询
     *
     * @param cacheSelector    查询缓存的方法
     * @param databaseSelector 数据库查询方法
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T selectCacheByTemplate(CacheSelector<?> cacheSelector, Supplier<?> databaseSelector) {
        try {
            // 先查 Redis缓存
            log.debug("query data from redis ······");
            return (T) cacheSelector.select();
        } catch (Exception e) {
            // 数据库查询
            log.error("redis error：", e);
            log.debug("query data from database ······");
            return (T) databaseSelector.get();
        }
    }

    /**
     * 获取当前操作用户
     *
     * @return 用户信息
     */
    public static User getCurrentUser() {
        String token = (String) SecurityUtils.getSubject().getPrincipal();
        String username = JWTUtil.getUsername(token);
        UserService userService = SpringContextUtil.getBean(UserService.class);
        CacheService cacheService = SpringContextUtil.getBean(CacheService.class);

        return selectCacheByTemplate(() -> cacheService.getUser(username), () -> userService.findByName(username));
    }

    /**
     * token 加密
     *
     * @param token token
     * @return 加密后的 token
     */
    public static String encryptToken(String token) {
        try {
            EncryptUtil encryptUtil = new EncryptUtil(FebsConstant.TOKEN_CACHE_PREFIX);
            return encryptUtil.encrypt(token);
        } catch (Exception e) {
            log.info("token加密失败：", e);
            return null;
        }
    }

    /**
     * token 解密
     *
     * @param encryptToken 加密后的 token
     * @return 解密后的 token
     */
    public static String decryptToken(String encryptToken) {
        try {
            EncryptUtil encryptUtil = new EncryptUtil(FebsConstant.TOKEN_CACHE_PREFIX);
            return encryptUtil.decrypt(encryptToken);
        } catch (Exception e) {
            log.info("token解密失败：", e);
            return null;
        }
    }

}

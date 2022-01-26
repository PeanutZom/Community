package com.xinhao.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @Xinhao
 * @Date 2022/1/23
 * @Descrption
 */
public class CommunityUtil {
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * 对一个key进行MD5加密
     * @param key
     * @return
     */
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}

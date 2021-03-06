package com.xinhao.community.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Xinhao
 * @Date 2022/2/5
 * @Descrption
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLWEE = "followee";
    private static final String PREFIX_FOLLWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    private static final String PREFIX_POST = "post";

    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    public static String getFollweeKey(int userId, int entityType){
        return PREFIX_FOLLWEE + SPLIT + userId + SPLIT + entityType;
    }

    public static String getFollwerKey(int entityId, int entityType){
        return PREFIX_FOLLWER + SPLIT + entityId +SPLIT +entityType;
    }

    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT +ticket;
    }

    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;

    }
    public static String getUVKey(Date date){
        return PREFIX_UV + SPLIT + df.format(date);
    }
    public static String getUVKey(Date start, Date end){
        return PREFIX_UV + SPLIT + df.format(start) + SPLIT + df.format(end);
    }
    public static String getDauKey(Date date){
        return PREFIX_DAU + SPLIT + df.format(date);
    }
    public static String getDauKey(Date start, Date end){
        return PREFIX_DAU + SPLIT + df.format(start) + SPLIT + df.format(end);
    }

    //???????????????????????????
    public static String getPostToFreshKey(){
        return PREFIX_POST + SPLIT + "score";
    }

}

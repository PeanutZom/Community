package com.xinhao.community.service;

import java.util.Date;

/**
 * @Xinhao
 * @Date 2022/2/21
 * @Descrption
 */
public interface DataService {
    void recordUV(String ip);
    long calculateUV(Date start, Date end);
    void recordDAU(int userId);
    long calculateDAU(Date start, Date end);

}

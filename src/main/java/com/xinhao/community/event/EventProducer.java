package com.xinhao.community.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinhao.community.entity.Event;
import com.xinhao.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @Xinhao
 * @Date 2022/2/13
 * @Descrption
 */
@Component
public class EventProducer {
    @Autowired
    KafkaTemplate kafkaTemplate;

    public void fireEvent(Event event){
        kafkaTemplate.send(event.getTopic(), JSON.toJSONString(event));
    }
}

package com.xinhao.community;

import com.xinhao.community.dao.MessageMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Xinhao
 * @Date 2022/1/19
 * @Descrption
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class KafkaTest {
    @Autowired
    KafkaProducer kafkaProducer;

    @Test
    public void test1(){
        kafkaProducer.sendMessage("test2", "nihaossaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        kafkaProducer.sendMessage("test2", "你好");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

@Component
class KafkaProducer{
    @Autowired
    KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content){
        kafkaTemplate.send(topic, content);
    }
}

@Component
class KafkaConsumer{

    @KafkaListener(topics = {"test2"})
    public void handleMessage(ConsumerRecord record) throws Exception {
        System.out.println(record.value());
        //System.out.println("这里被执行了");
    }
}
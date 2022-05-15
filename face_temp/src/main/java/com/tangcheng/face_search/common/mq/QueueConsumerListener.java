package com.tangcheng.face_search.common.mq;

import com.alibaba.fastjson.JSON;
import com.arcsoft.face.FaceInfo;
import com.tangcheng.face_search.common.face.faceUtils;
import com.tangcheng.face_search.common.util.Base64Utils;
import com.tangcheng.face_search.common.util.ByteUtils;
import com.tangcheng.face_search.pojo.form.image;
import com.tangcheng.face_search.pojo.form.messageImg;
import com.tangcheng.face_search.service.IUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class QueueConsumerListener {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    faceUtils faceUtils;
    @Autowired
    IUserService iUserService;
    // 使用JmsListener配置消费者监听的队列，其中name是接收到的消息
    @JmsListener(destination = "ActiveMQQueue")
    // SendTo 会将此方法返回的数据, 写入到 OutQueue 中去.
    @SendTo("SQueue")
    public void handleMessage_1(String img) {
        log.info("消费者1 进行消费");
        try {
        messageImg image = JSON.parseObject(img, messageImg.class);
        iUserService.addUser(image.getName(),image.getSex(),image.getImageBase64(),image.getFeature());
        } catch (IOException e) {
            log.error("文件上传错误");
            e.printStackTrace();
        }
    }
    @JmsListener(destination = "ActiveMQQueue")
    // SendTo 会将此方法返回的数据, 写入到 OutQueue 中去.
    @SendTo("SQueue")
    public void handleMessage_2(String img) {
        log.info("消费者2 进行消费");
        try {
            messageImg image = JSON.parseObject(img, messageImg.class);
            iUserService.addUser(image.getName(),image.getSex(),image.getImageBase64(),image.getFeature());
        } catch (IOException e) {
            log.error("文件上传错误");
            e.printStackTrace();
        }
    }
    @JmsListener(destination = "ActiveMQQueue")
    // SendTo 会将此方法返回的数据, 写入到 OutQueue 中去.
    @SendTo("SQueue")
    public void handleMessage_3(String img) {
        log.info("消费者3 进行消费");
        try {
            messageImg image = JSON.parseObject(img, messageImg.class);
            iUserService.addUser(image.getName(),image.getSex(),image.getImageBase64(),image.getFeature());
        } catch (IOException e) {
            log.error("文件上传错误");
            e.printStackTrace();
        }
    }

}

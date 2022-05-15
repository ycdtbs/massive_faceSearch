package com.tangcheng.face_search.common.face;

import com.alibaba.fastjson.JSON;
import com.arcsoft.face.FaceInfo;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.tangcheng.face_search.common.message.WebSocketServer;
import com.tangcheng.face_search.common.message.socketMessage;
import com.tangcheng.face_search.common.redis.RedisService;
import com.tangcheng.face_search.common.result.Result;
import com.tangcheng.face_search.common.result.ResultCodeEnum;
import com.tangcheng.face_search.common.util.Base64Utils;
import com.tangcheng.face_search.common.util.SnowflakeIdWorker;
import com.tangcheng.face_search.pojo.form.image;
import com.tangcheng.face_search.pojo.form.messageImg;
import com.tangcheng.face_search.pojo.form.upResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@Data
@Log4j2
@Component
public class faceList {
    // 在此类中处理照片 并发送数据给前端
    @Autowired
    faceUtils faceUtils;
    //注入存放消息的队列，用于下列方法一
    @Autowired
    private Queue queue;
    @Autowired
    RedisService redisService;
    //注入springboot封装的工具类
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @SneakyThrows
    public List<upResult> faceListCheck(File[] listFiles,String SessionId){
        // 创建一个线程进行通知
        new Thread(()->{
            int isJpeg = 0;
            int noJpeg = 0;
            for (File f : listFiles) {
                // 判断是否为标准文件
                if (f.isFile()) {
                    // 获取文件的名字并且查看是否是jpg文件
                    if (f.getName().endsWith(".jpg")) {
                        isJpeg++;
                    }
                    // 如果是标准文件夹运用递归计算jpg图片数量
                } else if (f.isDirectory()) {
                    noJpeg++;
                }
            };
            int all = isJpeg + noJpeg;
            log.info("总数" + all);

            WebSocketServer.sendInfo(JSON.toJSONString(new socketMessage("100","格式符合文件" + String.valueOf(isJpeg) + "个；不符合" + String.valueOf(noJpeg)  + "个",null,null,all + "" )),SessionId);
        }).start();
        for (File f : listFiles) {
            // 判断是否为标准文件
            if (f.isFile()) {
                // 获取文件的名字并且查看是否是jpg文件
                if (f.getName().endsWith(".jpg")) {
                    //log.info("当前是JPG文件");
//                    WebSocketServer.sendInfo(JSON.toJSONString(new socketMessage("200",
//                            f.getName() + "为JPG文件正在处理",
//                            null,
//                            null,
//                            null)),
//                            SessionId);
                    // 开始处理人脸信息
                    FileInputStream fileInputStream = null; // 这个流用来检测人脸
                    FileInputStream fileInputStreamNewPhoto = null; // 这个流用来处理新照片
                    FileInputStream fileInputStreamOldPhoto = null; // 这个流用来处理老照片
                    try {
                        fileInputStream = new FileInputStream(f);
                        List<FaceInfo> faceInfos = faceUtils.faceFind(fileInputStream);
                        if (faceInfos.size() == 0){
                            fileInputStreamOldPhoto = new FileInputStream(f); // 这个流用来处理老照片
                            WebSocketServer.sendInfo(JSON.toJSONString(new socketMessage("300",
                                    "人脸信息不存在", Base64Utils.inputStream2Base64(fileInputStreamOldPhoto),
                                            null,
                                    null)),
                                    SessionId);
                            continue;
                        }else if (faceInfos.size() > 1){
                            fileInputStreamOldPhoto = new FileInputStream(f); // 这个流用来处理老照片
                            WebSocketServer.sendInfo(JSON.toJSONString(new socketMessage("300",
                                            "存在多张人脸", Base64Utils.inputStream2Base64(fileInputStreamOldPhoto),
                                            null,
                                            null)),
                                    SessionId);
                            continue;
                        }else {
                            fileInputStreamOldPhoto = new FileInputStream(f); // 这个流用来处理老照片
                            fileInputStreamNewPhoto = new FileInputStream(f); // 这个流用来处理新照片
                            String newImageBase64 = faceUtils.faceCrop(fileInputStreamNewPhoto, faceInfos.get(0).getRect());
                            String oldImageBase64 = Base64Utils.inputStream2Base64(fileInputStreamOldPhoto);
                            if (newImageBase64 == null || newImageBase64 == ""){
                                WebSocketServer.sendInfo(JSON.toJSONString(new socketMessage("300",
                                                "人脸信息不存在", oldImageBase64,
                                                null,
                                                null)),
                                        SessionId);
                                continue;
                            }
                            WebSocketServer.sendInfo(JSON.toJSONString(new socketMessage("300",
                                            "人脸信息正确", oldImageBase64,
                                            newImageBase64,
                                            null)),
                                    SessionId);
                            // 人脸信息正确 提取特征向量
                            // 进行特征值提取
                            byte[] feature = faceUtils.faceFeature(new FileInputStream(f), faceInfos.get(0));
                            SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0,0);
                            String featureKey = snowflakeIdWorker.nextId() + ""; // redis中存储的key
                            if (feature.length == 0){
                                continue;
                            }else {
                                // 设置雪花算法设置key
                                redisService.setByKey(Base64Utils.byteArray2Base(feature), RedisService.TIME_ONE_MINUTE * 20,featureKey);
                                log.info("特征值采样成功缓存ID" + featureKey + "过期时间" + RedisService.TIME_ONE_MINUTE * 20);
                            }
                            String fileName = f.getName().replace(".jpg", "");
                            messageImg images = new messageImg();
                            images.setName(fileName);
                            images.setImageBase64(newImageBase64);
                            images.setFeature(featureKey);
                            images.setSex("男");
                            jmsMessagingTemplate.convertAndSend(queue, JSON.toJSONString(images));
                            log.info("队列数据发送成功");
                            continue;
                        }
                    } catch (FileNotFoundException e) {
                        log.error("提取流失败");
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //log.info("关闭了流");
                        fileInputStream.close();
                    }

                }
                // 如果是标准文件夹运用递归计算jpg图片数量
            } else if (f.isDirectory()) {
                    WebSocketServer.sendInfo(JSON.toJSONString(new socketMessage("200",f.getName() + "不为JPG文件 过滤",null,null,null)),SessionId);
            }
        }
        return null;
    }
}

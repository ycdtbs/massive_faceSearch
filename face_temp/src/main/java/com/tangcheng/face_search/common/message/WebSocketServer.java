package com.tangcheng.face_search.common.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tangcheng.face_search.common.face.faceSearchHandler;
import com.tangcheng.face_search.common.face.faceSearchUtils;
import com.tangcheng.face_search.common.util.SpringContext;
import com.tangcheng.face_search.model.User;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import com.tangcheng.face_search.pojo.form.faceSearch;

    /**
     * websocket的处理类。
     * 作用相当于HTTP请求
     * 中的controller
     */
    @Component
    @Log4j2
    @ServerEndpoint("/api/pushMessage/{userId}")
    public class WebSocketServer {
//
//        @Autowired
//        faceSearchHandler face;
        /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
        private static int onlineCount = 0;
        /**concurrent包的线程安全Set，用来存放每个客户端对应的WebSocket对象。*/
        private static ConcurrentHashMap<String,WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
        /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
        private  Session session;
        /**接收userId*/
        private String userId = "";

        /**
         * 连接建立成
         * 功调用的方法
         */
        @OnOpen
        public void onOpen(Session session,@PathParam("userId") String userId) {
            this.session = session;
            this.userId=userId;
            if(webSocketMap.containsKey(userId)){
                webSocketMap.remove(userId);
                //加入set中
                webSocketMap.put(userId,this);
            }else{
                //加入set中
                webSocketMap.put(userId,this);
                //在线数加1
                addOnlineCount();
            }
            log.info("用户连接:"+userId+",当前在线人数为:" + getOnlineCount());
            sendMessage("连接成功");
        }

        /**
         * 连接关闭
         * 调用的方法
         */
        @OnClose
        public void onClose() {
            if(webSocketMap.containsKey(userId)){
                webSocketMap.remove(userId);
                //从set中删除
                subOnlineCount();
            }
            log.info("用户退出:"+userId+",当前在线人数为:" + getOnlineCount());
        }

        /**
         * 收到客户端消
         * 息后调用的方法
         * @param message
         * 客户端发送过来的消息
         **/
        @OnMessage
        public void onMessage(String message, Session session) {
            faceSearch faceSearch = JSON.parseObject(message, faceSearch.class);
           if (faceSearch.getImageBase64() == null || "".equals(faceSearch.getImageBase64())){
                log.info("不处理");
            }else {
                log.info("处理人脸");
                try {
                    faceSearchHandler face = SpringContext.getBean(faceSearchHandler.class);
                    log.info(face == null);
                    List<User> users = face.faceHandler(faceSearch.getImageBase64());
                    if (users.size() != 0){
                        WebSocketServer.sendInfo(JSON.toJSONString(users),faceSearch.getSessionId());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
           }

        }


        /**
         * @param session
         * @param error
         */
        @OnError
        public void onError(Session session, Throwable error) {

            log.error("用户错误:"+this.userId+",原因:"+error.getMessage());
            error.printStackTrace();
        }

        /**
         * 实现服务
         * 器主动推送
         */
        public void sendMessage(String message) {
            synchronized(this.session) {
                try {
                    this.session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         *发送自定
         *义消息
         **/
        public static void sendInfo(String message, String userId) {
            //log.info("发送消息到:"+userId+"，报文:"+message);
            if(StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)){
                webSocketMap.get(userId).sendMessage(message);
            }else{
                log.error("用户"+userId+",不在线！");
            }
        }

        /**
         * 获得此时的
         * 在线人数
         * @return
         */
        public static synchronized int getOnlineCount() {
            return onlineCount;
        }

        /**
         * 在线人
         * 数加1
         */
        public static synchronized void addOnlineCount() {
            WebSocketServer.onlineCount++;
        }

        /**
         * 在线人
         * 数减1
         */
        public static synchronized void subOnlineCount() {
            WebSocketServer.onlineCount--;
        }


}

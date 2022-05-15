package com.tangcheng.face_search.common.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class socketMessage {
    private String messageCode; // 100 消息代表是 数据统计 200 消息代表是 日志记录 300 消息代表返回了数据
    private String messageInfo; //
    private String oldPhoto;
    private String newPhoto;
    private String allNum;
}

package com.tangcheng.face_search.common.manage;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class collectionLoadingServer {
    // 内存调度服务，每分钟加载一次内存
    @Scheduled(cron = "0/5 * * * * ?")
    //或直接指定时间间隔，例如：5秒
    private void configureTasks() {


        }
    }

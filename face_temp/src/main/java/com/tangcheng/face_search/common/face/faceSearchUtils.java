package com.tangcheng.face_search.common.face;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tangcheng.face_search.common.milvus.milvusOperateUtils;
import com.tangcheng.face_search.common.redis.RedisService;
import com.tangcheng.face_search.common.util.Base64Utils;
import com.tangcheng.face_search.common.util.ByteUtils;
import com.tangcheng.face_search.mapper.UserMapper;
import com.tangcheng.face_search.model.Admin;
import com.tangcheng.face_search.model.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 人脸搜索核心类
@Component
@Log4j2
public  class faceSearchUtils {
    @Value("${milvus.collection}")
    private String collection;
    @Value("${milvus.partition}")
    private String partition;
    @Value("${oos.endpoint}")
    private String endpoint;
    @Value("${oos.bucketname}")
    private String bucketname;
    @Autowired
    RedisService redisService;
    @Autowired
    milvusOperateUtils milvus;
    @Autowired
    faceUtils faceTools;
    @Autowired
    UserMapper userMapper;

    /**
     * 页面人搜索工具类
     * @param feature
     * @return
     * @throws IOException
     */
    public List<User> getPersonListByPhoto(String feature) throws IOException {
        String byKey = (String) redisService.getByKey(feature); // redis中存储的特征值
        if (byKey == null || byKey == ""){
            log.error("某些原因导致redis缓存失效");
            return null;
        }
        byte[] resource = Base64Utils.Base2byteArray(byKey); // 收到的
        List<List<Float>> lists = new ArrayList<>();
        List<Float> featurnFloats = ByteUtils.byteArray2List(resource);
        lists.add(featurnFloats);
        log.info("加载内存" );
        milvus.loadingLocation(collection);
        log.info("加载完毕");
        List<?> idList = milvus.searchByFeature(collection, lists);
        List<User> searchResultList = new ArrayList<>();
        for (Object id:idList
             ) {
            String userID = id.toString();
            String featureStr = redisService.getByKey(userID); // 特征向量
            if (featureStr == null || featureStr == ""){
                log.info("特征向量缓存加载失败  重新下载图片");
            }else {
                log.info("特征向量加载成功 调用虹软SDK 进行人脸对比");
                byte[] searchByte = Base64Utils.Base2byteArray(featureStr);
                float source = faceTools.faceCompared(resource, searchByte);
                if (source > 0.8){
                    log.info("是同一人获取url" + source +"-" + id);
                    QueryWrapper<User> wrapper = new QueryWrapper<>();
                    wrapper.eq("user_code",userID);
                    User user = userMapper.selectOne(wrapper);
                    if (user != null){
                        String url = "https:" + bucketname+ "." + endpoint + "/"  + user.getUserImage();
                        user.setUserImage(url);
                        searchResultList.add(user);
                    }
                }else {
                    log.info("不是同一人" + source + "-" + id);
                }
            }
        }
        return searchResultList;

    }
}

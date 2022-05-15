package com.tangcheng.face_search.common.face;

import com.arcsoft.face.FaceInfo;
import com.tangcheng.face_search.common.redis.RedisService;
import com.tangcheng.face_search.common.util.Base64Utils;
import com.tangcheng.face_search.common.util.SnowflakeIdWorker;
import com.tangcheng.face_search.model.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class faceSearchHandler {
    @Autowired
    faceUtils faceUtil ;
    @Autowired
    RedisService redisService;
    @Autowired
    faceSearchUtils faceSearch;
    public List<User>  faceHandler(String imaBase64) throws IOException {
        // 人脸检测先
        InputStream inputStream = Base64Utils.base2InputStream(imaBase64);
        InputStream featurnStreaam = Base64Utils.base2InputStream(imaBase64);
        List<FaceInfo> faceInfos = null;
        try {
            faceInfos = faceUtil.faceFind(inputStream);
            if (faceInfos.size() == 0){
                log.info("画面中不存在人脸");
            }else {
                // 开始提取特征值
                byte[] feature = faceUtil.faceFeature(featurnStreaam, faceInfos.get(0));
                if (feature.length == 0 || feature == null){
                    log.error("特征值提取失败");
                }else {
                    SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0,0);
                    String featureKey = snowflakeIdWorker.nextId() + ""; // redis中存储的key
                    redisService.setByKey(Base64Utils.byteArray2Base(feature), RedisService.TIME_ONE_SECOND * 10 ,featureKey);
                    log.info("前端页面搜索到的");
                    List<User> personListByPhoto = faceSearch.getPersonListByPhoto(featureKey);
                    return personListByPhoto;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();

        }finally {
            inputStream.close();
            featurnStreaam.close();
        }
        return new ArrayList<>();
    }
}

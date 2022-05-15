package com.tangcheng.face_search.service.impl;

import com.tangcheng.face_search.common.face.faceUtils;
import com.tangcheng.face_search.common.milvus.milvusOperateUtils;
import com.tangcheng.face_search.common.redis.RedisService;
import com.tangcheng.face_search.common.util.AliyunOSSUtil;
import com.tangcheng.face_search.common.util.Base64Utils;
import com.tangcheng.face_search.common.util.ByteUtils;
import com.tangcheng.face_search.common.util.SnowflakeIdWorker;
import com.tangcheng.face_search.model.User;
import com.tangcheng.face_search.mapper.UserMapper;
import com.tangcheng.face_search.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author astupidcoder
 * @since 2022-05-12
 */
@Service
@Log4j2
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Value("${milvus.collection}")
    private String collection;
    @Value("${milvus.partition}")
    private String partition;
    @Autowired
    UserMapper userMapper;
    @Autowired
    AliyunOSSUtil aliyunOSSUtil;
    @Autowired
    faceUtils faceUtils;
    @Autowired
    RedisService redisService;
    @Autowired
    milvusOperateUtils milvus;
    @Override
    public String addUser(String name, String sex, String imageBase64,String feature)  {
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0, 0);
        long l = snowflakeIdWorker.nextId();
        List<Long> userCodeList = Arrays.asList(l);
        String byKey = (String) redisService.getByKey(feature); // redis中存储的特征值
        if (byKey == null || byKey == ""){
            log.info("某些原因导致redis缓存失效");
            return null;
        }
        byte[] bytes = Base64Utils.Base2byteArray(byKey);
        List<List<Float>> venusLists = new ArrayList<>();
        List<Float> featurnFloats = ByteUtils.byteArray2List(bytes);
        log.info("特征向量长度" + featurnFloats.size());
        venusLists.add(featurnFloats);
        log.info(venusLists);
        long id = milvus.insert(collection, partition, userCodeList,userCodeList,venusLists);
        if (id == 0){
            log.error("特征向量存储失败");
            return null;
        }
        log.info("特征向量存储成功" + id);
        boolean b = redisService.setByKey(byKey,l + "");
        if (b == true){
            log.info("人脸数据缓存成功");
        }else {
            log.info("redis缓存失效，程序继续运行");
        }
        byte[] decode = Base64.decode(imageBase64);
        String filePath = aliyunOSSUtil.upload(decode,String.valueOf(l));
        if (filePath != null){
            log.info("上传成功文件地址" + filePath);
            User user = new User();
            user.setUserCode(String.valueOf(l));
            user.setUserSex(sex);
            user.setUserName(name);
            user.setUserImage(filePath);
            int insert = userMapper.insert(user);
            if (insert > 0){
                return filePath;
            }else {
                return null;
            }
        }else {
            log.info("上传失败" + filePath);
        }
        return filePath;
    }
}

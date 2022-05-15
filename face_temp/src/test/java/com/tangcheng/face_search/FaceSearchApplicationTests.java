package com.tangcheng.face_search;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import com.tangcheng.face_search.factory.FaceEnginePoolFactory;
import com.tangcheng.face_search.mapper.AdminMapper;
import com.tangcheng.face_search.model.Admin;
import com.tangcheng.face_search.pojo.milvusPojo.MilvusInfo;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Log4j2
class FaceSearchApplicationTests {
    @Autowired
    AdminMapper adminMapper;

    @Test
    void contextLoads() {
    }
    @Test
    void mybatisPlus(){
        List<Admin> admins = adminMapper.selectList(null);
        System.out.printf(admins.toString());
    }
    @Test
    void faceEneig() throws Exception {

        // 对象池工厂
        FaceEnginePoolFactory personPoolFactory = new FaceEnginePoolFactory();
        // 对象池配置
        GenericObjectPoolConfig<FaceEngine> objectPoolConfig = new GenericObjectPoolConfig<>();
        objectPoolConfig.setMaxTotal(5);
        // 对象池
        GenericObjectPool<FaceEngine> faceEneig = new GenericObjectPool<>(personPoolFactory, objectPoolConfig);
        FaceEngine faceEngine = faceEneig.borrowObject();
        ImageInfo imageInfo = getRGBData(new File("C:\\Users\\唐成\\Desktop\\bao\\face.png"));
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        int errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        System.out.println(faceInfoList);
    }

}

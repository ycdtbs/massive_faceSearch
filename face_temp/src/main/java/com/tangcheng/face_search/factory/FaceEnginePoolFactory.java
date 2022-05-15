package com.tangcheng.face_search.factory;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.tangcheng.face_search.config.eneity.FaceEngineConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class FaceEnginePoolFactory extends BasePooledObjectFactory<FaceEngine> {
    /**
     * 在对象池中创建对象
     * @return
     * @throws Exception
     */
    @Override
    public FaceEngine create() throws Exception {
        FaceEngine faceEngine = new FaceEngine(FaceEngineConfig.LIB);
        //激活引擎
        int errorCode = faceEngine.activeOnline(FaceEngineConfig.APPID, FaceEngineConfig.SDKKEY);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
        }
        ActiveFileInfo activeFileInfo=new ActiveFileInfo();
        errorCode = faceEngine.getActiveFileInfo(activeFileInfo);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("获取激活文件信息失败");
        }
        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        //初始化引擎
        errorCode = faceEngine.init(engineConfiguration);

        if (errorCode != ErrorInfo.MOK.getValue()) {
            log.error("初始化引擎失败");
        }
        return faceEngine;

    }

    /**
     * 包装对象
     * @param faceEngine
     * @return
     */
    @Override
    public PooledObject<FaceEngine> wrap(FaceEngine faceEngine) {
        return new DefaultPooledObject<>(faceEngine);
    }
    /**
     * 销毁对象
     * @param faceEngine 对象池
     * @throws Exception 异常
     */
    @Override
    public void destroyObject(PooledObject<FaceEngine> faceEngine) throws Exception {
        super.destroyObject(faceEngine);
    }

    /**
     * 校验对象是否可用
     * @param faceEngine 对象池
     * @return 对象是否可用结果，boolean
     */
    @Override
    public boolean validateObject(PooledObject<FaceEngine> faceEngine) {
        return super.validateObject(faceEngine);
    }

    /**
     * 激活钝化的对象系列操作
     * @param faceEngine 对象池
     * @throws Exception 异常信息
     */
    @Override
    public void activateObject(PooledObject<FaceEngine> faceEngine) throws Exception {
        super.activateObject(faceEngine);
    }

    /**
     * 钝化未使用的对象
     * @param faceEngine 对象池
     * @throws Exception 异常信息
     */
    @Override
    public void passivateObject(PooledObject<FaceEngine> faceEngine) throws Exception {
        super.passivateObject(faceEngine);
    }

}

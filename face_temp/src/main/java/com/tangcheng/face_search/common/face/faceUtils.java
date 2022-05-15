package com.tangcheng.face_search.common.face;

import com.arcsoft.face.*;
import com.arcsoft.face.toolkit.ImageInfo;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.tangcheng.face_search.factory.FaceEnginePoolFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

/**
 * 人脸识别公用方法
 */
@Component
@Log4j2
public class faceUtils {
    private GenericObjectPool<FaceEngine> faceEngineGenericObjectPool;
    faceUtils(){
        // 对象池工厂
        FaceEnginePoolFactory personPoolFactory = new FaceEnginePoolFactory();
        // 对象池配置
        GenericObjectPoolConfig<FaceEngine> objectPoolConfig = new GenericObjectPoolConfig<>();
        objectPoolConfig.setMaxTotal(5);
        AbandonedConfig abandonedConfig = new AbandonedConfig();

        abandonedConfig.setRemoveAbandonedOnMaintenance(true); //在Maintenance的时候检查是否有泄漏

        abandonedConfig.setRemoveAbandonedOnBorrow(true); //borrow 的时候检查泄漏

        abandonedConfig.setRemoveAbandonedTimeout(10); //如果一个对象borrow之后10秒还没有返还给pool，认为是泄漏的对象

        // 对象池
        faceEngineGenericObjectPool = new GenericObjectPool<>(personPoolFactory, objectPoolConfig);
        faceEngineGenericObjectPool.setAbandonedConfig(abandonedConfig);
        faceEngineGenericObjectPool.setTimeBetweenEvictionRunsMillis(5000); //5秒运行一次维护任务
        log.info("引擎池开启成功");
    }
    /**
     * 人脸检测
     *
     * @param fileInputStream
     * @return
     */
    public  List<FaceInfo> faceFind(InputStream fileInputStream) throws IOException {
        FaceEngine faceEngine = null;
        try {
            faceEngine = faceEngineGenericObjectPool.borrowObject();
            ImageInfo imageInfo = getRGBData(fileInputStream);
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
            int errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
            return faceInfoList;
        } catch (Exception e) {
            log.error("出现了异常");
            e.printStackTrace();
            return new ArrayList<FaceInfo>();
        } finally {
            fileInputStream.close();
            // 回收对象到对象池
            if (faceEngine != null) {
                faceEngineGenericObjectPool.returnObject(faceEngine);
            }
        }

    }

    /**
     * 人脸截取
     *
     * @param fileInputStream
     * @param rect
     * @return
     */
    public  String faceCrop(InputStream fileInputStream, Rect rect) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BufferedImage bufImage = ImageIO.read(fileInputStream);
            int height = bufImage.getHeight();
            int width = bufImage.getWidth();
            int top = rect.getTop();
            int bottom = rect.getBottom();
            int left = rect.getLeft();
            int right = rect.getRight();
            //System.out.println(top + "-" + bottom + "-" + left + "-" + right);
            try {
                BufferedImage subimage = bufImage.getSubimage(left, top, right - left, bottom - left);
                ImageIO.write(subimage, "png", stream);
                String base64 = Base64.encode(stream.toByteArray());
                return base64;
            }catch (Exception e){
                return null;
            }finally {
                stream.close();
                fileInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
        return null;
    }

    /**
     * 人脸特征值提取
     */
    public byte[] faceFeature(InputStream fileInputStream,FaceInfo faceInfo) throws IOException {
        FaceEngine faceEngine = null;
        FaceFeature faceFeature = new FaceFeature();
        try {
            faceEngine = faceEngineGenericObjectPool.borrowObject();
            ImageInfo imageInfo = getRGBData(fileInputStream);
            int errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfo, faceFeature);
            byte[] featureData = faceFeature.getFeatureData();
            return featureData;

        } catch (Exception e) {
            log.error("出现了异常");
            e.printStackTrace();
            return new byte[0];
        } finally {
            fileInputStream.close();
            // 回收对象到对象池
            if (faceEngine != null) {
                faceEngineGenericObjectPool.returnObject(faceEngine);
            }
        }
    }

    /**
     * 人脸对比
     */
    public float faceCompared(byte [] source,byte [] des) throws IOException {
        FaceEngine faceEngine = null;
        try {
            faceEngine = faceEngineGenericObjectPool.borrowObject();
            FaceFeature targetFaceFeature = new FaceFeature();
            targetFaceFeature.setFeatureData(source);
            FaceFeature sourceFaceFeature = new FaceFeature();
            sourceFaceFeature.setFeatureData(des);
            FaceSimilar faceSimilar = new FaceSimilar();
            faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
            float score = faceSimilar.getScore();
            return score;
        } catch (Exception e) {
            log.error("出现了异常");
            e.printStackTrace();
            return 0;
        } finally {
            // 回收对象到对象池
            if (faceEngine != null) {
                faceEngineGenericObjectPool.returnObject(faceEngine);
            }
        }
    }


}

package com.tangcheng.face_search.controller;

import com.alibaba.fastjson.JSON;
import com.arcsoft.face.FaceInfo;
import com.tangcheng.face_search.common.face.faceList;
import com.tangcheng.face_search.common.face.faceUtils;
import com.tangcheng.face_search.common.message.WebSocketServer;
import com.tangcheng.face_search.common.redis.RedisService;
import com.tangcheng.face_search.common.result.Result;
import com.tangcheng.face_search.common.result.ResultCodeEnum;
import com.tangcheng.face_search.common.util.Base64Utils;
import com.tangcheng.face_search.common.util.SnowflakeIdWorker;
import com.tangcheng.face_search.common.util.ZipUtils;
import com.tangcheng.face_search.model.Admin;
import com.tangcheng.face_search.pojo.form.*;
import com.tangcheng.face_search.service.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/file")
@Tag(name = "文件上传接口")
@Log4j2
@CrossOrigin
public class fileController {
    @Value("${uploadFile.templocation}")
    String tempFilepath;
    @Autowired
    faceUtils faceUtils;
    @Autowired
    faceList faceList;
    @Autowired
    RedisService redisService;
    @PostMapping("/getImageUrl")
    @Operation(summary  = "获取照片URL")
    public Result getImageUrl(@RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            return Result.ok(ResultCodeEnum.FILE_UPLOAD_ERROR);
        }
        try {
            InputStream inputStream = file.getInputStream(); // 上传的照片获取流
            InputStream inputStreamFeature = file.getInputStream(); // 上传的照片获取流
            List<FaceInfo> faceInfos = faceUtils.faceFind(inputStream);
            if (faceInfos.size() == 0){
                return Result.ok(ResultCodeEnum.NO_FACE);
            }if (faceInfos.size() >1){
                return Result.ok(ResultCodeEnum.HAVE_MORE_FACE);
            }else {
                // 照片裁剪
                String encoding = faceUtils.faceCrop(file.getInputStream(), faceInfos.get(0).getRect());
                if (encoding == null || "".equals(encoding)){
                    return Result.ok(ResultCodeEnum.NO_FACE);
                }
                // 进行特征值提取
                byte[] feature = faceUtils.faceFeature(inputStreamFeature, faceInfos.get(0));
                if (feature.length == 0){
                    return Result.ok(ResultCodeEnum.HAVE_MORE_FACE);
                }
                // 设置雪花算法设置key
                SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0,0);
                String featureKey = snowflakeIdWorker.nextId() + ""; // redis中存储的key
                redisService.setByKey(Base64Utils.byteArray2Base(feature),RedisService.TIME_ONE_MINUTE * 3,featureKey);
                log.info("特征值采样成功缓存ID" + featureKey + "过期时间" + RedisService.TIME_ONE_MINUTE * 3);
                featureInfo featureInfo = new featureInfo(null,encoding,featureKey);
                return Result.ok(ResultCodeEnum.HAVE_FACE).data("image",featureInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/getListImageUrl")
    @Operation(summary  = "批量上传照片")
    public Result getListImageUrl(@RequestParam("file") MultipartFile file,@RequestParam("sessionId") String sessionId){
        if(!file.isEmpty()){
            String uploadPath = tempFilepath + "/"+UUID.randomUUID().toString();
            // 如果目录不存在则创建
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String OriginalFilename = file.getOriginalFilename();//获取原文件名
            String suffixName = OriginalFilename.substring(OriginalFilename.lastIndexOf("."));//获取文件后缀名
            String uploadFileName = OriginalFilename.replace(suffixName,"");
            //重新随机生成名字
            String filename = UUID.randomUUID().toString() +suffixName;
            File localFile = new File(uploadPath+"\\"+filename);
            try {
                file.transferTo(localFile); //把上传的文件保存至本地
                /**
                 * 这里应该把filename保存到数据库,供前端访问时使用
                 */
                try {
                    ZipUtils.zipUncompress(uploadPath+"\\"+filename,uploadPath);  // windows 用// linux 用 /
                    File photoList = new File(uploadPath + "\\" +uploadFileName);
                    System.out.println(uploadPath + "\\" +uploadFileName);
                    File[] listFiles = photoList.listFiles();
                    List<upResult> upResults = faceList.faceListCheck(listFiles,sessionId);

                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.error();
                }
                return Result.ok().data("path",uploadPath+"\\"+filename);//上传成功，返回保存的文件地址
            }catch (IOException e){
                e.printStackTrace();
                log.error("上传失败");
                return Result.error();
            }
        }else{
            log.error("文件为空");
            return Result.error();
        }

    }


}
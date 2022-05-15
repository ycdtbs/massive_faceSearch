package com.tangcheng.face_search.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(name = "存储照片信息的类")
@AllArgsConstructor
public class featureInfo {
    @Schema(name = "旧照片")
    private String oldImageBase64;
    @Schema(name = "新照片")
    private String newImageBase64;
    @Schema(name = "特征向量存储的session的key")
    private String feature;
}

package com.tangcheng.face_search.pojo.form;

import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.Rect;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "登录信息")
public class messageImg {
    @Schema(name = "文件名")
    private String name;
    @Schema(name = "性别")
    private String sex;
    @Schema(name = "base64")
    private String imageBase64;
    @Schema(name = "人脸")
    private String feature;
}

package com.tangcheng.face_search.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "人脸查找类")
public class faceSearch {
    @Schema(name = "session")
    private String sessionId;
    @Schema(name = "imageBase64")
    private String imageBase64;
    @Schema(name = "消息")
    private String msg;
}

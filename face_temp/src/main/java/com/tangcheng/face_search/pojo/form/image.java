package com.tangcheng.face_search.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "登录信息")
public class image {
    @Schema(name = "文件名")
    private String fileName;
    @Schema(name = "base64")
    private String imageBase64;
}
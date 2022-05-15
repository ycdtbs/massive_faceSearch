package com.tangcheng.face_search.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "上传结果")
public class upResult {
    @Schema(name = "图片名称")
    private String imageName;
    @Schema(name = "上传结果")
    private String result;
    @Schema(name = "原因")
    private String reson;
}

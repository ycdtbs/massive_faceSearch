package com.tangcheng.face_search.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "用户信息")
public class addUser {
    @Schema(name = "姓名")
    private String name;
    @Schema(name = "性别")
    private String sex;
    @Schema(name = "照片")
    private String imageBase64;
    @Schema(name = "特征值")
    private String feature;
}

package com.tangcheng.face_search.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "人脸查找类")
public class searchPerson {
    @Schema(name = "特征值")
    private String feature;
}
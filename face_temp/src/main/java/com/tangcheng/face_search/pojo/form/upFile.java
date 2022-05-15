package com.tangcheng.face_search.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "文件上传信息")
public class upFile {
    @Schema(name = "sessionID")
    private String sessionId;
}

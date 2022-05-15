package com.tangcheng.face_search.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "登录信息")
public class login {
    @Schema(name = "用户名")
    private String username;
    @Schema(name = "用户密码")
    private String password;
}
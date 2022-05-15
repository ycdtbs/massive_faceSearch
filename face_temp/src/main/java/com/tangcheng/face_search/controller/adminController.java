package com.tangcheng.face_search.controller;

import com.tangcheng.face_search.common.result.Result;
import com.tangcheng.face_search.common.result.ResultCodeEnum;
import com.tangcheng.face_search.model.Admin;
import com.tangcheng.face_search.pojo.form.login;
import com.tangcheng.face_search.service.IAdminService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员操作接口")
@Log4j2
@CrossOrigin
public class adminController {
    @Autowired
    IAdminService iAdminService;
    @PostMapping("/login")
    @Operation(summary  = "登录")
    public Result login(@RequestBody login login){
        Admin admin = iAdminService.login(login);
        if (admin == null){
            return Result.error(ResultCodeEnum.ERROR_PASSWORD);
        }else {
            return Result.ok().data("token",admin.getAdminCode());
        }
    }
    @RequestMapping("/userinfo")
    @Operation(summary  = "用户信息获取")
    public Result userInfo(@RequestParam("token") String token){
        return Result.ok().data("userinfo","admin");
    }
}

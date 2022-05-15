package com.tangcheng.face_search.controller;


import com.tangcheng.face_search.common.result.Result;
import com.tangcheng.face_search.common.result.ResultCodeEnum;
import com.tangcheng.face_search.model.Admin;
import com.tangcheng.face_search.pojo.form.addUser;
import com.tangcheng.face_search.pojo.form.login;
import com.tangcheng.face_search.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author astupidcoder
 * @since 2022-05-12
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户信息接口")
@Log4j2
@CrossOrigin
public class UserController {
    @Autowired
    IUserService iUserService;
    @PostMapping("/addUser")
    @Operation(summary  = "登录")
    public Result addUser(@RequestBody addUser addUser) throws IOException {
        String name = addUser.getName();
        String sex = addUser.getSex();
        String imageBase64 = addUser.getImageBase64();
        String feature = addUser.getFeature();
        String s = iUserService.addUser(name, sex, imageBase64,feature);
        if (s != null || s != ""){
            return Result.ok().data("url",s);
        }else {
            return Result.error();
        }
    }
}

package com.tangcheng.face_search.controller;

import com.tangcheng.face_search.common.face.faceSearchUtils;
import com.tangcheng.face_search.common.result.Result;
import com.tangcheng.face_search.model.User;
import com.tangcheng.face_search.pojo.form.addUser;
import com.tangcheng.face_search.pojo.form.searchPerson;
import com.tangcheng.face_search.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/face")
@Tag(name = "用户信息接口")
@Log4j2
@CrossOrigin
public class faceSearchController {
    @Autowired
    IUserService iUserService;
    @Autowired
    faceSearchUtils faceSearch;
    @PostMapping("/searchFace")
    @Operation(summary  = "查找人脸")
    public Result addUser(@RequestBody searchPerson searchPerson) throws IOException {
        String feature = searchPerson.getFeature();
        List<User> personListByPhoto = faceSearch.getPersonListByPhoto(feature);
        return Result.ok().data("faceList",personListByPhoto);
    }
}

package com.tangcheng.face_search.service;

import com.tangcheng.face_search.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author astupidcoder
 * @since 2022-05-12
 */
public interface IUserService extends IService<User> {
    public String addUser(String name,String sex,String imageBase64,String feature) throws IOException;

}

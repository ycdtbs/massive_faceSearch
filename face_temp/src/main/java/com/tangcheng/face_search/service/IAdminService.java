package com.tangcheng.face_search.service;

import com.tangcheng.face_search.model.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangcheng.face_search.pojo.form.login;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author astupidcoder
 * @since 2022-05-11
 */
public interface IAdminService extends IService<Admin> {
    public Admin login(login login);
}

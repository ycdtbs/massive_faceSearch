package com.tangcheng.face_search.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tangcheng.face_search.common.util.MD5Tools;
import com.tangcheng.face_search.model.Admin;
import com.tangcheng.face_search.mapper.AdminMapper;
import com.tangcheng.face_search.pojo.form.login;
import com.tangcheng.face_search.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author astupidcoder
 * @since 2022-05-11
 */
@Service
@Log4j2
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {
    @Autowired
    AdminMapper adminMapper;
    @Override
    public Admin login(login login) {
        //创建一个QueryWrapper的对象
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("admin_name",login.getUsername());
        Admin admin = adminMapper.selectOne(wrapper);
        if (null != admin){
            // 查询到了用户 进行对比
            if (admin.getAdminPasswd().equals(MD5Tools.string2MD5(login.getPassword()))){
                log.info("登录成功");
                return admin;
            }else {
                log.info("登录失败");
                return null;
            }
        }else {
            return null;
        }
    }
}

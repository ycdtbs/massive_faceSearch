package com.tangcheng.face_search.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author astupidcoder
 * @since 2022-05-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class Admin extends Model {

    private static final long serialVersionUID = 1L;

    private String adminCode;

    private String adminName;

    private String adminPasswd;


}

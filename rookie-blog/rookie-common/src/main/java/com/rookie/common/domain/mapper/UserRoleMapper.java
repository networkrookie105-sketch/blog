package com.rookie.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rookie.common.domain.dos.UserRoleDo;

import java.util.List;
import java.util.Map;

/**
 * @author peishunwu
 * @ClassName UserRoleMapper
 * @description: TODO
 * @date 2024年03月08日
 * @version: 1.0
 */
public interface UserRoleMapper extends BaseMapper<UserRoleDo> {
    /**
     * 根据用户名查询角色
     * @param username
     * @return
     */
    default List<UserRoleDo> selectUserName(String username) {
        LambdaQueryWrapper<UserRoleDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleDo::getUserName, username);
        return selectList(wrapper);
    }
}

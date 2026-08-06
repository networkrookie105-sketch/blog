package com.rookie.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rookie.common.domain.dos.UserDO;

import java.time.LocalDateTime;

/**
 * @author peishunwu
 * @ClassName UserMapper
 * @description: TODO
 * @date 2023年12月16日
 * @version: 1.0
 */
public interface  UserMapper extends BaseMapper<UserDO> {
    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    default UserDO findByUsername(String username){
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUserName, username);
        return selectOne(wrapper);
    };

    /**
     * 根据用户名更新密码
     * @param username
     * @param password
     * @return
     */
    default int updatePasswordByUsername(String username, String password) {
        LambdaUpdateWrapper<UserDO> wrapper = new LambdaUpdateWrapper<>();
        // 设置要更新的字段
        wrapper.set(UserDO::getPassword, password);
        //其实这块时间不用更新，因为为数据库改字段的创建会默认去更新时间
        wrapper.set(UserDO::getUpdateTime, LocalDateTime.now());
        // 更新条件
        wrapper.eq(UserDO::getUserName, username);
        return update(null, wrapper);
    }

}

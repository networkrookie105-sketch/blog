package com.rookie.jwt.service;

import com.rookie.common.domain.dos.UserDO;
import com.rookie.common.domain.dos.UserRoleDo;
import com.rookie.common.domain.mapper.UserMapper;
import com.rookie.common.domain.mapper.UserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author peishunwu
 * @ClassName UserDetailServiceImpl
 * @description: TODO
 * @date 2023年12月19日
 * @version: 1.0
 */
@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中查询用户信息
        UserDO userDO = userMapper.findByUsername(username);
        if (Objects.isNull(userDO)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        //查询用户角色
        List<UserRoleDo> userRoleDos = userRoleMapper.selectUserName(username);
        String[] roleArr = null;
        if (!CollectionUtils.isEmpty(userRoleDos)) {
            List<String> roles = userRoleDos.stream().map(p->p.getRole()).collect(Collectors.toList());
            roleArr = roles.toArray(new String[roles.size()]);
        }
        // authorities 用于指定角色，这里写死为 ADMIN 管理员
        return User.withUsername(userDO.getUserName())
                .password(userDO.getPassword())
                .authorities(roleArr)
                .build();
    }
}

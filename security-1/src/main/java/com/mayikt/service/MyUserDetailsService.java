package com.mayikt.service;

import com.mayikt.entity.PermissionEntity;
import com.mayikt.entity.UserEntity;
import com.mayikt.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

@Component
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1.根据数据库查询，用户是否登陆
        UserEntity user = userMapper.findByUsername(username);
        // 2.查询该用户信息权限
        if (user != null) {
            // 设置用户权限
            List<PermissionEntity> listPermission = userMapper.findPermissionByUsername(username);
            System.out.println("用户信息权限:" + user.getUsername() + ",权限:" + listPermission.toString());
            if (listPermission != null && listPermission.size() > 0) {
                List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
                for (PermissionEntity permission : listPermission) {
                    // 添加用户权限
                    authorities.add(new SimpleGrantedAuthority(permission.getPermTag()));
                }
                // 设置用户权限
                user.setAuthorities(authorities);
            }

        }

        return user;
    }

}
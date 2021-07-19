package com.springsecurity.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springsecurity.demo.entity.Users;
import com.springsecurity.demo.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiawen yang
 * @date 2021/7/17 上午10:17
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("username", username);
        Users users = usersMapper.selectOne(wrapper);
        if(users == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }

        //这里先把用户的角色和权限进行硬编码，其中指定用户角色时，要加前缀ROLE_,将角色和权限两种模式区分开来
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("app,ROLE_sale");
        //把数据库中查出的用户来构造User对象
        return new User(users.getUsername(), new BCryptPasswordEncoder().encode(users.getPassword()), grantedAuthorities);
    }








}

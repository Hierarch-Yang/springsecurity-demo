package com.springsecurity.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springsecurity.demo.entity.Users;
import org.springframework.stereotype.Repository;

/**
 * @author xiawen yang
 * @date 2021/7/17 上午10:47
 */
@Repository
public interface UsersMapper extends BaseMapper<Users> {
}

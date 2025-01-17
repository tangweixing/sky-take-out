package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /*
    根据openId查询用户
     */
    @Select("select * from user where openid=#{openId}")
    User getByOpenId(String openId);
/*
插入用户
 */
    void insert(User user);
}

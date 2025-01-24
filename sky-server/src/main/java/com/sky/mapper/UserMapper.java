package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

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
    @Select("select * from user where id=#{userId}")
    User getById(Long userId);

    /*
    计算用户总数
     */
    Integer sumByMap(Map map);
}

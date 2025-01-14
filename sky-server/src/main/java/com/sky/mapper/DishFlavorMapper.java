package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /*
    批量插入口味数据
     */
    void insertBatch(List<DishFlavor> flavors);
/*
根据菜品删除口味数据
 */
    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    void deleteByDishId(Long dishId);
    /*
    根据菜品批量删除口味数据
     */
    void deleteByDishIds(List<Long> ids);
    /*
    通过id查口味列
     */
    @Select("select * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}

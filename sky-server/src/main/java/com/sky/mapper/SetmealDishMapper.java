package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /*
     *根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealDishIdsBySetmealId(List<Long> dishIds);
/*
新增菜品setmeal_dish
 */
    void insertBatch(List<SetmealDish> setmealDishes);

    void deleteByIds(List<Long> ids);
/*
删除套餐菜品
 */
    @Delete("delete from setmeal_dish where setmeal_id=#{setmealId}")
    void deleteBySetmealId(Long setmealId);
    @Select("select * from setmeal_dish where setmeal_id=#{setmealId}")
    List<SetmealDish> getSetmealDishBySetmealId(Long setmealId);
}

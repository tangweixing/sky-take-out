package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /*
    新增套餐
     */
    void saveWithDish(SetmealVO setmealVO);
    /*
     根据id查询菜品
      */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    /*
     修改套餐
      */
    void updateWithDishes(SetmealVO setmealVO);
    /*
    删除套餐
     */
    void deleteBatch(List<Long> ids);
/*
修改套餐页面显示
 */
    SetmealVO getByIdWithDish(Long id);
    /*
      套餐起售停售
       */
    void updateStatus(Integer status, Long id);
    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}

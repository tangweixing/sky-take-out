package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /*
    新增菜品和对应的口味
     */
    public void saveWithFlavor(DishDTO dishDTO);
    /**
     * 菜品分页查询
     *
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    void deleteBatch(List<Long> ids);
    /**
     * 根据菜品id查询信息
     *
     * @return
     */
    DishVO getByIdWithFlavor(Long id);
    /*
        修改菜品
         */
    void updateWithFlavor(DishDTO dishDTO);
/*
通过菜品分类查菜品列表
 */
    List<Dish> queryByCategoryId(Long categoryId);
}

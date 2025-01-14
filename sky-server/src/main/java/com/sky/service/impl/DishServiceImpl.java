package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
@Autowired
private DishMapper dishMapper;
@Autowired
private DishFlavorMapper dishFlavorMapper;
@Autowired
private SetmealDishMapper setmealDishMapper;
    /*
       新增菜品和对应的口味
        */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表插入1条数据
        dishMapper.insert(dish);
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            //向口味表插入多条数据
            dishFlavorMapper.insertBatch(flavors);
        }

    }
    /**
     * 分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
     Page<DishVO> page= dishMapper.pageQuery(dishPageQueryDTO);
     long total = page.getTotal();
        List<DishVO> result = page.getResult();
        return  new PageResult(total,result);
    }
    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够删除 起售and 套餐关联了
        for (Long id : ids) {
         Dish dish=dishMapper.getById(id);
         if (dish.getStatus()== StatusConstant.ENABLE){
             //当前菜品处于起售中不能删除
             throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
         }
        }
        List<Long> setmealIds = setmealDishMapper.getSetmealDishIdsBySetmealId(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        //删除菜品数据和菜品口味数据
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            dishFlavorMapper.deleteByDishId(id);
//        }
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }
/*
查询菜品和口味数据
 */
    @Transactional
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> dishFlavors=dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }
    /*
          修改菜品
           */
@Transactional
    public void updateWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //修改菜品表基本信息
        dishMapper.update(dish);
        //删除原有口味
        dishFlavorMapper.deleteByDishId(dish.getId());
        //重新插入口味数据

    List<DishFlavor> flavors = dishDTO.getFlavors();
    if (flavors != null && flavors.size() > 0) {
        flavors.forEach(flavor -> {
            flavor.setDishId(dish.getId());
        });
        //向口味表插入多条数据
        dishFlavorMapper.insertBatch(flavors);
    }

    }

}

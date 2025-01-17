package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /*
    新增套餐
     */
    @Transactional
    public void saveWithDish(SetmealVO setmealVO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealVO, setmeal);

        //新增套餐
        setmealMapper.insert(setmeal);
        System.out.println(setmeal);
        //新增菜品套餐
        List<SetmealDish> setmealDishes = setmealVO.getSetmealDishes();
        Long setmealId = setmeal.getId();

        if (setmealDishes != null && setmealDishes.size() > 0) {
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            //向套餐菜品表插入多条数据
            setmealDishMapper.insertBatch(setmealDishes);
        }

    }
    /*
     根据id查询菜品
      */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
       Page<SetmealVO> page= setmealMapper.pageQuery(setmealPageQueryDTO);
        List<SetmealVO> result = page.getResult();
        long total = page.getTotal();
        return new PageResult(total,result);
    }


    /*
     修改套餐
      */
    @Override
    public void updateWithDishes(SetmealVO setmealVO) {
        //修改套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealVO, setmeal);
        setmealMapper.update(setmeal);
        //修改关联菜品的套餐
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        //新增菜品套餐
        List<SetmealDish> setmealDishes = setmealVO.getSetmealDishes();
        Long setmealId = setmeal.getId();

        if (setmealDishes != null && setmealDishes.size() > 0) {
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            //向套餐菜品表插入多条数据
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }
    /*
      删除套餐
       */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
          Setmeal setmeal=setmealMapper.getById(id);
          if (setmeal.getStatus()== StatusConstant.ENABLE){
              throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
          }
        }
        setmealMapper.deleteByIds(ids);
        setmealDishMapper.deleteByIds(ids);
    }

    @Override
    public SetmealVO getByIdWithDish(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
    List<SetmealDish> setmealDishes=setmealDishMapper.getSetmealDishBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    public void updateStatus(Integer status, Long id) {

        Setmeal setmeal = Setmeal.builder().status(status).id(id).build();
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

}

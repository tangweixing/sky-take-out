package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api("套餐接口")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    /*
    新增菜品
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result insert(@RequestBody SetmealVO setmealVO) {
        log.info("新增套餐:{}", setmealVO);
        setmealService.saveWithDish(setmealVO);
        return Result.success();
    }
    /*
    根据id查询菜品
     */
    @GetMapping("/page")
    @ApiOperation("查询套餐")
public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("查询套餐:{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }
    /*
  删除套餐
   */
    @DeleteMapping
    @ApiOperation("删除套餐")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("套餐批量删除：{}",ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }
    /*
    修改套餐
     */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result updateWithDishes(@RequestBody SetmealVO setmealVO) {
        setmealService.updateWithDishes(setmealVO);
        return Result.success();
    }
    /*
    根据id显示套餐
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id显示套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据id显示套餐:{}",id);
        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }
    /*
    套餐起售停售
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    public Result updateStatus(@PathVariable Integer status,Long id) {
        log.info("套餐起售停售：{},{}", status,id);
        setmealService.updateStatus(status,id);
        return Result.success();
    }
}

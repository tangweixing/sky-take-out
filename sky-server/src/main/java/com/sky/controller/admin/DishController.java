package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

/*
菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
@Autowired
private RedisTemplate redisTemplate;

    /*
    新增菜品
    @param dishDTO
    @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        //清理缓存数据
        String key="dish_"+dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }
    /**
     * 菜品分页查询
     *
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询，参数为：:{}", dishPageQueryDTO);
        PageResult pageResult= dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除：{}",ids);
        dishService.deleteBatch(ids);
        //清理菜品缓存数据,所有以dish_开头的key
        cleanCache("dish_*");
        return Result.success();
    }
    /**
     * 根据菜品id查询信息
     *
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据菜品id查询信息")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品:{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }
    /*
    修改菜品
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        //清理菜品缓存数据,所有以dish_开头的key
        cleanCache("dish_*");
        return Result.success();
    }
    @GetMapping("/list")
    @ApiOperation("获得菜品类菜品")
    public Result<List<Dish>> list(@RequestParam Long categoryId){
        System.out.println("开始");
        log.info("加载菜品类id:{}",categoryId);
        List<Dish> dishes= dishService.queryByCategoryId(categoryId);
        return Result.success(dishes);
    }
    /**
     * 起售停售
     *
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("起售停售")
    public Result startOrStop(@PathVariable Integer status,Long id) {
        log.info("起售停售：{},{}", status,id);
        dishService.startOrStop(status,id);
        //清理菜品缓存数据,所有以dish_开头的key
        cleanCache("dish_*");
        return Result.success();
    }
    private void cleanCache(String patten) {
        Set keys = redisTemplate.keys(patten);
        redisTemplate.delete(keys);
    }
}

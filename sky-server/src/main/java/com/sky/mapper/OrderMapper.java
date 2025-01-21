package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {

    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


    List<Orders> getByUserId(Long currentId,Integer status);

    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);
@Delete("delete from orders where id=#{id}")
    void deleteById(Long id);

    List<Orders> getOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    Integer getCountByStatus(int a);
}

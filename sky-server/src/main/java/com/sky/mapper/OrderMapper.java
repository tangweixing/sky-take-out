package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    /*
    根据订单状态和下单时间查询订单
     */
@Select("select * from orders where status=#{status} and order_time<#{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime time);

/*
查询营业额
 */
    Double sumByMap(Map map);
    /*
订单数量统计
*/
    Integer sumOrdersByMap(Map map);
    /*
     销量排名top10
     */
    List<GoodsSalesDTO> getSalesTop(Map map);
}

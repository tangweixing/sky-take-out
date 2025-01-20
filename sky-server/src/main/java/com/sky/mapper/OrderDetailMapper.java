package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /*
    批量插入订单明细数据
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    @Select("select * from order_detail where order_id=#{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
    @Delete("delete from order_detail where order_id=#{id}")
    void deleteByOrderId(Long id);
}

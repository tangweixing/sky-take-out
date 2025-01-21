package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

public interface OrderService {
    /*
    用户下单
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
//    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);
    /**
     * 历史订单查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult queryHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO);
    /**
     * 查看订单详情
     *
     * @param id
     * @return
     */
    OrderVO queryOrdersById(Long id);
    /**
     * 取消订单
     *
     * @param id
     * @return
     */
    void cancelOrdersById(Long id);
    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    void repetitionOrders(Long id);
    /**
     * 订单查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult queryOrders(OrdersPageQueryDTO ordersPageQueryDTO);
    /**
     * 订单取消
     *
     * @param   ordersCancelDTO, id
     * @return
     */
    void cancelOrders(OrdersCancelDTO ordersCancelDTO);
    /**
     * 各个状态的订单数量统计
     *
     * @param
     * @return
     */
    OrderStatisticsVO getAllStatusOrders();
    /**
     * 完成订单
     *
     * @param   , id
     * @return
     */
    void completeOrders(Long id);
    /**
     * 拒单
     *
     * @param   , id
     * @return
     */
    void rejectOrders(OrdersRejectionDTO ordersRejectionDTO);
    /**
     * 接单订单
     *
     * @param   , id
     * @return
     */
    void confirmOrders(OrdersConfirmDTO ordersConfirmDTO);
    /**
     * 派送订单
     *
     * @param   , id
     * @return
     */
    void deliveryOrders(Long id);
    /**
     * 查询订单详情
     *
     * @param   , id
     * @return
     */
    OrderVO getOrderDetail(Long id);
}

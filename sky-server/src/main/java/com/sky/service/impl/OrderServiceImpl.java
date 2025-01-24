package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WebSocketServer webSocketServer;
  @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理各种业务异常（地址普为空.购物车数据为空）
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        AddressBook addressBook = addressBookMapper.getById(addressBookId);
        if (addressBook == null) {
            //抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //查询购物车数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list==null || list.size()==0) {
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //插入订单
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);

        orderMapper.insert(orders);
        List<OrderDetail> orderDetailList=new ArrayList<>();
        //插入订单明细表
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());//设置当前订单明细关联的订单id
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);
        //清空当前用户的购物车
        shoppingCartMapper.deleteByUserId(userId);
        //封装vo返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder().id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .build();

        return orderSubmitVO;
    }
//    /**
//     * 订单支付
//     *
//     * @param ordersPaymentDTO
//     * @return
//     */
//    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
//        Long userId = BaseContext.getCurrentId();
//        User user = userMapper.getById(userId);

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));
//
//        return vo;
//    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
        //通过websocket向客户端浏览器推送消息 type orderId content
        Map map=new HashMap();
        map.put("type",1);//1.来单提醒2.客户催单
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号："+outTradeNo);
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }
    /**
     * 历史订单查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
 @Transactional
    public PageResult queryHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {

        //获得订单
        List<OrderVO> orderVOS=new ArrayList<>();
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
            List<Orders> orders = orderMapper.getByUserId(BaseContext.getCurrentId(), ordersPageQueryDTO.getStatus());

        long total = orders.size();
        //配置返回值
        for (Orders order : orders) {
            OrderVO orderVO = new OrderVO();

            BeanUtils.copyProperties(order, orderVO);
            //获得订单内容

            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(order.getId());
            //设置订单菜品详细信息
            String dishName = "";
            for (OrderDetail orderDetail : orderDetailList) {
                dishName += orderDetail.getName();
            }
            orderVO.setOrderDishes(dishName);
            orderVO.setOrderDetailList(orderDetailList);
            orderVOS.add(orderVO);
        }
return new PageResult(total,orderVOS);
    }
    /**
     * 查看订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO queryOrdersById(Long id) {
     Orders orders=orderMapper.getById(id);
        AddressBook addressBook = addressBookMapper.getById(orders.getAddressBookId());
        OrderVO orderVO = new OrderVO();

        if (orders!=null) {
            BeanUtils.copyProperties(orders, orderVO);
        }
        orderVO.setAddress(addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }
    /**
     * 取消订单
     *
     * @param id
     * @return
     */
    @Override
    public void cancelOrdersById(Long id) {
    orderMapper.deleteById(id);
    orderDetailMapper.deleteByOrderId(id);
    }
    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @Override
    public void repetitionOrders(Long id) {
        //再来一单
        Orders orders=orderMapper.getById(id);
        orders.setId(null);
        orderMapper.insert(orders);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orders.getId());
        }
        orderDetailMapper.insertBatch(orderDetailList);
    }
    /**
     * 订单查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult queryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        //获得订单
        List<OrderVO> orderVOS=new ArrayList<>();
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        List<Orders> orders = orderMapper.getOrders(ordersPageQueryDTO);

        long total = orders.size();
        //配置返回值
        for (Orders order : orders) {
            OrderVO orderVO = new OrderVO();

            BeanUtils.copyProperties(order, orderVO);
            //获得订单内容

            List<OrderDetail> orderDetailList=orderDetailMapper.getByOrderId(order.getId());
            //设置订单菜品详细信息
            String dishName="";
            for (OrderDetail orderDetail : orderDetailList) {
                    dishName+=orderDetail.getName();
            }
            orderVO.setOrderDishes(dishName);
            orderVO.setOrderDetailList(orderDetailList);
            //自行设置地址
            AddressBook addressBook = addressBookMapper.getById(order.getAddressBookId());
            orderVO.setAddress(addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());
            orderVOS.add(orderVO);
        }
        return new PageResult(total,orderVOS);
    }
    /**
     * 订单取消
     *
     * @param   ordersCancelDTO, id
     * @return
     */
    @Override
    public void cancelOrders(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = orderMapper.getById(ordersCancelDTO.getId());
        orders.setRejectionReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(6);
        orderMapper.update(orders);
    }

    /**
     * 各个状态的订单数量统计
     *
     * @param
     * @return
     */
    @Override
    public OrderStatisticsVO getAllStatusOrders() {
        int a=2;
        List<Integer> list=new ArrayList<>();
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        for (;a<5;a++){
            list.add(orderMapper.getCountByStatus(a));
        }
       orderStatisticsVO.setToBeConfirmed(list.get(0));
        orderStatisticsVO.setConfirmed(list.get(1));
        orderStatisticsVO.setDeliveryInProgress(list.get(2));
        return orderStatisticsVO;
    }
    /**
     * 完成订单
     *
     * @param   , id
     * @return
     */
    @Override
    public void completeOrders(Long id) {
        Orders orders = orderMapper.getById(id);
        orders.setStatus(6);
        orderMapper.update(orders);
    }
    /**
     * 拒单
     *
     * @param   , id
     * @return
     */
    @Override
    public void rejectOrders(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = orderMapper.getById(ordersRejectionDTO.getId());
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setStatus(7);
        orderMapper.update(orders);
    }
    /**
     * 接单订单
     *
     * @param   , id
     * @return
     */
    @Override
    public void confirmOrders(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = orderMapper.getById(ordersConfirmDTO.getId());
        ordersConfirmDTO.setStatus(3);
        orders.setStatus(ordersConfirmDTO.getStatus());
        orderMapper.update(orders);
    }
    /**
     * 派送订单
     *
     * @param   , id
     * @return
     */
    @Override
    public void deliveryOrders(Long id) {
        Orders orders = orderMapper.getById(id);
        orders.setStatus(4);
        orderMapper.update(orders);
    }
    /**
     * 查询订单详情
     *
     * @param   , id
     * @return
     */
    @Override
    public OrderVO getOrderDetail(Long id) {
        Orders orders = orderMapper.getById(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }
    /**
     * 客户催单
     *
     * @param id
     * @return
     */
    @Override
    public void reminder(Long id) {
        //查询订单
        Orders orders = orderMapper.getById(id);
        //判断订单是否存在
        if (orders==null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //通过websocket向客户端浏览器推送消息 type orderId content
        Map map=new HashMap();
        map.put("type",2);//1.来单提醒2.客户催单
        map.put("orderId",orders.getId());
        map.put("content","订单号："+orders.getNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }
}

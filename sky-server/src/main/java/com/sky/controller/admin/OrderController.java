package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "管理员端相关接口")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;
    /**
     * 订单查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单查询")
    public Result<PageResult> queryOrders(OrdersPageQueryDTO ordersPageQueryDTO) throws Exception {
        log.info("订单查询: {}", ordersPageQueryDTO);
        PageResult pageResult=orderService.queryOrders(ordersPageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * 订单取消
     *
     * @param   ordersCancelDTO, id
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("订单取消")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        log.info("订单取消: {}", ordersCancelDTO);
        orderService.cancelOrders(ordersCancelDTO);
        return Result.success();
    }
    /**
     * 各个状态的订单数量统计
     *
     * @param
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result getAllStatusOrders() throws Exception {
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO=orderService.getAllStatusOrders();
        return Result.success(orderStatisticsVO);
    }
    /**
     * 完成订单
     *
     * @param   , id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result cancelOrder(@PathVariable Long id) throws Exception {
        log.info("完成订单: {}", id);
        orderService.completeOrders(id);
        return Result.success();
    }
    /**
     * 拒单
     *
     * @param   , id
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        log.info("拒单: {}", ordersRejectionDTO);
        orderService.rejectOrders(ordersRejectionDTO);
        return Result.success();
    }
    /**
     * 接单订单
     *
     * @param   , id
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单订单")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) throws Exception {
        log.info("接单订单: {}", ordersConfirmDTO);
        orderService.confirmOrders(ordersConfirmDTO);
        return Result.success();
    }
    /**
     * 派送订单
     *
     * @param   , id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result deliveryOrder(@PathVariable Long id) throws Exception {
        log.info("派送订单: {}", id);
        orderService.deliveryOrders(id);
        return Result.success();
    }
    /**
     * 查询订单详情
     *
     * @param   , id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) throws Exception {
        log.info("查询订单详情: {}", id);
        OrderVO orderVO=orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }
}

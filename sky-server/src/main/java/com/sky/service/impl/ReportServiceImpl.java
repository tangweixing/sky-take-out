package com.sky.service.impl;


import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /*
   统计指定时间区间内的营业额数据
   @param
   @return
    */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //获得日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //获得营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            //查询当天范围
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /*
    用户数量统计
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //获得日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //获得新增用户数
        List<Integer> newUserList = new ArrayList<>();
        //获得总用户数
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();

            map.put("endTime", endTime);
            Integer allUser = userMapper.sumByMap(map);
            map.put("beginTime", beginTime);
            Integer newUser = userMapper.sumByMap(map);


            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
            totalUserList.add(allUser);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();

    }

    /*
订单数量统计
*/
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //获得日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //每日完成订单
        List<Integer> orderCountList = new ArrayList<>();
        //每日订单
        List<Integer> totalOrderCountList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer sumAllOrders = orderMapper.sumOrdersByMap(map);
            map.put("status", Orders.COMPLETED);
            Integer sumNewOrders = orderMapper.sumOrdersByMap(map);
            totalOrderCountList.add(sumAllOrders);
            orderCountList.add(sumNewOrders);
        }
        //计算时间区间内的订单总数量
        Integer total = totalOrderCountList.stream().reduce(Integer::sum).get();
        //计算时间区间内的订单有效总数量
        Integer valid = orderCountList.stream().reduce(Integer::sum).get();
        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if (total != 0) {
            orderCompletionRate = valid.doubleValue() / total;
        }
        return OrderReportVO.builder()
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(total)
                .validOrderCount(valid)
                .validOrderCountList(StringUtils.join(orderCountList, ","))
                .orderCountList(StringUtils.join(totalOrderCountList, ","))
                .dateList(StringUtils.join(dateList, ","))
                .build();
    }
    /*
     销量排名top10
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        //获得日期
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            //获得前10数据
            List<GoodsSalesDTO> goodsSalesDTO= orderMapper.getSalesTop(map);
//封装数据
        List<String> names = goodsSalesDTO.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");
        List<Integer> numbers = goodsSalesDTO.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        return SalesTop10ReportVO.builder().nameList(nameList).numberList(numberList).build();
    }
}

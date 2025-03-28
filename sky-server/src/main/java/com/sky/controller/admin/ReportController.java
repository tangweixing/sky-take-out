package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "数据统计相关接口")
public class ReportController {
    @Autowired
    private ReportService reportService;
    /*
    营业额统计
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额统计：{},{}", begin,end);
        TurnoverReportVO turnoverReportVO=reportService.getTurnoverStatistics(begin,end);
        return Result.success(turnoverReportVO);
    }
    /*
 用户数量统计
  */
    @GetMapping("/userStatistics")
    @ApiOperation("用户数量统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户数量统计：{},{}", begin,end);
        UserReportVO userReportVO=reportService.getUserStatistics(begin,end);
        return Result.success(userReportVO);
    }
    /*
订单数量统计
*/
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单数量统计")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单数量统计：{},{}", begin,end);
        OrderReportVO orderReportVO=reportService.getOrdersStatistics(begin,end);
        return Result.success(orderReportVO);
    }
    /*
   销量排名top10
   */
    @GetMapping("/top10")
    @ApiOperation("销量排名top10")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("销量排名top10：{},{}", begin,end);
        SalesTop10ReportVO salesTop10ReportVO=reportService.getTop10(begin,end);
        return Result.success(salesTop10ReportVO);
    }
    /*
导出excel文件
*/
    @GetMapping("/export")
    @ApiOperation("导出excel文件")
    public void export(HttpServletResponse response) throws IOException {
        log.info("导出excel文件");
         reportService.exportBusinessData(response);

    }

}

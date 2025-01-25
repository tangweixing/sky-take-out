package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

public interface ReportService {
    /*
     统计指定时间区间内的营业额数据
     @param
     @return
      */
     TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) ;
    /*
    用户数量统计
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);
    /*
订单数量统计
*/
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);
    /*
     销量排名top10
     */
    SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end);
    /*
  导出excel文件
  */
    void exportBusinessData(HttpServletResponse response) throws IOException;
}

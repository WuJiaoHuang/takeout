package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;


    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date:dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover==null ? 0.0 :turnover;
            turnoverList.add(turnover);

        }




        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        Integer totalOrderCount = orderMapper.count(LocalDateTime.of(begin,LocalTime.MIN),LocalDateTime.of(end, LocalTime.MAX));
        Integer validOrderCount = orderMapper.countValid(LocalDateTime.of(begin,LocalTime.MIN),LocalDateTime.of(end,LocalTime.MAX));
        Integer CompletedCount = orderMapper.countCompleted(LocalDateTime.of(begin,LocalTime.MIN),LocalDateTime.of(end,LocalTime.MAX));
        Double orderCompletionRate = validOrderCount*1.0/totalOrderCount;

        List<LocalDate> time = new ArrayList<>();
        time.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            time.add(begin);
        }
        String dateList = StringUtils.join(time,",");
        List<Integer> orderCount = new ArrayList();
        List<Integer> validOrderCountArray = new ArrayList<>();
        for(LocalDate date:time){
            LocalDateTime beginDay = LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endDay = LocalDateTime.of(date,LocalTime.MAX);
            Integer count = orderMapper.count(beginDay,endDay);
            Integer valid = orderMapper.countValid(beginDay,endDay);
            validOrderCountArray.add(valid);
            orderCount.add(count);
        }
        String orderCountList = StringUtils.join(orderCount,",");
        String validOrderCountList = StringUtils.join(validOrderCountArray,",");
        return OrderReportVO.builder()
                .dateList(dateList)
                .orderCountList(orderCountList)
                .validOrderCountList(validOrderCountList)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }
}

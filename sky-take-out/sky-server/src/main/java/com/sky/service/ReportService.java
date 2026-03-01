package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);
}

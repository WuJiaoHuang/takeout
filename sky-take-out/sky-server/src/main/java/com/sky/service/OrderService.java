package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 分页查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单详情
     * @param id
     * @return
     */
    OrderVO orderDetail(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancel(Long id);

    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    void confirm(Long id);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void cancelAdmin(OrdersCancelDTO ordersCancelDTO);

    void delivery(Long id);

    void complete(Long id);
}

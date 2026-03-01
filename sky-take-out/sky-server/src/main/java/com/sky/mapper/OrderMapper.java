package com.sky.mapper;


import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {




    /**
     * 根据条件查询订单
     * @param orders
     * @return
     */
    List<Orders> select(Orders orders);

    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);


    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{checkOutTime} " +
            "where number = #{orderNumber}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime checkOutTime, String orderNumber);


    List<Orders> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    Double sumByMap(Map map);


    @Select("select  count(*) from orders where order_time > #{beginDay} and order_time < #{endDay}")
    Integer count(LocalDateTime beginDay, LocalDateTime endDay);

    @Select("select  count(*) from orders where order_time > #{beginDay} and order_time < #{endDay} and status not in (1,6,7)")
    Integer countValid(LocalDateTime beginDay, LocalDateTime endDay);

    @Select("select  count(*) from orders where order_time > #{beginDay} and order_time < #{endDay} and status = 5")
    Integer countCompleted(LocalDateTime beginDay, LocalDateTime endDay);
}

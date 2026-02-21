package com.sky.controller.admin;


import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("订单管理接口")
@RequestMapping("/admin/order")
@RestController("adminOrderController")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @ApiOperation("订单搜索")
    @GetMapping("/conditionSearch")
    public Result conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单搜索参数:{}",ordersPageQueryDTO);
        PageResult pageResult= orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("各个状态订单数量查询")
    @GetMapping("/statistics")
    public Result statistics(){
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success();
    }


    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result orderDetail(@PathVariable Long id){
        log.info("订单id:{}",id);
        OrderVO orderVO = orderService.orderDetail(id);
        return Result.success(orderVO);
    }


    /**
     * 接单
     * @param ordersDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersDTO ordersDTO){
        Long id = ordersDTO.getId();
        log.info("需要更改的订单id:{}",id);
        orderService.confirm(id);
        return Result.success();
    }

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("订单id:{} 拒单原因:{}",ordersRejectionDTO.getId(),ordersRejectionDTO.getRejectionReason());
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 商家取消订单
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("订单id:{} 取消原因:{}",ordersCancelDTO.getId(),ordersCancelDTO.getCancelReason());
        orderService.cancelAdmin(ordersCancelDTO);
        return Result.success();
    }


    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id){
        log.info("订单id:{} ",id);
        orderService.delivery(id);
        return Result.success();
    }


    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id){
        log.info("订单id:{}",id);
        orderService.complete(id);
        return Result.success();
    }

}

package com.sky.service.impl;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;


    @Autowired
    private OrderDetailMapper orderDetailMapper;


    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private UserMapper userMapper;

    //注入门店地址配送
    @Value("${sky.shop.address}")
    private String shopAddress;
    //注入百度AK
    @Value("${sky.baidu.ak}")
    private String baiduAk;
    private static final double MAX_DISTANCE_KM = 5.0;

    @Autowired
    private WeChatPayUtil weChatPayUtil;
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理各种异常(地址簿为空，购物车数据为空)
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            //抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }


        //拼接完整收货地址
        String userAddress = addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail();
        //使用trim()去掉首尾空格，防止地址前后有空格影响解析
        userAddress = userAddress.trim();
        //获取用户地址经纬度
        BaiduGeoResult userLocation = getLocationFromAddress(userAddress);
        // 获取门店地址经纬度
        BaiduGeoResult shopLocation = getLocationFromAddress(shopAddress);
        // 计算两点距离
        double distance = calculateDistance(shopLocation, userLocation);
        // 判断是否超出5公里
        if (distance > MAX_DISTANCE_KM) {
            throw new OrderBusinessException("超出配送范围");
        }























        //查询当前用户购物车数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList == null || shoppingCartList.size() == 0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(BaseContext.getCurrentId());
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setAddress(addressBook.getDetail());

        orderMapper.insert(orders);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        //向订单明细表插入多条数据
        for(ShoppingCart cart : shoppingCartList){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());//设置当前订单明细关联的订单id
            orderDetailList.add(orderDetail);
        }

        orderDetailMapper.insertBatch(orderDetailList);
        ShoppingCart s = new ShoppingCart();
        s.setUserId(userId);
        shoppingCartMapper.delete(s);
        com.sky.vo.OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId()).orderNumber(orders.getNumber()).orderAmount(orders.getAmount()).orderTime(orders.getOrderTime()).build();
        return orderSubmitVO;
    }










    /**
     * 根据地址获取经纬度（调用百度地理编码API）
     */
    private BaiduGeoResult getLocationFromAddress(String address) {
        try {
            // 1. 地址需要URL编码（处理中文）
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());

            // 2. 构建请求URL
            String url = "https://api.map.baidu.com/geocoding/v3/?address="
                    + encodedAddress
                    + "&output=json&ak=" + baiduAk;

            log.info("调用百度地理编码API");

            // 3. 发送HTTP请求（使用Hutool）
            String response = HttpUtil.get(url, 5000); // 5秒超时

            // 4. 解析JSON
            cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(response);
            int status = jsonObject.getInt("status");

            if (status != 0) {
                String msg = jsonObject.getStr("message");
                throw new RuntimeException("百度地图API返回错误：" + msg);
            }

            // 5. 提取经纬度
            cn.hutool.json.JSONObject location = jsonObject.getJSONObject("result")
                    .getJSONObject("location");
            double lng = location.getDouble("lng");
            double lat = location.getDouble("lat");

            log.info("地址解析成功：{} → ({}, {})", address, lng, lat);

            return new BaiduGeoResult(lng, lat);

        } catch (Exception e) {
            log.error("地址解析失败：{}", address, e);
            throw new RuntimeException("地址解析失败：" + e.getMessage());
        }
    }

    /**
     * 计算两点间距离（调用百度轻量级路线规划API）
     */
    private double calculateDistance(BaiduGeoResult origin, BaiduGeoResult destination) {
        try {
            // 1. 构建请求URL
            String url = "https://api.map.baidu.com/directionlite/v1/driving?" +
                    "origin=" + origin.getLat() + "," + origin.getLng() +
                    "&destination=" + destination.getLat() + "," + destination.getLng() +
                    "&ak=" + baiduAk;

            log.info("调用百度路线规划API");

            // 2. 发送请求
            String response = HttpUtil.get(url, 5000); // 5秒超时

            // 3. 解析JSON
            cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(response);
            int status = jsonObject.getInt("status");

            if (status != 0) {
                String msg = jsonObject.getStr("message");
                throw new RuntimeException("百度路线规划API返回错误：" + msg);
            }

            // 4. 提取距离（单位：米）
            cn.hutool.json.JSONObject result = jsonObject.getJSONObject("result");
            JSONArray routes = result.getJSONArray("routes");
            if (routes == null || routes.isEmpty()) {
                throw new RuntimeException("未找到可行路线");
            }

            double distanceInMeters = routes.getJSONObject(0).getDouble("distance");

            // 5. 转换为公里
            double distanceInKm = distanceInMeters / 1000;

            log.info("距离计算结果：{}米 = {}公里", distanceInMeters, distanceInKm);

            return distanceInKm;

        } catch (Exception e) {
            log.error("距离计算失败", e);
            throw new RuntimeException("距离计算失败：" + e.getMessage());
        }
    }

    /**
     * 内部类：存储经纬度
     */
    @Data
    @AllArgsConstructor
    private static class BaiduGeoResult {
        private double lng;
        private double lat;
    }
















    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        User user = userMapper.getById(userId);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        //为替代微信支付成功后的数据库订单状态更新，多定义一个方法进行修改
        Integer OrderPaidStatus = Orders.PAID; //支付状态，已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;  //订单状态，待接单

        //发现没有将支付时间 check_out属性赋值，所以在这里更新
        LocalDateTime check_out_time = LocalDateTime.now();

        //获取订单号码
        String orderNumber = ordersPaymentDTO.getOrderNumber();

        log.info("调用updateStatus，用于替换微信支付更新数据库状态的问题");
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, orderNumber);
        return vo;
    }

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
    }


    /**
     * 查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        Orders orders = Orders.builder().userId(userId).status(ordersPageQueryDTO.getStatus()).build();

        List<Orders> list1 = orderMapper.select(orders);
        List<OrderVO> result = new ArrayList<>();
        for(Orders o:list1){
            Long orderId = o.getId();
            List<OrderDetail> list2=orderDetailMapper.selectByOrderId(orderId);
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(o,orderVO);
            orderVO.setOrderDetailList(list2);
            result.add(orderVO);
        }
        return new PageResult(result.size(),result);

    }


    /**
     * 根据orderId查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO orderDetail(Long id) {
        Orders orders = Orders.builder().id(id).build();
        Orders o1 =  orderMapper.select(orders).get(0);
        OrderVO result = new OrderVO();
        BeanUtils.copyProperties(o1,result);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(id);
        result.setOrderDetailList(orderDetailList);
        return result;
    }

    /**
     * 根据订单id取消订单
     * @param id
     */
    @Override
    @Transactional
    public void cancel(Long id) {

        //查询订单状态
        Orders orderDB = orderMapper.select(Orders.builder().id(id).build()).get(0);
        if(orderDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(orderDB.getStatus()>2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder().id(id).status(6).cancelReason("用户取消").cancelTime(LocalDateTime.now()).build();
        if(orderDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            orders.setPayStatus(Orders.REFUND);
        }
        orderMapper.update(orders);
    }








    /**
     * 再来一单
     *
     * @param id
     */

    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());


        for(ShoppingCart s:shoppingCartList){
            shoppingCartMapper.insert(s);
        }
    }

    /**
     * 根据条件订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        List<Orders> result = orderMapper.conditionSearch(ordersPageQueryDTO);
        return new PageResult(result.size(),result);
    }

    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO orderStatisticsVO = orderMapper.statistics();
        return orderStatisticsVO;
    }


    /**
     * 根据订单id接单
     * @param id
     */
    @Override
    public void confirm(Long id) {
        //先查询状态
        Orders order = orderMapper.select(Orders.builder().id(id).build()).get(0);
        if(order==null || order.getStatus()!=Orders.TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orderMapper.update(Orders.builder().id(id).status(Orders.CONFIRMED).build());
    }

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Long id = ordersRejectionDTO.getId();
        //先查询状态
        Orders order = orderMapper.select(Orders.builder().id(id).build()).get(0);
        if(order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
        if(order.getStatus() == 1){
            orderMapper.update(Orders.builder().id(id).status(6).cancelReason(ordersRejectionDTO.getRejectionReason()).cancelTime(LocalDateTime.now()).build());
            return;
        }
        else if(order.getStatus() == 2){
            orderMapper.update(Orders.builder().id(id).status(7).rejectionReason(ordersRejectionDTO.getRejectionReason()).payStatus(Orders.REFUND).cancelTime(LocalDateTime.now()).build());
            return;
        }
        throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
    }

    /**
     * 商家取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancelAdmin(OrdersCancelDTO ordersCancelDTO) {
        Long id = ordersCancelDTO.getId();
        //先查询状态
        Orders order = orderMapper.select(Orders.builder().id(id).build()).get(0);
        if(order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
        if(order.getStatus() == 1){
            orderMapper.update(Orders.builder().id(id).status(6).cancelReason(ordersCancelDTO.getCancelReason()).cancelTime(LocalDateTime.now()).build());
            return;
        }
        if(order.getStatus() == 3){
            orderMapper.update(Orders.builder().id(id).status(6).cancelReason(ordersCancelDTO.getCancelReason()).payStatus(Orders.REFUND).cancelTime(LocalDateTime.now()).build());
            return;
        }
        throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {
        //先查询状态
        Orders order = orderMapper.select(Orders.builder().id(id).build()).get(0);
        if(order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(order.getStatus()!=3){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orderMapper.update(Orders.builder().id(id).status(4).build());
    }

    @Override
    public void complete(Long id) {
        //先查询状态
        Orders order = orderMapper.select(Orders.builder().id(id).build()).get(0);
        if(order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(order.getStatus()!=4){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orderMapper.update(Orders.builder().id(id).status(5).build());

    }


}

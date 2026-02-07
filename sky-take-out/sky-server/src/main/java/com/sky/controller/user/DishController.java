package com.sky.controller.user;


import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@Slf4j
@Api("菜品浏览")
@RequestMapping("/user/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    public Result selectWithFlavorByCategoryId(Long categoryId) {
        log.info("需要查询的categoryId:{}",categoryId);
        //构造redis中的key dish_分类id
        String key = "dish"+categoryId.toString();

        //查询redis中是否存在菜品数据
        List<DishVO>list = (List<DishVO>)redisTemplate.opsForValue().get(key);
        if(list != null && list.size()>0 ){
            //如果存在，直接返回，无需查询数据库
            return Result.success(list);
        }
        //如果不存在
        List<DishVO> result = dishService.selectWithFlavorByCategoryId(categoryId);
        redisTemplate.opsForValue().set(key,result);
        return Result.success(result);
    }
}

package com.sky.controller.user;


import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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


    @GetMapping("/list")
    public Result selectWithFlavorByCategoryId(Long categoryId) {
        log.info("需要查询的categoryId:{}",categoryId);
        List<DishVO> result = dishService.selectWithFlavorByCategoryId(categoryId);
        return Result.success(result);
    }
}

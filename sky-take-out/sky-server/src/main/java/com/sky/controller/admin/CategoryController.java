package com.sky.controller.admin;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@Slf4j
@RequestMapping("/admin/category")
@Service
@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result AddCategory(@RequestBody CategoryDTO categoryDTO){
        categoryService.AddCategory(categoryDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result ListByPage(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("传入参数:{}",categoryPageQueryDTO);
         PageResult result = categoryService.ListByPage(categoryPageQueryDTO);
        return Result.success(result);
    }

    @DeleteMapping
    public Result deleteById(Integer id){
        log.info("根据id删除",id);
        categoryService.deleteById(id);
        return Result.success();
    }
    @PutMapping
    public Result updateById(@RequestBody CategoryDTO categoryDTO){
        categoryService.updateById(categoryDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result setStatus(@PathVariable Integer status,Long id){
        log.info("更改状态:{}",status);
        categoryService.setStatus(status,id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result selectByType(Integer type){
        ArrayList<Category> result = categoryService.list(type);
        return Result.success(result);
    }

}

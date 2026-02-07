package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.ArrayList;

public interface CategoryService {
    void AddCategory(CategoryDTO categoryDTO);

    PageResult ListByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    void deleteById(Integer id);

    void updateById(CategoryDTO categoryDTO);

    void setStatus(Integer status, Long id);

    ArrayList<Category> list(Category c);
}

package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;
    @Override
    public void AddCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(0);
        categoryMapper.AddCategory(category);

    }

    @Override
    public PageResult ListByPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        //将DTO转换为实体对象
        Category condition = Category.builder().name(categoryPageQueryDTO.getName()).type(categoryPageQueryDTO.getType()).build();

        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        Page<Category> result= (Page<Category>)categoryMapper.queryCategories(condition);
        return new PageResult(result.getTotal(),result.getResult());
    }

    @Override
    public void deleteById(Integer id) {
        categoryMapper.deleteByID(id);
    }

    @Override
    public void updateById(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        categoryMapper.update(category);
    }

    @Override
    public void setStatus(Integer status, Long id) {
        Category category =
                Category.builder()
                        .id(id)
                        .status(status)
                        .build();
        categoryMapper.update(category);
    }

    @Override
    public ArrayList<Category> list(Integer type) {
        Category c = Category.builder().type(type).build();
        List<Category> list = categoryMapper.queryCategories(c);
        return new ArrayList<>(list);
    }

}

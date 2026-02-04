package com.sky.service;

import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {


    void addSetmeal(SetmealDTO setmealDTO);

    PageResult selectPage(SetmealPageQueryDTO setmealPageQueryDTO);

    void updateSetmeal(SetmealDTO setmealDTO);

    SetmealVO selectById(Long id);

    void updateStatus(Long id, Integer status);

    void deleteById(List<Long> ids);
}

package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;


public interface DishService {
    //新增菜品和口味
    public void saveWithFloavor(DishDTO dishDTO);

    PageResult listByPage(DishPageQueryDTO q);

    void deleteBatch(List<Long> ids);

    DishVO getByIdWithFlavor(Long id);

    List<DishVO> listWithFlavor(Dish dish);
    void updateWithFlavor(DishDTO dishDTO);

    void updateStatus(Integer status,Long id);

    List<Dish> getByCategoryId(Long categoryId);

    List<DishVO> selectWithFlavorByCategoryId(Long categoryId);
}

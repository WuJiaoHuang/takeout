package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    void addSetmeal(List<SetmealDish> meals,long setMealid);

    @Select("select * from setmeal_dish where dish_id = id")
    Dish selectByDishId(Long id);

    @Delete("delete from setmeal_dish where setmeal_id = #{setMealId}")
    void deleteBysetmeal_id(Long setMealId);

    @Select("select * from setmeal_dish where setmeal_id = #{setMealId}")
    List<SetmealDish> selectByCategoryId(Long setMealId);
}

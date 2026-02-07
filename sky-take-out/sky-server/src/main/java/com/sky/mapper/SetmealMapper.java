package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface SetmealMapper {


    @AutoFill(OperationType.INSERT)
    int addSetmeal(Setmeal setmeal);


    List<SetmealVO> selectByPage(SetmealPageQueryDTO setmealPageQueryDTO);

    @AutoFill(OperationType.UPDATE)
    void updateSetmeal(Setmeal setmeal);

    @Select("select * from setmeal where id = #{id}")
    Setmeal selectById(Long id);

    @Delete("delete from setmeal where id = #{id}")
    void deleteById(Long id);

    @Select("select * from setmeal where category_id=#{categoryId} and status = 1")
    List<Setmeal> selectByCategoryId(Long categoryId);

    List<DishItemVO> selectDishBySetmealId(Long id);
}

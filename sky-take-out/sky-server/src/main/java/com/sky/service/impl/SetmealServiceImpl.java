package com.sky.service.impl;

import com.github.pagehelper.Page;import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishPageQueryDTO;import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
@Autowired
private SetmealMapper setmealMapper;
@Autowired
private SetmealDishMapper setmealDishMapper;
@Autowired
private DishMapper dishMapper;

@Override
    @Transactional
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        //先插入setmeal
        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealMapper.addSetmeal(setmeal);
        Long setMealid = setmeal.getId();
        List<SetmealDish> meals = setmealDTO.getSetmealDishes();
        for(SetmealDish setmealDish: meals){

            if(dishMapper.list(Dish.builder().id(setmealDish.getDishId()).build()).get(0).getStatus()==0){
                throw new SetmealEnableFailedException("无法将已经停售的商品加入套餐中");
            }
        }
        setmealDishMapper.addSetmeal(meals,setMealid);


    }
    @Override
    public PageResult selectPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO>  result = (Page<SetmealVO>) setmealMapper.selectByPage(setmealPageQueryDTO);
        return new PageResult(result.getTotal(),result.getResult());
    }

    @Override
    @Transactional
    public void updateSetmeal(SetmealDTO setmealDTO) {
        //更新setmeal表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.updateSetmeal(setmeal);

        //删除原有所有套餐

        Long setMealid = setmealDTO.getId();
        setmealDishMapper.deleteBysetmeal_id(setMealid);
        List<SetmealDish> meals = setmealDTO.getSetmealDishes();
        for(SetmealDish setmealDish: meals){

            if(dishMapper.list(Dish.builder().id(setmealDish.getDishId()).build()).get(0).getStatus()==0){
                throw new SetmealEnableFailedException("无法将已经停售的商品加入套餐中");
            }
        }
        setmealDishMapper.addSetmeal(meals,setMealid);

    }

    @Override
    public SetmealVO selectById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        //先查找setmeal数据库
        Setmeal setmeal=  setmealMapper.selectById(id);
        //再查找setmeal_dish数据库
        List<SetmealDish> setmealDishes = setmealDishMapper.selectByCategoryId(id);
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        //更新setmeal表
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        setmealMapper.updateSetmeal(setmeal);
    }

    @Override
    @Transactional
    public void deleteById(List<Long> ids) {
        for(Long id:ids){
            //查找套餐状态，起售中不允许删除
            if(setmealMapper.selectById(id).getStatus()==1){
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ON_SALE);
            }
            setmealMapper.deleteById(id);
            setmealDishMapper.deleteBysetmeal_id(id);
        }
    }

}

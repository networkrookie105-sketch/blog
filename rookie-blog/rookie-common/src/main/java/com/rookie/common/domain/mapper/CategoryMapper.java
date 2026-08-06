package com.rookie.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rookie.common.domain.dos.CategoryDO;

/**
 * @author peishunwu
 * @ClassName CategoryMapper
 * @description: TODO
 * @date 2024年04月23日
 * @version: 1.0
 */
public interface CategoryMapper extends BaseMapper<CategoryDO> {
    /**
     * 根据分类名称查询分类
     * @param categoryName
     * @return
     */
    default CategoryDO findByName(String categoryName){
        // 构建查询条件
        LambdaQueryWrapper<CategoryDO> wrapper = new LambdaQueryWrapper<>();
        // 精确匹配，避免 like 导致的重名误判以及 selectOne 命中多行抛异常
        wrapper.eq(CategoryDO::getName, categoryName).last("LIMIT 1");
        // 执行查询
        return selectOne(wrapper);
    }
}

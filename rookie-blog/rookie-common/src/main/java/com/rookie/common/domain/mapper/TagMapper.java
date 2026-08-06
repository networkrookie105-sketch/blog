package com.rookie.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rookie.common.domain.dos.TagDo;

import java.util.List;

/**
 * @author peishunwu
 * @ClassName TagMapper
 * @description: TODO
 * @date 2024年05月03日
 * @version: 1.0
 */
public interface TagMapper extends BaseMapper<TagDo> {
    /**
     * 根据标签名称查询标签
     * @param tagName
     * @return
     */
    default TagDo findByName(String tagName){
        // 构建查询条件
        LambdaQueryWrapper<TagDo> wrapper = new LambdaQueryWrapper<>();
        // 精确匹配，避免 like 导致的重名误判以及 selectOne 命中多行抛异常
        wrapper.eq(TagDo::getName, tagName).last("LIMIT 1");
        return selectOne(wrapper);
    }
    /**
     * 根据标签 ID 批量查询
     * @param tagIds
     * @return
     */
    default List<TagDo> selectByIds(List<Long> tagIds) {
        return selectList(Wrappers.<TagDo>lambdaQuery()
                .in(TagDo::getId, tagIds));
    }
}

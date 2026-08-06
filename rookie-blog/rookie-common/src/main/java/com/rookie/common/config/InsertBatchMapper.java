package com.rookie.common.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author peishunwu
 * @ClassName InsertBatchMapper
 * @description: TODO
 * @date 2024年05月30日
 * @version: 1.0
 */
public interface InsertBatchMapper<T> extends BaseMapper<T> {
    /**
     * 批量插入（自定义 SQL 实现）
     * @param batchList 实体对象集合
     * @return
     */
    int insertBatchSomeColumn(@Param("list") List<T> batchList);
}

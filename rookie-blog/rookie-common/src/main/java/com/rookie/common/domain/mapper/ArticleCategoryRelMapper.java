package com.rookie.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rookie.common.domain.dos.ArticleCategoryRelDO;

import java.util.List;

/**
 * @author peishunwu
 * @ClassName ArticleCategoryRelMapper
 * @description: TODO
 * @date 2024年05月27日
 * @version: 1.0
 */
public interface ArticleCategoryRelMapper extends BaseMapper<ArticleCategoryRelDO> {

    /**
     * 根据文章id删除文章分类关联
     * @param articleId
     * @return
     */
    default int deleteByArticleId(Long articleId) {
        return delete(Wrappers.<ArticleCategoryRelDO>lambdaQuery().eq(ArticleCategoryRelDO::getArticleId, articleId));
    }

    /**
     * 根据文章id查询文章分类关联
     * @param articleId
     * @return
     */
    default ArticleCategoryRelDO selectByArticleId(Long articleId) {
        return selectOne(Wrappers.<ArticleCategoryRelDO>lambdaQuery().eq(ArticleCategoryRelDO::getArticleId, articleId));
    }

    /**
     * 根据分类 ID 查询
     * @param categoryId
     * @return
     */
    default ArticleCategoryRelDO selectOneByCategoryId(Long categoryId) {
        return selectOne(Wrappers.<ArticleCategoryRelDO>lambdaQuery()
                .eq(ArticleCategoryRelDO::getCategoryId, categoryId)
                .last("LIMIT 1"));
    }

    default List<ArticleCategoryRelDO> selectByArticleIds(List<Long> articleIds) {
        return selectList(Wrappers.<ArticleCategoryRelDO>lambdaQuery().in(ArticleCategoryRelDO::getArticleId, articleIds));
    }

    /**
     * 根据分类id查询所有关联文章的记录
     */
    default List<ArticleCategoryRelDO> selectListByCategoryId(Long categoryId) {
        return selectList(Wrappers.<ArticleCategoryRelDO>lambdaQuery().eq(ArticleCategoryRelDO::getCategoryId, categoryId));
    }


}

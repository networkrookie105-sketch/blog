package com.rookie.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rookie.common.config.InsertBatchMapper;
import com.rookie.common.domain.dos.ArticleCategoryRelDO;
import com.rookie.common.domain.dos.ArticleTagRelDO;

import java.util.List;

/**
 * @author peishunwu
 * @ClassName ArticleTagRelMapper
 * @description: TODO
 * @date 2024年05月27日
 * @version: 1.0
 */
public interface ArticleTagRelMapper extends InsertBatchMapper<ArticleTagRelDO> {

    /**
     * 根据文章id删除文章标签关联
     * @param articleId
     * @return
     */
    default int deleteByArticleId(Long articleId) {
        return delete(Wrappers.<ArticleTagRelDO>lambdaQuery().eq(ArticleTagRelDO::getArticleId, articleId));
    }

    /**
     * 根据文章id查询文章标签关联
     * @param articleId
     * @return
     */
    default List<ArticleTagRelDO> selectByArticleId(Long articleId) {
        return selectList(Wrappers.<ArticleTagRelDO>lambdaQuery().eq(ArticleTagRelDO::getArticleId, articleId));
    }
    /**
     * 根据标签 ID 查询
     * @param tagId
     * @return
     */
    default ArticleTagRelDO selectOneByTagId(Long tagId) {
        return selectOne(Wrappers.<ArticleTagRelDO>lambdaQuery()
                .eq(ArticleTagRelDO::getTagId, tagId)
                .last("LIMIT 1"));
    }

    /**
     * 根据文章 ID 集合批量查询
     * @param articleIds
     * @return
     */
    default List<ArticleTagRelDO> selectByArticleIds(List<Long> articleIds) {
        return selectList(Wrappers.<ArticleTagRelDO>lambdaQuery().in(ArticleTagRelDO::getArticleId, articleIds));
    }


    /**
     * 查询该标签 ID 下所有关联记录文章
     * @param tagId
     * @return
     */
    default List<ArticleTagRelDO> selectByTagId(Long tagId) {
        return selectList(Wrappers.<ArticleTagRelDO>lambdaQuery()
                .eq(ArticleTagRelDO::getTagId, tagId));
    }
}

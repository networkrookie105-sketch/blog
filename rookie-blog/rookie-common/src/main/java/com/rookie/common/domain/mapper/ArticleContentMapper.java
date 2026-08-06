package com.rookie.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rookie.common.domain.dos.ArticleContentDO;


/**
 * @author peishunwu
 * @ClassName ArticleContentDO
 * @description: TODO
 * @date 2024年05月27日
 * @version: 1.0
 */
public interface ArticleContentMapper extends BaseMapper<ArticleContentDO> {

    /**
     * 根据文章id查询文章内容
     * @param articleId
     * @return
     */
    default int deleteByArticleIdInt(Long articleId) {
        return delete(Wrappers.<ArticleContentDO>lambdaQuery().eq(ArticleContentDO::getArticleId, articleId));
    }

    /**
     * 根据文章id查询文章内容
     * @param articleId
     * @return
     */
    default ArticleContentDO selectByArticleId(Long articleId) {
        return selectOne(Wrappers.<ArticleContentDO>lambdaQuery().eq(ArticleContentDO::getArticleId, articleId));
    }

    /**
     * 根据文章id更新文章内容
     * @param articleContentDO
     * @return
     */
    default int updateByArticleId(ArticleContentDO articleContentDO) {
        return update(articleContentDO, Wrappers.<ArticleContentDO>lambdaUpdate().eq(ArticleContentDO::getArticleId, articleContentDO.getArticleId()));
    }

    /**
     * 根据正文关键字模糊查询命中的文章 ID 列表（用于全文搜索）
     * @param keyword 关键字
     * @return 文章 ID 列表
     */
    default java.util.List<Long> selectArticleIdsByContentKeyword(String keyword) {
        return selectList(Wrappers.<ArticleContentDO>lambdaQuery()
                .select(ArticleContentDO::getArticleId)
                .like(ArticleContentDO::getContent, keyword))
                .stream()
                .map(ArticleContentDO::getArticleId)
                .collect(java.util.stream.Collectors.toList());
    }
}

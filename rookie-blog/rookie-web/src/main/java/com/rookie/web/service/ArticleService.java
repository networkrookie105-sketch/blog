package com.rookie.web.service;

import com.rookie.common.utils.Response;
import com.rookie.web.model.vo.article.FindArticleDetailReqVO;
import com.rookie.web.model.vo.article.FindIndexArticlePageListReqVO;
import com.rookie.web.model.vo.article.SearchArticlePageListReqVO;

/**
 * @author peishunwu
 * @ClassName ArticleService
 * @description: TODO
 * @date 2024年08月31日
 * @version: 1.0
 */
public interface ArticleService {
    /**
     * 首页查询文章分页
     * @param reqVO
     * @return
     */
    Response findIndexArticlePageList(FindIndexArticlePageListReqVO reqVO);

    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

    /**
     * 全文搜索文章
     * @param reqVO
     * @return
     */
    Response searchArticles(SearchArticlePageListReqVO reqVO);

}

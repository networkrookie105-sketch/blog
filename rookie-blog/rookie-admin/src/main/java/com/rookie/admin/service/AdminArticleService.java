package com.rookie.admin.service;

import com.rookie.admin.model.vo.article.*;
import com.rookie.common.utils.Response;

/**
 * @author peishunwu
 * @ClassName AdminArticleService
 * @description: TODO
 * @date 2024年05月28日
 * @version: 1.0
 */
public interface AdminArticleService {
    /**
     * 发布文章
     * @param publishArticleReqVO
     * @return
     */
    Response  publishArticle(PublishArticleReqVO publishArticleReqVO);

    Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO);

    Response findArticlePageList(FindArticlePageListReqVO findArticlePageListReqVO);

    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

    Response updateArticle(UpdateArticleReqVO updateArticleReqVO);
}

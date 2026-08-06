package com.rookie.web.service;

import com.rookie.common.utils.Response;
import com.rookie.web.model.vo.article.FindArchiveArticlePageListReqVO;

/**
 * @author peishunwu
 * @ClassName ArchiveService
 * @description: TODO
 * @date 2025年08月11日
 * @version: 1.0
 */
public interface ArchiveService {
    /**
     * 查询文章归档分页列表
     * @param reqVO
     * @return
     */
    Response findArchivePageList(FindArchiveArticlePageListReqVO reqVO);
}

package com.rookie.web.service;

import com.rookie.common.utils.Response;
import com.rookie.web.model.vo.category.FindCategoryArticlePageListReqVO;

/**
 * @author peishunwu
 * @ClassName CategoryService
 * @description: TODO
 * @date 2024年09月04日
 * @version: 1.0
 */
public interface CategoryService {
    /**
     * 查询分类列表
     * @return
     */
    Response findCategoryList();

    /**
     * 查询分类文章分页列表
     * @param reqVO
     * @return
     */
    Response findCategoryArticlePageList(FindCategoryArticlePageListReqVO reqVO);
}

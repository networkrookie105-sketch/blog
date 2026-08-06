package com.rookie.admin.service;

import com.rookie.admin.model.vo.category.AddCategoryReqVO;
import com.rookie.admin.model.vo.category.DeleteCategoryReqVO;
import com.rookie.admin.model.vo.category.FindCategoryPageListReqVO;
import com.rookie.common.utils.PageResponse;
import com.rookie.common.utils.Response;

/**
 * @author peishunwu
 * @ClassName AdminCategoryService
 * @description: TODO
 * @date 2024年04月23日
 * @version: 1.0
 */
public interface AdminCategoryService {
    /**
     * 添加分类
     * @param addCategoryReqVO
     * @return
     */
    Response addCategory(AddCategoryReqVO addCategoryReqVO);

    /**
     * 分类分页数据获取
     * @param findCategoryPageListReqVO
     * @return
     */
    PageResponse findCategoryPageList(FindCategoryPageListReqVO findCategoryPageListReqVO);

    /**
     * 删除分类
     * @param deleteCategoryReqVO
     * @return
     */
    Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO);

    /**
     * 获取分类下拉列表
     * @return
     */
    Response findCategorySeclectList();
}

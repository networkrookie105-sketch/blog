package com.rookie.web.service;

import com.rookie.common.utils.Response;
import com.rookie.web.model.vo.tag.FindTagArticlePageListReqVO;

/**
 * @author peishunwu
 * @ClassName TagService
 * @description: TODO
 * @date 2024年09月04日
 * @version: 1.0
 */
public interface TagService {

    /**
     * 查询标签列表
     * @return
     */
    Response findTagList();

    /**
     * 获取标签下文章分页列表
     * @param findTagArticlePageListReqVO
     * @return
     */
    Response findTagPageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO);
}

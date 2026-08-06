package com.rookie.admin.service;

import com.rookie.admin.model.vo.tag.AddTagReqVO;
import com.rookie.admin.model.vo.tag.DeleteTagReqVO;
import com.rookie.admin.model.vo.tag.FindTagPageListReqVO;
import com.rookie.common.utils.PageResponse;
import com.rookie.common.utils.Response;

/**
 * @author peishunwu
 * @ClassName AdminTagService
 * @description: TODO
 * @date 2024年05月03日
 * @version: 1.0
 */
public interface AdminTagService {
    Response addTag(AddTagReqVO addTagReqVO);

    PageResponse findTagPageList(FindTagPageListReqVO findTagPageListReqVO);

    Response deleteTag(DeleteTagReqVO deleteTagReqVO);

    Response findTagSeclectList(String name);

    /**
     * 查询标签 Select 列表数据,用于前端下拉列表展示回显
     * @return
     */
    Response findTagSelectList();
}

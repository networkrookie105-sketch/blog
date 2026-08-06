package com.rookie.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.common.domain.dos.ArticleCategoryRelDO;
import com.rookie.common.domain.dos.ArticleDO;
import com.rookie.common.domain.dos.CategoryDO;
import com.rookie.common.domain.mapper.ArticleCategoryRelMapper;
import com.rookie.common.domain.mapper.ArticleMapper;
import com.rookie.common.domain.mapper.CategoryMapper;
import com.rookie.common.enums.ResponseCodeEnum;
import com.rookie.common.exception.BizException;
import com.rookie.common.utils.PageResponse;
import com.rookie.common.utils.Response;
import com.rookie.web.convert.ArticleConvert;
import com.rookie.web.model.vo.category.FindCategoryArticlePageListReqVO;
import com.rookie.web.model.vo.category.FindCategoryArticlePageListRspVO;
import com.rookie.web.model.vo.category.FindCategoryListRspVO;
import com.rookie.common.constant.CacheConstants;
import com.rookie.web.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author peishunwu
 * @ClassName CategoryServiceImpl
 * @description: TODO
 * @date 2024年09月04日
 * @version: 1.0
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Override
    @Cacheable(cacheNames = CacheConstants.CATEGORY_LIST)
    public Response findCategoryList() {
        //查询所有分类
        List<CategoryDO> categoryList = categoryMapper.selectList(Wrappers.emptyWrapper());
        List<FindCategoryListRspVO> categoryListRspVO = null;
        if (!CollectionUtils.isEmpty(categoryList)) {
            categoryListRspVO = categoryList.stream().map(categoryDO -> {
                FindCategoryListRspVO rspVO = new FindCategoryListRspVO();
                rspVO.setId(categoryDO.getId());
                rspVO.setName(categoryDO.getName());
                return rspVO;
            }).collect(Collectors.toList());
        }
        return Response.success(categoryListRspVO);
    }

    @Override
    public Response findCategoryArticlePageList(FindCategoryArticlePageListReqVO reqVO) {
        Long current = reqVO.getCurrent();
        Long size = reqVO.getSize();
        Long categoryId = reqVO.getId();
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);

        // 判断该分类是否存在
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 该分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }
        // 先查询该分类下所有关联的文章 ID
        List<ArticleCategoryRelDO> articleCategoryRelDOS = articleCategoryRelMapper.selectListByCategoryId(categoryId);
        // 若该分类下未发布任何文章
        if (CollectionUtils.isEmpty(articleCategoryRelDOS)) {
            log.info("==> 该分类下还未发布任何文章, categoryId: {}", categoryId);
            return PageResponse.success(null, null);
        }
        List<Long> articleIds = articleCategoryRelDOS.stream().map(ArticleCategoryRelDO::getArticleId).collect(Collectors.toList());

        // 根据文章 ID 集合查询文章分页数据
        Page<ArticleDO> page = articleMapper.selectPageListByArticleIds(current, size, articleIds);
        List<ArticleDO> articleDOS = page.getRecords();

        // DO 转 VO
        List<FindCategoryArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2CategoryArticleVO(articleDO))
                    .collect(Collectors.toList());
        }

        return PageResponse.success(page, vos);
    }
}

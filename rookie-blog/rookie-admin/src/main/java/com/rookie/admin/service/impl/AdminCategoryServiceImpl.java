package com.rookie.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.admin.model.vo.category.AddCategoryReqVO;
import com.rookie.admin.model.vo.category.DeleteCategoryReqVO;
import com.rookie.admin.model.vo.category.FindCategoryPageListReqVO;
import com.rookie.admin.model.vo.category.FindCategoryPageListRspVO;
import com.rookie.admin.service.AdminCategoryService;
import com.rookie.common.domain.dos.ArticleCategoryRelDO;
import com.rookie.common.domain.dos.CategoryDO;
import com.rookie.common.domain.mapper.ArticleCategoryRelMapper;
import com.rookie.common.domain.mapper.CategoryMapper;
import com.rookie.common.enums.ResponseCodeEnum;
import com.rookie.common.exception.BizException;
import com.rookie.common.constant.CacheConstants;
import com.rookie.common.model.vo.SelectRspVO;
import com.rookie.common.utils.PageResponse;
import com.rookie.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author peishunwu
 * @ClassName AdminCategoryServiceImpl
 * @description: TODO
 * @date 2024年04月23日
 * @version: 1.0
 */
@Service
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    /**
     * 添加分类
     * @param addCategoryReqVO
     * @return
     */
    @Override
    @CacheEvict(cacheNames = CacheConstants.CATEGORY_LIST, allEntries = true)
    public Response addCategory(AddCategoryReqVO addCategoryReqVO) {
        // 先判断该分类是否已经存在
        CategoryDO categoryDO = categoryMapper.findByName(addCategoryReqVO.getName());
        if (Objects.nonNull(categoryDO)){
            log.warn("分类名称：{},此分类已存在", addCategoryReqVO.getName());
            throw new BizException(ResponseCodeEnum.CATEGORY_NAME_IS_EXISTED);
        }
        //构建分类对象
        CategoryDO insertCategoryDO  = CategoryDO.builder()
                .name(addCategoryReqVO.getName().trim())
                .build();
        // 插入分类
        categoryMapper.insert(insertCategoryDO );
        return Response.success(insertCategoryDO);
    }

    @Override
    public PageResponse findCategoryPageList(FindCategoryPageListReqVO findCategoryPageListReqVO) {
        //获取当前页、以及每页需要的数据量
        Long currentPage = findCategoryPageListReqVO.getCurrent();
        Long pageSize = findCategoryPageListReqVO.getSize();
        // 分页对象(查询第几页、每页多少数据)
        Page<CategoryDO> page = new Page<>(currentPage, pageSize);
        // 构建查询条件
        LambdaQueryWrapper<CategoryDO> wrapper = new LambdaQueryWrapper<>();

        String name = findCategoryPageListReqVO.getName();
        LocalDate startDate = findCategoryPageListReqVO.getStartDate();
        LocalDate endDate = findCategoryPageListReqVO.getEndDate();
        wrapper
                .like(StringUtils.isNotBlank(name), CategoryDO::getName, name.trim()) // like 模块查询
                .ge(Objects.nonNull(startDate), CategoryDO::getCreateTime, startDate) // 大于等于 startDate
                .le(Objects.nonNull(endDate), CategoryDO::getCreateTime, endDate)  // 小于等于 endDate
                .orderByDesc(CategoryDO::getCreateTime); // 按创建时间倒叙
        Page<CategoryDO> categoryDOPage = categoryMapper.selectPage(page, wrapper);
        List<CategoryDO> categoryDOS = categoryDOPage.getRecords();
        // DO 转 VO
        List<FindCategoryPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            vos = categoryDOS.stream()
                    .map(categoryDO -> FindCategoryPageListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .createTime(categoryDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(categoryDOPage, vos);
    }

    @Override
    @CacheEvict(cacheNames = CacheConstants.CATEGORY_LIST, allEntries = true)
    public Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO) {
        // 分类 ID
        Long categoryId = deleteCategoryReqVO.getId();
        // 先判断该分类下是否存在关联文章，若有则只有删除分类下所有文章才能删除
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectOneByCategoryId(categoryId);
        if (Objects.nonNull(articleCategoryRelDO)){
            log.warn("分类 ID：{},此分类下存在关联文章，无法删除", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_CAN_NOT_DELETE);
        }
        // 删除分类
        categoryMapper.deleteById(categoryId);

        return Response.success();
    }

    @Override
    public Response findCategorySeclectList() {
        List<CategoryDO> categoryDOS = categoryMapper.selectList(null);
        List<SelectRspVO> selectRspVOS = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            selectRspVOS = categoryDOS.stream()
                    .map(categoryDO -> SelectRspVO.builder()
                            .label(categoryDO.getName())
                            .value(categoryDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }
        return Response.success(selectRspVOS);
    }
}

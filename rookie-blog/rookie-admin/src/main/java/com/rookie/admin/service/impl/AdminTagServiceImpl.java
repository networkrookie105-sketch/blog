package com.rookie.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.admin.model.vo.tag.AddTagReqVO;
import com.rookie.admin.model.vo.tag.DeleteTagReqVO;
import com.rookie.admin.model.vo.tag.FindTagPageListReqVO;
import com.rookie.admin.model.vo.tag.FindTagPageListRspVO;
import com.rookie.admin.service.AdminTagService;
import com.rookie.common.domain.dos.ArticleTagRelDO;
import com.rookie.common.domain.dos.TagDo;
import com.rookie.common.domain.mapper.ArticleTagRelMapper;
import com.rookie.common.domain.mapper.TagMapper;
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
 * @ClassName AdminTagServiceImpl
 * @description: TODO
 * @date 2024年05月03日
 * @version: 1.0
 */
@Service
@Slf4j
public class AdminTagServiceImpl implements AdminTagService {
    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Override
    @CacheEvict(cacheNames = CacheConstants.TAG_LIST, allEntries = true)
    public Response addTag(AddTagReqVO addTagReqVO) {
        TagDo tagDo = tagMapper.findByName(addTagReqVO.getName());
        if (Objects.nonNull(tagDo)){
            log.warn("标签名称：{},此标签已存在", addTagReqVO.getName());
            throw new BizException(ResponseCodeEnum.CATEGORY_NAME_IS_EXISTED);
        }
        TagDo insertTagDo = TagDo.builder()
                .name(addTagReqVO.getName().trim())
                .build();
        tagMapper.insert(insertTagDo);
        return Response.success(insertTagDo);
    }

    @Override
    public PageResponse findTagPageList(FindTagPageListReqVO findTagPageListReqVO) {
        //获取当前页、以及每页需要的数据量
        Long currentPage = findTagPageListReqVO.getCurrent();
        Long pageSize = findTagPageListReqVO.getSize();
        // 分页对象(查询第几页、每页多少数据)
        Page<TagDo> page = new Page<>(currentPage, pageSize);
        //构建查询条件
        LambdaQueryWrapper<TagDo> queryWrapper = new LambdaQueryWrapper<>();
        String name = findTagPageListReqVO.getName();
        LocalDate startDate = findTagPageListReqVO.getStartDate();
        LocalDate endDate = findTagPageListReqVO.getEndDate();
        queryWrapper.like(StringUtils.isNotBlank(name), TagDo::getName, name)
                .ge(Objects.nonNull(startDate), TagDo::getCreateTime, startDate)
                .le(Objects.nonNull(endDate), TagDo::getCreateTime, endDate);
        Page<TagDo> tagDoPage = tagMapper.selectPage(page, queryWrapper);
        List<FindTagPageListRspVO> vos = null;
        if (tagDoPage.getRecords().size() > 0){
            vos = tagDoPage.getRecords().stream().map(tagDo -> FindTagPageListRspVO.builder()
                    .id(tagDo.getId())
                    .name(tagDo.getName())
                    .createTime(tagDo.getCreateTime())
                    .build()).collect(Collectors.toList());
        }

        return PageResponse.success(tagDoPage, vos);
    }

    @Override
    @CacheEvict(cacheNames = CacheConstants.TAG_LIST, allEntries = true)
    public Response deleteTag(DeleteTagReqVO deleteTagReqVO) {
        ArticleTagRelDO articleTagRelDO = articleTagRelMapper.selectOneByTagId(deleteTagReqVO.getId());
        if (Objects.nonNull(articleTagRelDO)){
            log.warn("标签 ID：{}, 此标签已关联文章", deleteTagReqVO.getId());
            throw new BizException(ResponseCodeEnum.TAG_CAN_NOT_DELETE);
        }
        tagMapper.deleteById(deleteTagReqVO.getId());
        return Response.success();
    }

    @Override
    public Response findTagSeclectList(String name) {
        //构造查询参数
        QueryWrapper<TagDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        List<TagDo> tagDos = tagMapper.selectList(queryWrapper);
        List<SelectRspVO> selectRspVOS = null;
        if (!CollectionUtils.isEmpty(tagDos)){
            selectRspVOS = tagDos.stream().map(tagDo -> SelectRspVO.builder()
                    .value(tagDo.getId())
                    .label(tagDo.getName())
                    .build()).collect(Collectors.toList());
        }
        return Response.success(selectRspVOS);
    }

    @Override
    public Response findTagSelectList() {
        // 查询所有标签, Wrappers.emptyWrapper() 表示查询条件为空
        List<TagDo> tagDos = tagMapper.selectList(Wrappers.emptyWrapper());
        List<SelectRspVO> selectRspVOS = null;
        if (!CollectionUtils.isEmpty(tagDos)){
            selectRspVOS = tagDos.stream().map(tagDo -> SelectRspVO.builder()
                    .value(tagDo.getId())
                    .label(tagDo.getName())
                    .build()).collect(Collectors.toList());
        }
        return Response.success(selectRspVOS);
    }
}

package com.rookie.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.common.domain.dos.ArticleDO;
import com.rookie.common.domain.dos.ArticleTagRelDO;
import com.rookie.common.domain.dos.TagDo;
import com.rookie.common.domain.mapper.ArticleMapper;
import com.rookie.common.domain.mapper.ArticleTagRelMapper;
import com.rookie.common.domain.mapper.TagMapper;
import com.rookie.common.enums.ResponseCodeEnum;
import com.rookie.common.exception.BizException;
import com.rookie.common.utils.PageResponse;
import com.rookie.common.utils.Response;
import com.rookie.web.convert.ArticleConvert;
import com.rookie.web.model.vo.tag.FindTagArticlePageListReqVO;
import com.rookie.web.model.vo.tag.FindTagArticlePageListRspVO;
import com.rookie.web.model.vo.tag.FindTagListRspVO;
import com.rookie.common.constant.CacheConstants;
import com.rookie.web.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author peishunwu
 * @ClassName TagServiceImpl
 * @description: TODO
 * @date 2024年09月04日
 * @version: 1.0
 */
@Service
@Slf4j
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    @Autowired
    private ArticleMapper articleMapper;
    @Override
    @Cacheable(cacheNames = CacheConstants.TAG_LIST)
    public Response findTagList() {
        List<TagDo> tagList = tagMapper.selectList(null);
        List<FindTagListRspVO> tagListRspVO = null;
        if (!CollectionUtils.isEmpty(tagList)) {
            tagListRspVO = tagList.stream().map(tagDo -> {
                FindTagListRspVO rspVO = new FindTagListRspVO();
                rspVO.setId(tagDo.getId());
                rspVO.setName(tagDo.getName());
                return rspVO;
            }).collect(java.util.stream.Collectors.toList());
        }
        return Response.success(tagListRspVO);
    }

    @Override
    public Response findTagPageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO) {
        Long current = findTagArticlePageListReqVO.getCurrent();
        Long size = findTagArticlePageListReqVO.getSize();
        // 标签 ID
        Long tagId = findTagArticlePageListReqVO.getId();
        //判断该标签是否存在
        TagDo tagDo = tagMapper.selectById(tagId);
        if (Objects.isNull(tagDo)){
            log.warn("==> 该标签不存在, tagId: {}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_NOT_EXISTED);
        }
        // 先查询该标签下所有关联的文章 ID
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByTagId(tagId);
        if (CollectionUtils.isEmpty(articleTagRelDOS)){
            log.info("==> 该标签下还未发布文章, tagId: {}", tagId);
            return PageResponse.success(null, null);
        }
        // 提取所有的文章id
        List<Long> articleIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getArticleId).collect(Collectors.toList());
        // 根据文章 ID 集合查询文章分页数据
        Page<ArticleDO> page = articleMapper.selectPageListByArticleIds(current, size, articleIds);
        List<ArticleDO> articleDOS = page.getRecords();
        // DO 转 VO
        List<FindTagArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)){
            vos = articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2TagArticleVO(articleDO))
                    .collect(Collectors.toList());
        }
        return PageResponse.success(page, vos);
    }
}

package com.rookie.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.rookie.admin.convert.ArticleDetailConvert;
import com.rookie.admin.model.vo.article.*;
import com.rookie.admin.service.AdminArticleService;
import com.rookie.common.domain.dos.*;
import com.rookie.common.domain.mapper.*;
import com.rookie.common.constant.CacheConstants;
import com.rookie.common.enums.ResponseCodeEnum;
import com.rookie.common.exception.BizException;
import com.rookie.common.utils.PageResponse;
import com.rookie.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author peishunwu
 * @ClassName AdminArticleServiceImpl
 * @description: TODO
 * @date 2024年05月28日
 * @version: 1.0
 */
@Service
@Slf4j
public class AdminArticleServiceImpl implements AdminArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = CacheConstants.TAG_LIST, allEntries = true)
    public Response publishArticle(PublishArticleReqVO publishArticleReqVO) {
        ArticleDO articleDO = ArticleDO.builder()
                .title(publishArticleReqVO.getTitle())
                .summary(publishArticleReqVO.getSummary())
                .cover(publishArticleReqVO.getCover())
                // 不传状态默认已发布(1)，传 0 表示存草稿
                .status(Objects.isNull(publishArticleReqVO.getStatus()) ? 1 : publishArticleReqVO.getStatus())
                // 不传默认不置顶
                .isTop(Boolean.TRUE.equals(publishArticleReqVO.getIsTop()))
                .readNum(0L)
                .build();
        articleMapper.insert(articleDO);
        //获取插入文章的id
        Long articleId = articleDO.getId();
        //插入文章内容
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(publishArticleReqVO.getContent())
                .build();
        articleContentMapper.insert(articleContentDO);
        //插入文章分类关系
        Long categoryId = publishArticleReqVO.getCategoryId();
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }
        ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);
        //插入文章标签关系
        List<String> tags = publishArticleReqVO.getTagIds();
        insertTags(articleId,tags);
        return Response.success();
    }

    /**
     * 删除文章
     * @param deleteArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO) {
        Long articleId = deleteArticleReqVO.getId();
        //1、删除文章
        articleMapper.deleteById(articleId);
        //2、删除文章内容
        articleContentMapper.deleteByArticleIdInt(articleId);
        //3、删除文章分类关联
        articleCategoryRelMapper.deleteByArticleId(articleId);
        //4、删除文章标签关联
        articleTagRelMapper.deleteByArticleId(articleId);
        return Response.success();
    }

    @Override
    public Response findArticlePageList(FindArticlePageListReqVO findArticlePageListReqVO) {
        Long current = findArticlePageListReqVO.getCurrent();
        Long size = findArticlePageListReqVO.getSize();
        String title = findArticlePageListReqVO.getTitle();
        LocalDate startDate = findArticlePageListReqVO.getStartDate();
        LocalDate endDate = findArticlePageListReqVO.getEndDate();
        Page<ArticleDO> articleDOPage = articleMapper.selectPageList(current,size,title,startDate,endDate);
        List<ArticleDO> articleDOS = articleDOPage.getRecords();
        //DO转VO
        List<FindArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)){
            vos = articleDOS.stream()
                    .map(articleDO -> FindArticlePageListRspVO.builder()
                            .id(articleDO.getId())
                            .title(articleDO.getTitle())
                            .cover(articleDO.getCover())
                            .createTime(articleDO.getCreateTime())
                            .status(articleDO.getStatus())
                            .isTop(articleDO.getIsTop())
                            .build())
                    .collect(Collectors.toList());
        }
        return PageResponse.success(articleDOPage,vos);
    }

    @Override
    public Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {
        Long articleId = findArticleDetailReqVO.getId();

        ArticleDO articleDO = articleMapper.selectById(articleId);
        if (Objects.isNull(articleDO)){
            log.warn("==> 文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }
        ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);
        // 所属分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(articleId);
       // 对应标签
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleId(articleId);
        // 获取对应标签 ID 集合
        List<Long> tagIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getTagId).collect(Collectors.toList());

        FindArticleDetailRspVO vo = ArticleDetailConvert.INSTANCE.convertDO2VO(articleDO);
        vo.setContent(articleContentDO.getContent());
        vo.setCategoryId(articleCategoryRelDO.getCategoryId());
        vo.setTagIds(tagIds);
        return Response.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = CacheConstants.TAG_LIST, allEntries = true)
    public Response updateArticle(UpdateArticleReqVO updateArticleReqVO) {
        Long articleId = updateArticleReqVO.getId();
        ArticleDO articleDO = articleMapper.selectById(articleId);
        if (Objects.isNull(articleDO)){
            log.warn("==> 文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }
        articleDO=ArticleDO.builder()
                .id(articleId)
                .title(updateArticleReqVO.getTitle())
                .summary(updateArticleReqVO.getSummary())
                .cover(updateArticleReqVO.getCover())
                .status(updateArticleReqVO.getStatus())
                .isTop(updateArticleReqVO.getIsTop())
                .build();
        articleMapper.updateById(articleDO);
        // 2. VO 转 ArticleContentDO，并更新
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(updateArticleReqVO.getContent())
                .build();
        articleContentMapper.updateByArticleId(articleContentDO);
        // 3. 更新文章分类关联
        Long categoryId = updateArticleReqVO.getCategoryId();
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }
        // 先删除该文章关联的分类记录，再插入新的关联关系
        articleCategoryRelMapper.deleteByArticleId(articleId);
        ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);
        // 4. 更新文章标签关联
        // 先删除该文章关联的标签记录，再插入新的关联关系
        articleTagRelMapper.deleteByArticleId(articleId);
        List<String> tags = updateArticleReqVO.getTags();
        insertTags(articleId, tags);
        return Response.success();
    }

    /**
     * 保存标签
     * @param publishTags
     */
    private void insertTags(Long articleId,List<String> publishTags) {
        //筛选提交的标签（后台表中不存在的）
        List<String> notExistTags = null;
        //筛选提交的标签（后台表中存在的）
        List<String> existTags = null;
        List<TagDo> tagDos = tagMapper.selectList(null);
        if (CollectionUtils.isEmpty(tagDos)){
            notExistTags = publishTags;
        }else {
            List<String> tagsIds = tagDos.stream().map(tagDo -> String.valueOf(tagDo.getId())).collect(Collectors.toList());
            // 通过标签 ID 来筛选，包含对应 ID 则表示提交的标签是表中存在的
            existTags = publishTags.stream().filter(publishTag->tagsIds.contains(publishTag)).collect(Collectors.toList());
            notExistTags = publishTags.stream().filter(publishTag->!tagsIds.contains(publishTag)).collect(Collectors.toList());
            // 补充逻辑：
            // 还有一种可能：按字符串名称提交上来的标签，也有可能是表中已存在的，比如表中已经有了 Java 标签，用户提交了个 java 小写的标签，需要内部装换为 Java 标签
            Map<String, Long> tagNameIdMap = tagDos.stream().collect(Collectors.toMap(tagDO -> tagDO.getName().toLowerCase(), TagDo::getId));
            // 使用迭代器进行安全的删除操作
            Iterator<String> iterator = notExistTags.iterator();
            while (iterator.hasNext()){
                String notExistTag = iterator.next();
                // 转小写, 若 Map 中相同的 key，则表示该新标签是重复标签
                if (tagNameIdMap.containsKey(notExistTag.toLowerCase())) {
                    // 从不存在的标签集合中清除
                    iterator.remove();
                    // 并将对应的 ID 添加到已存在的标签集合
                    existTags.add(String.valueOf(tagNameIdMap.get(notExistTag.toLowerCase())));
                }
            }
        }
        // 将提交的上来的，已存在于表中的标签，文章-标签关联关系入库
        if (CollectionUtils.isNotEmpty(existTags)){
            List<ArticleTagRelDO> articleTagRelDOS = Lists.newArrayList();
            existTags.forEach(tagId -> {
                ArticleTagRelDO articleTagRelDO = ArticleTagRelDO.builder()
                        .articleId(articleId)
                        .tagId(Long.valueOf(tagId))
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            //批量插入
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }
        // 将提交的上来的，不存在于表中的标签，入库，并与文章建立关联关系
        if (CollectionUtils.isNotEmpty(notExistTags)){
            //需要先把标签入库，拿到对应的标签id后，再把文章-标签关联关系入库
            List<ArticleTagRelDO> articleTagRelDOS = Lists.newArrayList();
            notExistTags.forEach(tagName -> {
                TagDo tagDo = TagDo.builder()
                        .name(tagName)
                        .build();
                tagMapper.insert(tagDo);
                //拿到保存的标签id
                Long tagId = tagDo.getId();
                //文章-标签关联关系表
                ArticleTagRelDO articleTagRelDO = ArticleTagRelDO.builder()
                        .articleId(articleId)
                        .tagId(tagId)
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            //批量插入
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }
    }
}

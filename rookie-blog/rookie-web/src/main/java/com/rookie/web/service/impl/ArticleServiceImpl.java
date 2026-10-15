package com.rookie.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.common.domain.dos.*;
import com.rookie.common.domain.mapper.*;
import com.rookie.common.enums.ResponseCodeEnum;
import com.rookie.common.exception.BizException;
import com.rookie.common.utils.PageResponse;
import com.rookie.common.utils.Response;
import com.rookie.web.convert.ArticleConvert;
import com.rookie.web.markdown.MarkdownHelper;
import com.rookie.web.model.vo.article.*;
import com.rookie.web.model.vo.category.FindCategoryListRspVO;
import com.rookie.web.model.vo.tag.FindTagListRspVO;
import com.rookie.web.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author peishunwu
 * @ClassName ArticleServiceImpl
 * @description: TODO
 * @date 2024年08月31日
 * @version: 1.0
 */
@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Override
    public Response findIndexArticlePageList(FindIndexArticlePageListReqVO reqVO) {
        Long currentPage = reqVO.getCurrent();
        Long pageSize = reqVO.getSize();
        // 分页查询「已发布」文章列表（置顶优先）
        Page<ArticleDO> articleDOPage = articleMapper.selectPublishedPageList(currentPage, pageSize);
        List<ArticleDO> articleDOList = articleDOPage.getRecords();
        List<FindIndexArticlePageListRspVO> vos = null;

        if (!CollectionUtils.isEmpty(articleDOList)) {
            // 文章 DO 转 VO
            vos = articleDOList.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2VO(articleDO))
                    .collect(Collectors.toList());
            //拿到所有文章的id集合
            List<Long> articleIds = articleDOList.stream().map(ArticleDO::getId).collect(Collectors.toList());
            // 查询所有分类
            List<CategoryDO> categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());
            // 转 Map, 方便后续根据分类 ID 拿到对应的分类名称
            // key: 分类 ID, value: 分类名称
            Map<Long,String> categoryMap = categoryDOS.stream().collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName));
            // 根据文章 ID 批量查询所有关联记录
            List<ArticleCategoryRelDO> articleCategoryRelDOS = articleCategoryRelMapper.selectByArticleIds(articleIds);

            vos.forEach(vo -> {
                Long articleId = vo.getId();
                // 根据文章 ID 过滤出所有关联记录
                List<ArticleCategoryRelDO> relDOS = articleCategoryRelDOS.stream()
                        .filter(rel -> rel.getArticleId().equals(articleId))
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(relDOS)) {
                    // 获取第一条关联记录
                    ArticleCategoryRelDO relDO = relDOS.get(0);
                    // 根据分类 ID 拿到分类名称
                    String categoryName = categoryMap.get(relDO.getCategoryId());
                    FindCategoryListRspVO findCategoryListRspVO = FindCategoryListRspVO.builder()
                            .id(relDO.getCategoryId())
                            .name(categoryName)
                            .build();
                    vo.setCategory(findCategoryListRspVO);
                }
            });
            //设置文章标签
            //查询所有标签
            List<TagDo> tagDos = tagMapper.selectList(Wrappers.emptyWrapper());
            //转Map,方便后续根据标签ID拿到对应的标签名称
            //key:标签ID,value:标签名称
            Map<Long,String> tagMap = tagDos.stream().collect(Collectors.toMap(TagDo::getId, TagDo::getName));
            //根据文章ID批量查询所有关联记录
            List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleIds(articleIds);
            vos.forEach(vo -> {
               //根据文章id过滤出该文章对应的标签集合
                List<ArticleTagRelDO> relDOS = articleTagRelDOS.stream()
                        .filter(rel -> rel.getArticleId().equals(vo.getId()))
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(relDOS)) {
                    //获取标签名称集合
                    List<FindTagListRspVO> tagList = relDOS.stream()
                            .map(rel -> {
                                String tagName = tagMap.get(rel.getTagId());
                                return FindTagListRspVO.builder()
                                        .id(rel.getTagId())
                                        .name(tagName)
                                        .build();
                            }).collect(Collectors.toList());
                    vo.setTags(tagList);
                }
            });
        }
        return PageResponse.success(articleDOPage, vos);
    }

    @Override
    public Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {
        Long articleId = findArticleDetailReqVO.getArticleId();

        ArticleDO articleDO = articleMapper.selectById(articleId);

        // 判断文章是否存在；草稿(status!=1)对前台不可见
        if (Objects.isNull(articleDO) || !Integer.valueOf(1).equals(articleDO.getStatus())) {
            log.warn("==> 该文章不存在或未发布, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 查询正文
        ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);

        if (articleContentDO == null) throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);

        // 阅读量 +1（数据库原子自增）
        articleMapper.increaseReadNum(articleId);
        // 当前展示的阅读量 = 库中旧值 + 本次访问；兼容历史数据 read_num 为 null 的情况
        long currentReadNum = (Objects.isNull(articleDO.getReadNum()) ? 0L : articleDO.getReadNum()) + 1;

        // DO 转 VO
        FindArticleDetailRspVO vo = FindArticleDetailRspVO.builder()
                .title(articleDO.getTitle())
                .createTime(articleDO.getCreateTime())
                .content(MarkdownHelper.convertMarkdown2Html(articleContentDO.getContent()))
                .readNum(currentReadNum)
                .build();

        // 查询所属分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(articleId);
        CategoryDO categoryDO = articleCategoryRelDO == null ? null : categoryMapper.selectById(articleCategoryRelDO.getCategoryId());
        if (categoryDO != null) {
            vo.setCategoryId(categoryDO.getId());
            vo.setCategoryName(categoryDO.getName());
        }

        // 查询标签
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleId(articleId);
        List<Long> tagIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getTagId).collect(Collectors.toList());
        List<TagDo> tagDOS = tagIds.isEmpty() ? Collections.emptyList() : tagMapper.selectByIds(tagIds);

        // 标签 DO 转 VO
        List<FindTagListRspVO> tagVOS = tagDOS.stream()
                .map(tagDO -> FindTagListRspVO.builder().id(tagDO.getId()).name(tagDO.getName()).build())
                .collect(Collectors.toList());
        vo.setTags(tagVOS);

        // 上一篇
        ArticleDO preArticleDO = articleMapper.selectPreArticle(articleId);
        if (Objects.nonNull(preArticleDO)) {
            FindPreNextArticleRspVO preArticleVO = FindPreNextArticleRspVO.builder()
                    .articleId(preArticleDO.getId())
                    .articleTitle(preArticleDO.getTitle())
                    .build();
            vo.setPreArticle(preArticleVO);
        }

        // 下一篇
        ArticleDO nextArticleDO = articleMapper.selectNextArticle(articleId);
        if (Objects.nonNull(nextArticleDO)) {
            FindPreNextArticleRspVO nextArticleVO = FindPreNextArticleRspVO.builder()
                    .articleId(nextArticleDO.getId())
                    .articleTitle(nextArticleDO.getTitle())
                    .build();
            vo.setNextArticle(nextArticleVO);
        }

        return Response.success(vo);
    }

    @Override
    public Response searchArticles(SearchArticlePageListReqVO reqVO) {
        String keyword = reqVO.getKeyword();
        // 正文命中的文章 ID
        List<Long> contentMatchIds = articleContentMapper.selectArticleIdsByContentKeyword(keyword);
        // 标题或正文命中的已发布文章分页
        Page<ArticleDO> page = articleMapper.searchPublishedPageList(reqVO.getCurrent(), reqVO.getSize(), keyword, contentMatchIds);
        List<ArticleDO> records = page.getRecords();

        List<SearchArticlePageListRspVO> vos = Collections.emptyList();
        if (!CollectionUtils.isEmpty(records)) {
            vos = records.stream()
                    .map(a -> SearchArticlePageListRspVO.builder()
                            .id(a.getId())
                            .title(a.getTitle())
                            .cover(a.getCover())
                            .summary(a.getSummary())
                            .createTime(a.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }
        return PageResponse.success(page, vos);
    }
}

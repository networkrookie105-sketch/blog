package com.rookie.web.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.rookie.common.domain.dos.ArticleDO;
import com.rookie.common.domain.mapper.ArticleMapper;
import com.rookie.common.utils.PageResponse;
import com.rookie.common.utils.Response;
import com.rookie.web.convert.ArticleConvert;
import com.rookie.web.model.vo.article.FindArchiveArticlePageListReqVO;
import com.rookie.web.model.vo.article.FindArchiveArticlePageListRspVO;
import com.rookie.web.model.vo.article.FindArchiveArticleRspVO;
import com.rookie.web.service.ArchiveService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author peishunwu
 * @ClassName ArchiveServiceImpl
 * @description: TODO
 * @date 2025年08月11日
 * @version: 1.0
 */
@Service
@Slf4j
public class ArchiveServiceImpl implements ArchiveService {

    @Autowired
    private ArticleMapper articleMapper;
    @Override
    public Response findArchivePageList(FindArchiveArticlePageListReqVO reqVO) {
        Long current = reqVO.getCurrent();
        Long size = reqVO.getSize();
        // 分页查询「已发布」文章
        IPage<ArticleDO> page = articleMapper.selectArchivePageList(current, size);
        List<ArticleDO> articleDOS = page.getRecords();

        List<FindArchiveArticlePageListRspVO> vos = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(articleDOS)) {
            // DO 转 VO
            List<FindArchiveArticleRspVO> archiveArticleRspVOS =  articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2ArchiveArticleVO(articleDO))
                    .collect(Collectors.toList());
            // 按创建的月份进行分组
            Map<YearMonth, List<FindArchiveArticleRspVO>> map = archiveArticleRspVOS.stream().collect(Collectors.groupingBy(FindArchiveArticleRspVO::getCreateMonth));
            // 使用 TreeMap 按月份倒序排列
            Map<YearMonth, List<FindArchiveArticleRspVO>> sortedMap = new TreeMap<>(Collections.reverseOrder());
            sortedMap.putAll(map);
            // 遍历排序后的 Map，将其转换为归档 VO
            sortedMap.forEach((k, v) -> vos.add(FindArchiveArticlePageListRspVO.builder().month(k).articles(v).build()));

        }
        return PageResponse.success(page, vos);
    }
}

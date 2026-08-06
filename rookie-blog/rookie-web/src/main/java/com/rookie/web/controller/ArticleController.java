package com.rookie.web.controller;

import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.Response;
import com.rookie.web.model.vo.article.FindArticleDetailReqVO;
import com.rookie.web.model.vo.article.FindIndexArticlePageListReqVO;
import com.rookie.web.model.vo.article.SearchArticlePageListReqVO;
import com.rookie.web.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author peishunwu
 * @ClassName ArticleController
 * @description: TODO
 * @date 2024年09月04日
 * @version: 1.0
 */
@RestController
@Api(tags = "文章")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @PostMapping("/article/list")
    @ApiOperation(value = "首页查询文章分页")
    @ApiOperationLog(description = "首页查询文章分页")
    public Response findIndexArticlePageList(@RequestBody FindIndexArticlePageListReqVO reqVO) {
        return articleService.findIndexArticlePageList(reqVO);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取文章详情")
    @ApiOperationLog(description = "获取文章详情")
    public Response findArticleDetail(@RequestBody FindArticleDetailReqVO findArticleDetailReqVO) {
        return articleService.findArticleDetail(findArticleDetailReqVO);
    }

    @PostMapping("/search")
    @ApiOperation(value = "全文搜索文章")
    @ApiOperationLog(description = "全文搜索文章")
    public Response searchArticles(@RequestBody @Validated SearchArticlePageListReqVO reqVO) {
        return articleService.searchArticles(reqVO);
    }
}

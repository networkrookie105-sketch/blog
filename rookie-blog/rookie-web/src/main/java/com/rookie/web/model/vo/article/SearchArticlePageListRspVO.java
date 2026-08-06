package com.rookie.web.model.vo.article;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author kiro
 * @ClassName SearchArticlePageListRspVO
 * @date 2026年08月05日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "文章搜索结果 VO")
public class SearchArticlePageListRspVO {
    private Long id;
    private String title;
    private String cover;
    private String summary;
    private LocalDateTime createTime;
}

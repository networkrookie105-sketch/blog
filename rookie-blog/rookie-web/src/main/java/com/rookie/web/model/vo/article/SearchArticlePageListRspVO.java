package com.rookie.web.model.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "文章搜索结果 VO")
public class SearchArticlePageListRspVO {
    private Long id;
    private String title;
    private String cover;
    private String summary;
    private LocalDateTime createTime;
}

package com.rookie.admin.convert;

import com.rookie.admin.model.vo.article.FindArticleDetailRspVO;
import com.rookie.common.domain.dos.ArticleDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author peishunwu
 * @ClassName ArticleDetailConvert
 * @description: TODO
 * @date 2024年06月11日
 * @version: 1.0
 */
@Mapper
public interface ArticleDetailConvert {
    /**
     * 初始化 convert 实例
     */
    ArticleDetailConvert INSTANCE = Mappers.getMapper(ArticleDetailConvert.class);

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    FindArticleDetailRspVO convertDO2VO(ArticleDO bean);
}

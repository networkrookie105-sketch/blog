package com.rookie.web.convert;

import com.rookie.common.domain.dos.BlogSettingsDO;
import com.rookie.web.model.vo.blogsettings.FindBlogSettingsDetailRspVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author peishunwu
 * @ClassName BlogSettingsConvert
 * @description: TODO
 * @date 2024年09月05日
 * @version: 1.0
 */
@Mapper
public interface BlogSettingsConvert {
    /**
     * 初始化 convert 实例
     */
    BlogSettingsConvert INSTANCE = Mappers.getMapper(BlogSettingsConvert.class);

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    FindBlogSettingsDetailRspVO convertDO2VO(BlogSettingsDO bean);
}

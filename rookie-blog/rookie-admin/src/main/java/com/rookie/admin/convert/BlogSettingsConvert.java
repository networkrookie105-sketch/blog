package com.rookie.admin.convert;

import com.rookie.admin.model.vo.blogsettings.FindBlogSettingsRspVo;
import com.rookie.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.rookie.common.domain.dos.BlogSettingsDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author peishunwu
 * @ClassName BlogSettingsConvert
 * @description: TODO
 * @date 2024年05月14日
 * @version: 1.0
 */
@Mapper
public interface BlogSettingsConvert {
    /**
     * 初始化 convert 实例
     */
    BlogSettingsConvert INSTANCE = Mappers.getMapper(BlogSettingsConvert.class);

    /**
     * Do 转 Vo
     * @param blogSettingsDO
     * @return
     */
    BlogSettingsDO convertVO2DO(UpdateBlogSettingsReqVO blogSettingsDO);

    /**
     * Vo 转 Do
     * @param blogSettingsDO
     * @return
     */
    FindBlogSettingsRspVo convertDO2VO(BlogSettingsDO blogSettingsDO);
}

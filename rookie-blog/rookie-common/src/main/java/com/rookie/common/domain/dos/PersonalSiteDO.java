package com.rookie.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_personal_site")
public class PersonalSiteDO {
    @TableId(type = IdType.INPUT)
    private String userName;
    private String document;
    private Integer revision;
}

package com.rookie.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author peishunwu
 * @ClassName UserRoleDo
 * @description: TODO
 * @date 2024年03月08日
 * @version: 1.0
 */
@Data
@Builder
@TableName("t_user_role")
public class UserRoleDo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userName;
    private String role;
    private Date createTime;
    private Date updateTime;
    private Boolean isDeleted;
}

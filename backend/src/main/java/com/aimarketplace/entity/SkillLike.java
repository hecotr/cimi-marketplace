package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("skill_like")
public class SkillLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long skillId;
    private LocalDateTime createdAt;
}

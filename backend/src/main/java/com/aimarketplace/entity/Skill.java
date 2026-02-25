package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("skill")
public class Skill {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String category;
    private String type;
    private String content;
    private String storagePath;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer likeCount;
    private Long createdBy;
    private LocalDateTime createdAt;
}

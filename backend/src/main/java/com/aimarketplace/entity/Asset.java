package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Asset Entity - Unified table for LLM models and Skills
 */
@Data
@TableName("asset")
public class Asset {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String type; // 'llm_model' or 'skill'

    private String description;

    private Long categoryId;

    private String content;

    private String storagePath;

    private String status; // 'draft', 'pending', 'published', 'rejected'

    private String version;

    private Integer viewCount;

    private Integer downloadCount;

    private Integer likeCount;

    private Long createdBy;

    private Long approvedBy;

    private LocalDateTime approvedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

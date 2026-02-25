package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("llm_model")
public class LlmModel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String provider;
    private String modelName;
    private String description;
    private String apiEndpoint;
    private Integer maxTokens;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("llm_test_record")
public class LlmTestRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long modelId;
    private String prompt;
    private String response;
    private String parameters;
    private Integer responseTime;
    private String tokenUsage;
    private LocalDateTime createdAt;
}

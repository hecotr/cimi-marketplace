package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "llm_model_config", autoResultMap = true)
public class LlmModelConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String provider;
    private String modelName;
    private String description;
    private Long categoryId;
    private String apiProtocol; // openai, anthropic
    private String apiEndpoint;
    private Integer maxTokens;

    @com.baomidou.mybatisplus.annotation.TableField(typeHandler = JacksonTypeHandler.class, jdbcType = JdbcType.OTHER)
    private Map<String, Object> defaultParams;

    @com.baomidou.mybatisplus.annotation.TableField(typeHandler = JacksonTypeHandler.class, jdbcType = JdbcType.OTHER)
    private Map<String, Object> billingRule;

    private String version;
    private String status; // active, inactive
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

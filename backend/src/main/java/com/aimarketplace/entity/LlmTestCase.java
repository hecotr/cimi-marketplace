package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("llm_test_case")
public class LlmTestCase {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long modelId;
    private String name;
    private String prompt;
    private String expectedOutput;
    private LocalDateTime createdAt;
}

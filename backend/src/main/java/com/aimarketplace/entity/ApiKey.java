package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("api_key")
public class ApiKey {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long modelId;
    private String keyValue;
    private String status;
    private LocalDateTime applyTime;
    private LocalDateTime approveTime;
    private LocalDateTime expiryDate;
}

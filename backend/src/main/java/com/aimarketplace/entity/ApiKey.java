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
    private Long modelConfigId;
    private String keyValue;
    private String apiProtocol; // openai, anthropic
    private String status; // pending, approved, rejected, expired
    private String expiryType; // 3m, 6m, 1y, permanent
    private LocalDateTime applyTime;
    private LocalDateTime approveTime;
    private LocalDateTime expiryDate;
    private Long approverId;
    private String rejectionReason;
}

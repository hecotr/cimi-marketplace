package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("approval_record")
public class ApprovalRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String approvalType; // asset, api_key
    private Long targetId;
    private String targetType;
    private Long reviewerId;
    private String status; // approved, rejected
    private String comment;
    private String externalWorkflowId;
    private LocalDateTime createdAt;
}

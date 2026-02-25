package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Approval Record Entity
 */
@Data
@TableName("approval_record")
public class ApprovalRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assetId;

    private String assetVersion;

    private Long approverId;

    private String action; // 'approve', 'reject', 'request_changes'

    private String comment;

    private String status; // 'pending', 'approved', 'rejected'

    private LocalDateTime createdAt;
}

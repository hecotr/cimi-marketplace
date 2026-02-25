package com.aimarketplace.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Approval Callback Request DTO
 */
@Data
public class ApprovalCallbackRequest {

    @NotNull(message = "Action is required")
    private String action; // 'approve', 'reject', 'request_changes'

    @NotBlank(message = "Comment is required")
    private String comment;
}

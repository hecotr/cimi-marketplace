package com.aimarketplace.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class InteractionRequest {
    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    @NotBlank(message = "资产类型不能为空")
    @Pattern(regexp = "^(llm|skill)$", message = "资产类型只能是 llm 或 skill")
    private String assetType;
}

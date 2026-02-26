package com.aimarketplace.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ApiKeyApplyRequest {
    @NotNull(message = "模型配置ID不能为空")
    private Long modelConfigId;

    @NotBlank(message = "API协议不能为空")
    @Pattern(regexp = "^(openai|anthropic)$", message = "API协议只能是 openai 或 anthropic")
    private String apiProtocol;

    @NotBlank(message = "有效期类型不能为空")
    @Pattern(regexp = "^(3m|6m|1y|permanent)$", message = "有效期只能是 3m、6m、1y 或 permanent")
    private String expiryType;
}

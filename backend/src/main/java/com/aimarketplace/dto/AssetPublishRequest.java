package com.aimarketplace.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AssetPublishRequest {
    @NotBlank(message = "资产类型不能为空")
    private String assetType; // skill (llm 由管理员配置)

    @NotBlank(message = "名称不能为空")
    @Size(max = 200, message = "名称不能超过200字符")
    private String name;

    @NotBlank(message = "描述不能为空")
    private String description;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private String tags;

    @NotBlank(message = "内容不能为空")
    private String content; // 在线编辑内容

    private String fileType; // md, zip
}

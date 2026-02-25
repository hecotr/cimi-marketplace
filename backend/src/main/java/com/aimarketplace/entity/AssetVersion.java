package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Asset Version Entity
 */
@Data
@TableName("asset_version")
public class AssetVersion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assetId;

    private String version;

    private String content;

    private String storagePath;

    private String changeNotes;

    private Long createdBy;

    private LocalDateTime createdAt;

    private Boolean isCurrent;
}

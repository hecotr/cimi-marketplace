package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("asset_version")
public class AssetVersion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long assetId;
    private Integer versionNo;
    private String content;
    private String storagePath;
    private String fileType; // md, zip
    private Integer viewCount;
    private Integer downloadCount;
    private Integer likeCount;
    private LocalDateTime createdAt;
}

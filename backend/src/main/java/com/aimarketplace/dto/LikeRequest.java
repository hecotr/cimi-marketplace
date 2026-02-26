package com.aimarketplace.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class LikeRequest extends InteractionRequest {
    private Long versionId;
}

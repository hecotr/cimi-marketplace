package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Approval Record Mapper
 */
@Mapper
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecord> {

    @Select("SELECT * FROM approval_record WHERE asset_id = #{assetId} ORDER BY created_at DESC")
    List<ApprovalRecord> findByAssetId(Long assetId);

    @Select("SELECT * FROM approval_record WHERE status = 'pending' ORDER BY created_at")
    List<ApprovalRecord> findPending();
}

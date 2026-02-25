package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.LlmModelConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * LLM Model Config Mapper
 */
@Mapper
public interface LlmModelConfigMapper extends BaseMapper<LlmModelConfig> {

    @Select("SELECT * FROM llm_model_config WHERE llm_model_id = #{modelId} AND is_encrypted = false")
    List<LlmModelConfig> findPublicByModelId(Long modelId);

    @Select("SELECT * FROM llm_model_config WHERE llm_model_id = #{modelId}")
    List<LlmModelConfig> findByModelId(Long modelId);

    @Select("SELECT * FROM llm_model_config WHERE llm_model_id = #{modelId} AND config_key = #{key}")
    LlmModelConfig findByModelIdAndKey(Long modelId, String key);
}

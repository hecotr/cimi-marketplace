package com.aimarketplace.service;

import com.aimarketplace.dto.LlmModelDTO;
import com.aimarketplace.entity.Category;
import com.aimarketplace.entity.LlmModelConfig;
import com.aimarketplace.mapper.CategoryMapper;
import com.aimarketplace.mapper.LlmModelConfigMapper;
import com.aimarketplace.service.impl.LlmModelConfigServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LlmModelConfigServiceTest {

    @Mock
    private LlmModelConfigMapper modelConfigMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private LlmModelConfigServiceImpl llmModelConfigService;

    private LlmModelConfig testConfig;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("GPT系列");
        testCategory.setAssetType("llm");

        testConfig = new LlmModelConfig();
        testConfig.setId(1L);
        testConfig.setName("GPT-4");
        testConfig.setProvider("OpenAI");
        testConfig.setModelName("gpt-4");
        testConfig.setDescription("GPT-4 模型");
        testConfig.setCategoryId(1L);
        testConfig.setApiProtocol("openai");
        testConfig.setApiEndpoint("https://api.openai.com/v1");
        testConfig.setMaxTokens(8192);
        testConfig.setStatus("active");
        testConfig.setVersion("1.0");
        testConfig.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("获取所有活跃模型")
    void getActiveModels() {
        when(modelConfigMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testConfig));
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);

        List<LlmModelDTO> result = llmModelConfigService.getActiveModels();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("GPT-4", result.get(0).getName());
        assertEquals("GPT系列", result.get(0).getCategoryName());
    }

    @Test
    @DisplayName("获取所有模型（管理员）")
    void getAllModels() {
        LlmModelConfig inactiveConfig = new LlmModelConfig();
        inactiveConfig.setId(2L);
        inactiveConfig.setName("GPT-3.5");
        inactiveConfig.setStatus("inactive");
        inactiveConfig.setCategoryId(1L);
        inactiveConfig.setCreatedAt(LocalDateTime.now());

        when(modelConfigMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testConfig, inactiveConfig));
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);

        List<LlmModelDTO> result = llmModelConfigService.getAllModels();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取模型详情 - 成功")
    void getModelByIdSuccess() {
        when(modelConfigMapper.selectById(1L)).thenReturn(testConfig);
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);

        LlmModelDTO result = llmModelConfigService.getModelById(1L);

        assertNotNull(result);
        assertEquals("GPT-4", result.getName());
        assertEquals("OpenAI", result.getProvider());
    }

    @Test
    @DisplayName("获取模型详情 - 不存在")
    void getModelByIdNotFound() {
        when(modelConfigMapper.selectById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> llmModelConfigService.getModelById(999L));
    }

    @Test
    @DisplayName("创建模型配置")
    void createModel() {
        LlmModelConfig newConfig = new LlmModelConfig();
        newConfig.setName("Claude 3");
        newConfig.setProvider("Anthropic");
        newConfig.setModelName("claude-3-opus");

        when(modelConfigMapper.insert(any(LlmModelConfig.class))).thenAnswer(invocation -> {
            LlmModelConfig config = invocation.getArgument(0);
            config.setId(3L);
            return 1;
        });

        Long id = llmModelConfigService.createModel(newConfig);

        assertNotNull(id);
        verify(modelConfigMapper).insert(any(LlmModelConfig.class));
    }

    @Test
    @DisplayName("更新模型配置 - 成功")
    void updateModelSuccess() {
        LlmModelConfig updateConfig = new LlmModelConfig();
        updateConfig.setName("GPT-4 Turbo");
        updateConfig.setMaxTokens(128000);

        when(modelConfigMapper.selectById(1L)).thenReturn(testConfig);
        when(modelConfigMapper.updateById(any())).thenReturn(1);

        llmModelConfigService.updateModel(1L, updateConfig);

        verify(modelConfigMapper).updateById(any(LlmModelConfig.class));
    }

    @Test
    @DisplayName("更新模型配置 - 不存在")
    void updateModelNotFound() {
        when(modelConfigMapper.selectById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> llmModelConfigService.updateModel(999L, new LlmModelConfig()));
    }

    @Test
    @DisplayName("删除模型配置 - 软删除")
    void deleteModel() {
        when(modelConfigMapper.selectById(1L)).thenReturn(testConfig);
        when(modelConfigMapper.updateById(any())).thenReturn(1);

        llmModelConfigService.deleteModel(1L);

        verify(modelConfigMapper).updateById(argThat(config -> "inactive".equals(config.getStatus())));
    }

    @Test
    @DisplayName("删除模型配置 - 不存在")
    void deleteModelNotFound() {
        when(modelConfigMapper.selectById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> llmModelConfigService.deleteModel(999L));
    }
}

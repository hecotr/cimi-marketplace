-- AI Marketplace Initial Data
-- 初始化数据脚本

-- 插入管理员用户 (密码: admin, BCrypt加密)
INSERT INTO "user" (username, password, role, email, department) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin', 'admin@example.com', 'IT部门')
ON CONFLICT (username) DO NOTHING;

-- 插入测试用户 (密码: 123456)
INSERT INTO "user" (username, password, role, email, department) VALUES
('testuser', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'user', 'test@example.com', '研发部')
ON CONFLICT (username) DO NOTHING;

-- 插入 LLM 分类
INSERT INTO category (name, asset_type, sort_order) VALUES
('通用对话', 'llm', 1),
('代码生成', 'llm', 2),
('数据分析', 'llm', 3),
('图像理解', 'llm', 4)
ON CONFLICT DO NOTHING;

-- 插入 Skills 分类
INSERT INTO category (name, asset_type, sort_order) VALUES
('文案写作', 'skill', 1),
('数据分析', 'skill', 2),
('代码辅助', 'skill', 3),
('翻译', 'skill', 4),
('总结摘要', 'skill', 5)
ON CONFLICT DO NOTHING;

-- 插入预置 LLM 模型配置
-- 注意：需要先获取分类ID
DO $$
DECLARE
    chat_cat_id BIGINT;
    code_cat_id BIGINT;
BEGIN
    SELECT id INTO chat_cat_id FROM category WHERE name = '通用对话' AND asset_type = 'llm' LIMIT 1;
    SELECT id INTO code_cat_id FROM category WHERE name = '代码生成' AND asset_type = 'llm' LIMIT 1;

    -- 插入 LLM 模型配置
    INSERT INTO llm_model_config (name, provider, model_name, description, category_id, api_protocol, api_endpoint, max_tokens, status, created_by)
    VALUES
    ('GLM-4', '智谱AI', 'glm-4', '智谱AI GLM-4 大语言模型，支持通用对话、代码生成等能力', chat_cat_id, 'openai', 'https://open.bigmodel.cn/api/paas/v4/', 128000, 'active', 1),
    ('GLM-4-Flash', '智谱AI', 'glm-4-flash', '智谱AI GLM-4-Flash 快速响应模型，适合高频调用场景', chat_cat_id, 'openai', 'https://open.bigmodel.cn/api/paas/v4/', 128000, 'active', 1),
    ('GLM-4-Plus', '智谱AI', 'glm-4-plus', '智谱AI GLM-4-Plus 增强版模型，更强的推理能力', chat_cat_id, 'openai', 'https://open.bigmodel.cn/api/paas/v4/', 128000, 'active', 1),
    ('GPT-4', 'OpenAI', 'gpt-4', 'OpenAI GPT-4 模型', chat_cat_id, 'openai', 'https://api.openai.com/v1/', 8192, 'active', 1),
    ('GPT-4-Turbo', 'OpenAI', 'gpt-4-turbo', 'OpenAI GPT-4 Turbo 模型', chat_cat_id, 'openai', 'https://api.openai.com/v1/', 128000, 'active', 1),
    ('Claude-3-Opus', 'Anthropic', 'claude-3-opus', 'Anthropic Claude 3 Opus 模型', chat_cat_id, 'anthropic', 'https://api.anthropic.com/', 200000, 'active', 1)
    ON CONFLICT DO NOTHING;
END $$;

-- 插入默认参数配置
UPDATE llm_model_config SET default_params = '{"temperature": 0.7, "top_p": 0.9}' WHERE default_params IS NULL;

-- AI Marketplace Database Schema
-- PostgreSQL 数据库建表语句

-- 用户表
CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    department VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT,
    asset_type VARCHAR(20) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 资产表
CREATE TABLE IF NOT EXISTS asset (
    id BIGSERIAL PRIMARY KEY,
    asset_type VARCHAR(20) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id BIGINT REFERENCES category(id),
    tags VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    current_version_id BIGINT,
    created_by BIGINT REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 资产版本表
CREATE TABLE IF NOT EXISTS asset_version (
    id BIGSERIAL PRIMARY KEY,
    asset_id BIGINT NOT NULL REFERENCES asset(id) ON DELETE CASCADE,
    version_no INT NOT NULL,
    content TEXT,
    storage_path VARCHAR(500),
    file_type VARCHAR(20),
    view_count INT DEFAULT 0,
    download_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(asset_id, version_no)
);

-- LLM 模型配置表
CREATE TABLE IF NOT EXISTS llm_model_config (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id BIGINT REFERENCES category(id),
    api_protocol VARCHAR(20) NOT NULL DEFAULT 'openai',
    api_endpoint VARCHAR(500) NOT NULL,
    max_tokens INT DEFAULT 4096,
    default_params JSONB,
    billing_rule JSONB,
    version VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_by BIGINT REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LLM 测试记录表
CREATE TABLE IF NOT EXISTS llm_test_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id),
    model_config_id BIGINT REFERENCES llm_model_config(id),
    prompt TEXT,
    response TEXT,
    parameters JSONB,
    response_time INT,
    token_usage JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- API Key 表
CREATE TABLE IF NOT EXISTS api_key (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    model_config_id BIGINT REFERENCES llm_model_config(id),
    key_value VARCHAR(255) UNIQUE NOT NULL,
    api_protocol VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    expiry_type VARCHAR(20) NOT NULL DEFAULT '3m',
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approve_time TIMESTAMP,
    expiry_date TIMESTAMP,
    approver_id BIGINT REFERENCES "user"(id),
    rejection_reason VARCHAR(500)
);

-- 审核记录表
CREATE TABLE IF NOT EXISTS approval_record (
    id BIGSERIAL PRIMARY KEY,
    approval_type VARCHAR(20) NOT NULL,
    target_id BIGINT NOT NULL,
    target_type VARCHAR(20),
    reviewer_id BIGINT REFERENCES "user"(id),
    status VARCHAR(20) NOT NULL,
    comment TEXT,
    external_workflow_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 收藏表
CREATE TABLE IF NOT EXISTS favorite (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    asset_id BIGINT NOT NULL,
    asset_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, asset_id, asset_type)
);

-- 点赞记录表
CREATE TABLE IF NOT EXISTS like_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    asset_id BIGINT NOT NULL,
    asset_type VARCHAR(20) NOT NULL,
    asset_version_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, asset_id, asset_type, asset_version_id)
);

-- LLM 使用日志表
CREATE TABLE IF NOT EXISTS llm_usage_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id),
    api_key_id BIGINT REFERENCES api_key(id),
    model_config_id BIGINT REFERENCES llm_model_config(id),
    prompt_tokens INT DEFAULT 0,
    completion_tokens INT DEFAULT 0,
    total_tokens INT DEFAULT 0,
    response_time INT,
    error_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_asset_type ON asset(asset_type);
CREATE INDEX IF NOT EXISTS idx_asset_status ON asset(status);
CREATE INDEX IF NOT EXISTS idx_asset_created_by ON asset(created_by);
CREATE INDEX IF NOT EXISTS idx_asset_category ON asset(category_id);
CREATE INDEX IF NOT EXISTS idx_asset_version_asset_id ON asset_version(asset_id);
CREATE INDEX IF NOT EXISTS idx_llm_config_status ON llm_model_config(status);
CREATE INDEX IF NOT EXISTS idx_api_key_user_id ON api_key(user_id);
CREATE INDEX IF NOT EXISTS idx_api_key_status ON api_key(status);
CREATE INDEX IF NOT EXISTS idx_approval_target ON approval_record(approval_type, target_id);
CREATE INDEX IF NOT EXISTS idx_favorite_user ON favorite(user_id, asset_type);
CREATE INDEX IF NOT EXISTS idx_like_record_asset ON like_record(asset_id, asset_type);

-- 注释
COMMENT ON TABLE "user" IS '用户表';
COMMENT ON TABLE category IS '分类表';
COMMENT ON TABLE asset IS '资产表（LLM/Skill）';
COMMENT ON TABLE asset_version IS '资产版本表';
COMMENT ON TABLE llm_model_config IS 'LLM模型配置表';
COMMENT ON TABLE llm_test_record IS 'LLM测试记录表';
COMMENT ON TABLE api_key IS 'API Key表';
COMMENT ON TABLE approval_record IS '审核记录表';
COMMENT ON TABLE favorite IS '收藏表';
COMMENT ON TABLE like_record IS '点赞记录表';
COMMENT ON TABLE llm_usage_log IS 'LLM使用日志表';

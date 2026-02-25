-- Enhanced Schema for AI Marketplace
-- This file adds new tables for the enhanced features

-- User Roles table
CREATE TABLE IF NOT EXISTS user_role (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role)
);

-- Asset table (unifies LLM models and Skills)
CREATE TABLE IF NOT EXISTS asset (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL, -- 'llm_model' or 'skill'
    description TEXT,
    category_id BIGINT REFERENCES category(id) ON DELETE SET NULL,
    content TEXT,
    storage_path VARCHAR(500),
    status VARCHAR(20) DEFAULT 'draft', -- 'draft', 'pending', 'published', 'rejected'
    version VARCHAR(20) DEFAULT '1.0.0',
    view_count INTEGER DEFAULT 0,
    download_count INTEGER DEFAULT 0,
    like_count INTEGER DEFAULT 0,
    created_by BIGINT NOT NULL REFERENCES "user"(id),
    approved_by BIGINT REFERENCES "user"(id),
    approved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Asset Version table
CREATE TABLE IF NOT EXISTS asset_version (
    id BIGSERIAL PRIMARY KEY,
    asset_id BIGINT NOT NULL REFERENCES asset(id) ON DELETE CASCADE,
    version VARCHAR(20) NOT NULL,
    content TEXT,
    storage_path VARCHAR(500),
    change_notes TEXT,
    created_by BIGINT NOT NULL REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_current BOOLEAN DEFAULT FALSE,
    UNIQUE(asset_id, version)
);

-- Category table
CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    parent_id BIGINT REFERENCES category(id) ON DELETE SET NULL,
    sort_order INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LLM Model Config table (extends llm_model with more configuration)
CREATE TABLE IF NOT EXISTS llm_model_config (
    id BIGSERIAL PRIMARY KEY,
    llm_model_id BIGINT NOT NULL REFERENCES llm_model(id) ON DELETE CASCADE,
    config_key VARCHAR(100) NOT NULL,
    config_value TEXT,
    config_type VARCHAR(20) DEFAULT 'string', -- 'string', 'number', 'boolean', 'json'
    is_encrypted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(llm_model_id, config_key)
);

-- Approval Record table
CREATE TABLE IF NOT EXISTS approval_record (
    id BIGSERIAL PRIMARY KEY,
    asset_id BIGINT NOT NULL REFERENCES asset(id) ON DELETE CASCADE,
    asset_version VARCHAR(20),
    approver_id BIGINT NOT NULL REFERENCES "user"(id),
    action VARCHAR(20) NOT NULL, -- 'approve', 'reject', 'request_changes'
    comment TEXT,
    status VARCHAR(20) NOT NULL, -- 'pending', 'approved', 'rejected'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Favorite table (general, for all asset types)
CREATE TABLE IF NOT EXISTS favorite (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    asset_id BIGINT NOT NULL REFERENCES asset(id) ON DELETE CASCADE,
    asset_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, asset_id, asset_type)
);

-- Like Record table (general, for all asset types)
CREATE TABLE IF NOT EXISTS like_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    asset_id BIGINT NOT NULL REFERENCES asset(id) ON DELETE CASCADE,
    asset_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, asset_id, asset_type)
);

-- Indexes for enhanced tables
CREATE INDEX IF NOT EXISTS idx_user_role_user ON user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_asset_type ON asset(type);
CREATE INDEX IF NOT EXISTS idx_asset_category ON asset(category_id);
CREATE INDEX IF NOT EXISTS idx_asset_status ON asset(status);
CREATE INDEX IF NOT EXISTS idx_asset_created_by ON asset(created_by);
CREATE INDEX IF NOT EXISTS idx_asset_version_asset ON asset_version(asset_id);
CREATE INDEX IF NOT EXISTS idx_asset_version_current ON asset_version(is_current);
CREATE INDEX IF NOT EXISTS idx_category_parent ON category(parent_id);
CREATE INDEX IF NOT EXISTS idx_category_status ON category(status);
CREATE INDEX IF NOT EXISTS idx_llm_model_config_model ON llm_model_config(llm_model_id);
CREATE INDEX IF NOT EXISTS idx_approval_record_asset ON approval_record(asset_id);
CREATE INDEX IF NOT EXISTS idx_approval_record_approver ON approval_record(approver_id);
CREATE INDEX IF NOT EXISTS idx_approval_record_status ON approval_record(status);
CREATE INDEX IF NOT EXISTS idx_favorite_user ON favorite(user_id);
CREATE INDEX IF NOT EXISTS idx_favorite_asset ON favorite(asset_id);
CREATE INDEX IF NOT EXISTS idx_like_record_user ON like_record(user_id);
CREATE INDEX IF NOT EXISTS idx_like_record_asset ON like_record(asset_id);

-- Insert default admin role for user_id 1
INSERT INTO user_role (user_id, role)
SELECT 1, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM user_role WHERE user_id = 1 AND role = 'admin');

-- Insert default categories
INSERT INTO category (name, description, sort_order) VALUES
('Natural Language Processing', 'NLP models and tools', 1),
('Computer Vision', 'Image and video processing models', 2),
('Data Analysis', 'Data processing and analysis tools', 3),
('Automation', 'Workflow automation scripts', 4),
('Code Generation', 'Code generation and assistance tools', 5)
ON CONFLICT DO NOTHING;

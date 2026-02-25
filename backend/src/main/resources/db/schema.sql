-- Users table (using quoted "user" to avoid PostgreSQL reserved keyword)
CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255),
    department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LLM Models table
CREATE TABLE IF NOT EXISTS llm_model (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    provider VARCHAR(100),
    model_name VARCHAR(255) NOT NULL,
    description TEXT,
    api_endpoint VARCHAR(500),
    max_tokens INTEGER DEFAULT 4096,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LLM Test Cases table
CREATE TABLE IF NOT EXISTS llm_test_case (
    id BIGSERIAL PRIMARY KEY,
    model_id BIGINT NOT NULL REFERENCES llm_model(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    prompt TEXT NOT NULL,
    expected_output TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LLM Test Records table
CREATE TABLE IF NOT EXISTS llm_test_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    model_id BIGINT NOT NULL REFERENCES llm_model(id),
    prompt TEXT NOT NULL,
    response TEXT,
    parameters JSONB,
    response_time INTEGER,
    token_usage JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- API Keys table
CREATE TABLE IF NOT EXISTS api_key (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    model_id BIGINT NOT NULL REFERENCES llm_model(id),
    key_value VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) DEFAULT 'pending',
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approve_time TIMESTAMP,
    expiry_date TIMESTAMP
);

-- Skills table
CREATE TABLE IF NOT EXISTS skill (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    type VARCHAR(20) NOT NULL DEFAULT 'file',
    content TEXT,
    storage_path VARCHAR(500),
    view_count INTEGER DEFAULT 0,
    download_count INTEGER DEFAULT 0,
    like_count INTEGER DEFAULT 0,
    created_by BIGINT NOT NULL REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Skill Likes table
CREATE TABLE IF NOT EXISTS skill_like (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    skill_id BIGINT NOT NULL REFERENCES skill(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, skill_id)
);

-- Skill Favorites table
CREATE TABLE IF NOT EXISTS skill_favorite (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    skill_id BIGINT NOT NULL REFERENCES skill(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, skill_id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_llm_test_case_model ON llm_test_case(model_id);
CREATE INDEX IF NOT EXISTS idx_llm_test_record_user ON llm_test_record(user_id);
CREATE INDEX IF NOT EXISTS idx_llm_test_record_model ON llm_test_record(model_id);
CREATE INDEX IF NOT EXISTS idx_api_key_user ON api_key(user_id);
CREATE INDEX IF NOT EXISTS idx_api_key_model ON api_key(model_id);
CREATE INDEX IF NOT EXISTS idx_skill_category ON skill(category);
CREATE INDEX IF NOT EXISTS idx_skill_type ON skill(type);
CREATE INDEX IF NOT EXISTS idx_skill_like_user ON skill_like(user_id);
CREATE INDEX IF NOT EXISTS idx_skill_like_skill ON skill_like(skill_id);
CREATE INDEX IF NOT EXISTS idx_skill_favorite_user ON skill_favorite(user_id);
CREATE INDEX IF NOT EXISTS idx_skill_favorite_skill ON skill_favorite(skill_id);

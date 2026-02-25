-- Insert test user
INSERT INTO "user" (username, email, department, created_at) VALUES
('test_user', 'test@example.com', '研发部', NOW());

-- Insert LLM models
INSERT INTO llm_model (name, provider, model_name, description, api_endpoint, max_tokens, status, created_at, updated_at) VALUES
('GPT-4', 'OpenAI', 'gpt-4', 'GPT-4 是 OpenAI 最先进的大型语言模型，具有强大的理解和生成能力。', 'https://api.openai.com/v1/chat/completions', 8192, 'active', NOW(), NOW()),
('GPT-3.5 Turbo', 'OpenAI', 'gpt-3.5-turbo', 'GPT-3.5 Turbo 是一款快速且经济的语言模型，适合大多数应用场景。', 'https://api.openai.com/v1/chat/completions', 4096, 'active', NOW(), NOW()),
('Claude 3 Opus', 'Anthropic', 'claude-3-opus-20240229', 'Claude 3 Opus 是 Anthropic 最强大的模型，在复杂推理和创意任务上表现优异。', 'https://api.anthropic.com/v1/messages', 200000, 'active', NOW(), NOW()),
('Qwen-Turbo', '阿里云', 'qwen-turbo', 'Qwen-Turbo 是阿里云推出的高性能中文大模型，针对中文场景优化。', 'https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation', 8000, 'active', NOW(), NOW()),
('DeepSeek Chat', 'DeepSeek', 'deepseek-chat', 'DeepSeek Chat 是一款开源的高性能大语言模型。', 'https://api.deepseek.com/v1/chat/completions', 4096, 'active', NOW(), NOW());

-- Insert LLM test cases
INSERT INTO llm_test_case (model_id, name, prompt, expected_output, created_at) VALUES
(1, '基础问答', '请简单介绍一下人工智能。', '人工智能是计算机科学的一个分支，致力于创建能够模拟人类智能的系统。', NOW()),
(1, '代码生成', '请用 Python 写一个冒泡排序函数。', 'def bubble_sort(arr): ...', NOW()),
(2, '文本摘要', '请总结以下内容：人工智能正在改变我们的生活方式...', 'AI 正在深刻影响人类生活的各个方面。', NOW()),
(3, '创意写作', '请写一首关于春天的短诗。', '春风拂柳绿，细雨润花红...', NOW());

-- Insert Skills
INSERT INTO skill (name, description, category, type, content, view_count, download_count, like_count, created_by, created_at) VALUES
('代码审查 Skill', '这是一个用于代码审查的提示词模板，可以帮助检查代码质量和潜在问题。', '代码工具', 'file', '# 代码审查

你是一位资深代码审查专家，请审查以下代码：

## 审查要点
1. 代码风格是否符合规范
2. 是否有潜在的性能问题
3. 是否存在安全漏洞
4. 错误处理是否完善

## 审查结果
请给出详细的审查意见和改进建议。', 120, 45, 23, 1, NOW()),

('邮件撰写 Skill', '专业的邮件撰写提示词，适用于各种商务场景。', '写作助手', 'file', '# 商务邮件撰写

你是一位专业的商务沟通专家，请根据以下信息撰写一封邮件：

## 邮件要素
- 收件人：{收件人}
- 邮件主题：{主题}
- 关键内容：{内容}

## 要求
- 语言专业、简洁
- 结构清晰
- 礼貌得体

请撰写邮件：', 85, 32, 18, 1, NOW()),

('数据分析 Skill', '数据分析的通用提示词，支持多种分析场景。', '数据分析', 'file', '# 数据分析助手

你是一位数据分析专家，请分析以下数据：

## 分析内容
{数据内容}

## 分析要求
1. 描述数据的基本特征
2. 识别关键趋势和模式
3. 给出可行的建议

请提供详细的分析报告。', 67, 28, 15, 1, NOW()),

('产品描述 Skill', '产品营销文案的撰写模板。', '营销文案', 'file', '# 产品描述生成

你是一位专业的文案撰稿人，请为以下产品撰写描述：

## 产品信息
- 产品名称：{产品名}
- 核心功能：{功能}
- 目标用户：{用户群体}

## 写作要求
- 突出产品优势
- 语言有吸引力
- 符合品牌调性

请撰写产品描述：', 54, 19, 11, 1, NOW()),

('学习计划 Skill', '个性化学习计划的生成模板。', '学习工具', 'file', '# 学习计划制定

你是一位学习规划专家，请为以下需求制定学习计划：

## 学习目标
- 学习主题：{主题}
- 当前水平：{水平}
- 学习时间：{时间}
- 学习目的：{目的}

## 计划要求
- 阶段划分清晰
- 目标具体可衡量
- 资源推荐实用

请制定详细的学习计划：', 41, 15, 9, 1, NOW()),

('面试准备 Skill', '面试准备的全面提示词模板。', '求职工具', 'file', '# 面试准备指南

你是一位资深面试官和职业顾问，请帮助准备面试：

## 面试信息
- 职位：{职位}
- 公司：{公司}
- 应聘者背景：{背景}

## 准备内容
1. 常见面试问题及回答要点
2. 岗位相关技术考察重点
3. 应当准备的问题

请提供完整的面试准备建议：', 38, 12, 7, 1, NOW()),

('文档翻译 Skill', '多语言文档翻译的专业模板。', '翻译工具', 'file', '# 文档翻译

你是一位专业的翻译专家，请翻译以下文档：

## 翻译要求
- 源语言：{源语言}
- 目标语言：{目标语言}
- 文档类型：{文档类型}

## 翻译原则
- 准确传达原意
- 符合目标语言习惯
- 保持专业术语统一

请翻译以下内容：', 29, 8, 5, 1, NOW()),

('项目总结 Skill', '项目复盘和总结的提示词模板。', '项目管理', 'file', '# 项目总结报告

你是一位项目管理专家，请帮助总结项目：

## 项目信息
- 项目名称：{项目名}
- 周期：{周期}
- 成员：{成员}
- 关键里程碑：{里程碑}

## 总结内容
1. 项目成果概述
2. 关键成功因素
3. 遇到的挑战与应对
4. 经验教训
5. 改进建议

请生成项目总结报告：', 22, 6, 4, 1, NOW()),

('技术调研 Skill', '技术调研和分析的提示词模板。', '技术研究', 'file', '# 技术调研报告

你是一位技术专家，请对以下技术进行调研：

## 技术信息
- 技术名称：{技术名}
- 应用场景：{场景}
- 关注问题：{问题}

## 调研维度
1. 技术原理和特点
2. 优势和局限性
3. 适用场景分析
4. 竞品对比
5. 选型建议

请提供详细的技术调研报告：', 18, 5, 3, 1, NOW()),

('客户沟通 Skill', '客户沟通和话术的提示词模板。', '销售工具', 'file', '# 客户沟通指南

你是一位销售沟通专家，请提供沟通建议：

## 沟通场景
- 客户类型：{类型}
- 沟通目的：{目的}
- 当前阶段：{阶段}

## 沟通要点
1. 了解客户需求
2. 介绍产品价值
3. 处理异议
4. 推进下一步

请提供详细的沟通话术和注意事项：', 15, 4, 2, 1, NOW());

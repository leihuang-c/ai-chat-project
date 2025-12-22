package com.ai.chat.service.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai.chat.service.domain.model.ChatHistory;
import com.ai.chat.service.domain.repo.ChatHistoryRepo;
import com.ai.chat.service.infrastructure.feign.DeepSeekClient;

/**
 * Chat领域服务
 * 处理问答相关的核心业务逻辑，协调领域对象和外部依赖
 */
@Service
@Transactional
public class ChatService {

    @Autowired
    private ChatHistoryRepo chatHistoryRepo;

    @Autowired // 添加Autowired注解
    private DeepSeekClient deepSeekClient;

    // @Autowired // 添加Autowired注解
    // private GeminiClient geminiClient;

    /**
     * 处理用户问题并返回答案
     * 完整的问答处理流程：获取用户信息 → 生成RAG上下文 → 生成答案 → 保存历史
     *
     * @param userId    用户名
     * @param question  用户问题
     * @param sessionId 会话ID
     * @return AI生成的答案
     */
    public String processQuestion(Long userId, String question, String sessionId) {
        // 1. 获取用户信息
        // String userInfo = getUserInfo(userId);

        // 2. 生成RAG上下文
        // ChatRAG rag = generateRAGContext(question, userId);

        // 3. 使用Gemini生成答案
        String answer = generateAnswer(question);

        // 4. 保存问答历史
        saveChatHistory(userId.toString(), question, answer, sessionId);

        return answer;
    }

    /**
     * 使用Gemini生成答案
     * 结合RAG上下文和用户问题生成更准确的回答
     *
     * @param question 用户问题
     * @param rag      RAG上下文
     * @return Gemini生成的答案
     */
    private String generateAnswer(String question) {
        try {
            // 构建包含RAG上下文的增强问题
            // String enhancedQuestion = buildEnhancedQuestion(question, rag);

            // 调用deepSeek客户端获取答案
            String answer = deepSeekClient.askQuestion(question);

            // 可选：对答案进行后处理
            return postProcessAnswer(answer);

        } catch (Exception e) {
            // 如果Gemini调用失败，使用降级方案
            return generateFallbackAnswer(question);
        }
    }

    /**
     * 对Gemini的回答进行后处理
     *
     * @param rawAnswer 原始回答
     * @return 处理后的回答
     */
    private String postProcessAnswer(String rawAnswer) {
        // 这里可以添加一些后处理逻辑，比如：
        // - 移除不必要的标记
        // - 格式化回答
        // - 检查回答质量等

        return rawAnswer.trim();
    }

    /**
     * 降级方案：当Gemini调用失败时使用
     *
     * @param question 用户问题
     * @return 降级回答
     */
    private String generateFallbackAnswer(String question) {
        return String.format(
                "根据您的查询 '%s'，我找到以下相关信息：\n\n%s\n\n" +
                        "（注意：当前为简化模式回答，完整AI功能暂时不可用）",
                question);
    }

    /**
     * 保存问答历史
     *
     * @param userId    用户ID
     * @param question  用户问题
     * @param answer    AI回答
     * @param sessionId 会话ID
     */
    private void saveChatHistory(String userId, String question, String answer,
            String sessionId) {
        ChatHistory history = ChatHistory.createNew(userId, question, answer, sessionId);
        chatHistoryRepo.save(history);
    }

    /**
     * 获取用户的问答历史
     *
     * @param userId 用户ID
     * @return 用户的问答历史列表
     */
    public List<ChatHistory> getUserHistory(String userId) {
        return chatHistoryRepo.findHistoryByUserId(userId);
    }

    /**
     * 获取会话的问答历史
     *
     * @param sessionId 会话ID
     * @return 会话的问答历史列表
     */
    public List<ChatHistory> getSessionHistory(String sessionId) {
        return chatHistoryRepo.findHistoryBySession(sessionId);
    }

    /**
     * 根据ID获取问答记录
     *
     * @param id 记录ID
     * @return Chat历史记录
     * @throws RuntimeException 如果记录不存在
     */
    public ChatHistory getHistoryById(String id) {
        return chatHistoryRepo.findHistoryById(id)
                .orElseThrow(() -> new RuntimeException("问答记录不存在: " + id));
    }

    /**
     * 删除问答记录
     *
     * @param id 要删除的记录ID
     */
    public void deleteHistory(String id) {
        chatHistoryRepo.deleteById(id);
    }

    /**
     * 获取用户问答统计
     *
     * @param userId 用户ID
     * @return 用户的问答记录数量
     */
    public long getUserHistoryCount(String userId) {
        return chatHistoryRepo.countByUserId(userId);
    }

    /**
     * 批量删除会话历史
     *
     * @param sessionId 会话ID
     */
    public void clearSessionHistory(String sessionId) {
        List<ChatHistory> sessionHistory = getSessionHistory(sessionId);
        sessionHistory.forEach(history -> deleteHistory(history.getId().toString()));
    }
}
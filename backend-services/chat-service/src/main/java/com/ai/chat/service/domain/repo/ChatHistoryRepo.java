package com.ai.chat.service.domain.repo;

import java.util.List;
import java.util.Optional;

import com.ai.chat.service.domain.model.ChatHistory;

/**
 * Chat历史记录仓库接口
 * 定义领域层与基础设施层的契约，提供数据访问抽象
 */
public interface ChatHistoryRepo {

    /**
     * 保存Chat历史记录
     *
     * @param history 要保存的Chat历史记录
     * @return 保存后的Chat历史记录（包含生成的ID）
     */
    ChatHistory save(ChatHistory history);

    /**
     * 根据ID查找Chat历史记录
     *
     * @param id 记录ID
     * @return 包含Chat历史记录的Optional对象
     */
    Optional<ChatHistory> findHistoryById(String id);

    /**
     * 根据会话ID查找Chat历史记录
     *
     * @param sessionId 会话ID
     * @return 该会话的所有Chat历史记录列表
     */
    List<ChatHistory> findHistoryBySession(String sessionId);

    /**
     * 根据用户ID查找Chat历史记录
     *
     * @param userId 用户ID
     * @return 该用户的所有Chat历史记录列表
     */
    List<ChatHistory> findHistoryByUserId(String userId);

    /**
     * 根据用户ID和会话ID查找Chat历史记录
     *
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 符合条件的Chat历史记录列表
     */
    List<ChatHistory> findHistoryByUserIdAndSessionId(String userId, String sessionId);

    /**
     * 删除Chat历史记录
     *
     * @param id 要删除的记录ID
     */
    void deleteById(String id);

    /**
     * 获取用户最近的Chat历史记录
     *
     * @param userId 用户ID
     * @param limit 返回记录数量限制
     * @return 最近的Chat历史记录列表
     */
    List<ChatHistory> findRecentHistoryByUserId(String userId, int limit);

    /**
     * 统计用户的问答数量
     *
     * @param userId 用户ID
     * @return 该用户的问答记录总数
     */
    long countByUserId(String userId);
}
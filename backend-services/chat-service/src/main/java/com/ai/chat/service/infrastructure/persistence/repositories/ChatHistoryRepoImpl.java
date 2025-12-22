package com.ai.chat.service.infrastructure.persistence.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.ai.chat.service.domain.model.ChatHistory;
import com.ai.chat.service.domain.repo.ChatHistoryRepo;
import com.ai.chat.service.infrastructure.persistence.entities.ChatHistoryPO;
import com.ai.chat.service.infrastructure.persistence.mapper.ChatHistoryMapper;

import lombok.RequiredArgsConstructor;

/**
 * Chat历史记录仓库实现类
 * 实现领域层的ChatHistoryRepo接口，提供具体的持久化操作
 * 使用JpaChatHistoryRepository进行数据库操作，使用Mapper进行对象转换
 */
@Component
@RequiredArgsConstructor
public class ChatHistoryRepoImpl implements ChatHistoryRepo {

    private final JpaChatHistoryRepository jpaChatHistoryRepository;
    private final ChatHistoryMapper mapper;

    /**
     * 保存Chat历史记录
     *
     * @param history 领域对象
     * @return 保存后的领域对象（包含生成的ID）
     */
    @Override
    public ChatHistory save(ChatHistory history) {
        ChatHistoryPO chatHistoryPO = mapper.toPO(history);
        ChatHistoryPO savedPO = jpaChatHistoryRepository.save(chatHistoryPO);
        return mapper.toDomain(savedPO);
    }

    /**
     * 根据ID查找Chat历史记录
     *
     * @param id 记录ID（字符串格式）
     * @return 包含Chat历史记录的Optional对象
     */
    @Override
    public Optional<ChatHistory> findHistoryById(String id) {
        try {
            Long historyId = Long.parseLong(id);
            Optional<ChatHistoryPO> chatHistoryPO = jpaChatHistoryRepository.findById(historyId);
            return chatHistoryPO.map(mapper::toDomain);
        } catch (NumberFormatException e) {
            // 记录ID格式错误，返回空Optional
            return Optional.empty();
        }
    }

    /**
     * 根据会话ID查找Chat历史记录
     *
     * @param sessionId 会话ID
     * @return 该会话的所有Chat历史记录列表
     */
    @Override
    public List<ChatHistory> findHistoryBySession(String sessionId) {
        List<ChatHistoryPO> historyPOs = jpaChatHistoryRepository.findBySessionId(sessionId);
        return historyPOs.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID查找Chat历史记录
     *
     * @param userId 用户ID
     * @return 该用户的所有Chat历史记录列表
     */
    @Override
    public List<ChatHistory> findHistoryByUserId(String userId) {
        List<ChatHistoryPO> historyPOs = jpaChatHistoryRepository.findByUserId(userId);
        return historyPOs.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID和会话ID查找Chat历史记录
     *
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 符合条件的Chat历史记录列表
     */
    @Override
    public List<ChatHistory> findHistoryByUserIdAndSessionId(String userId, String sessionId) {
        List<ChatHistoryPO> historyPOs = jpaChatHistoryRepository.findByUserIdAndSessionId(userId, sessionId);
        return historyPOs.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * 删除Chat历史记录
     *
     * @param id 要删除的记录ID
     */
    @Override
    public void deleteById(String id) {
        try {
            Long historyId = Long.parseLong(id);
            jpaChatHistoryRepository.deleteById(historyId);
        } catch (NumberFormatException e) {
            // 记录日志：ID格式错误，忽略删除操作
            // log.warn("Invalid ID format for deletion: {}", id);
        }
    }

    /**
     * 获取用户最近的Chat历史记录
     *
     * @param userId 用户ID
     * @param limit 返回记录数量限制
     * @return 最近的Chat历史记录列表
     */
    @Override
    public List<ChatHistory> findRecentHistoryByUserId(String userId, int limit) {
        // 创建分页请求，按时间戳降序排列
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));

        // 调用JPA仓库的分页查询方法
        List<ChatHistoryPO> historyPOs = jpaChatHistoryRepository.findByUserId(userId, pageRequest);

        return historyPOs.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * 统计用户的问答数量
     *
     * @param userId 用户ID
     * @return 该用户的问答记录总数
     */
    @Override
    public long countByUserId(String userId) {
        return jpaChatHistoryRepository.countByUserId(userId);
    }
}
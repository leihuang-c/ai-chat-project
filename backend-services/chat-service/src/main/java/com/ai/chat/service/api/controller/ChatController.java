package com.ai.chat.service.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping; // 确保这个导入正确
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ai.chat.service.api.dto.ChatAskingRequest;
import com.ai.chat.service.api.dto.ChatHistoryDTO;
import com.ai.chat.service.api.dto.SaveHistoryRequest;
import com.ai.chat.service.application.dto.ChatHistoryQuery;
import com.ai.chat.service.application.dto.SaveHistoryCommand;
import com.ai.chat.service.application.service.ChatHistoryService;
import com.ai.chat.service.domain.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 聊天问答控制器
 * 提供问答相关的REST API接口
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat Service", description = "聊天问答服务接口")
public class ChatController {

    private final ChatService chatService;
    private final ChatHistoryService chatHistoryService;

    /**
     * 处理用户问题
     *
     * @param userId    用户ID
     * @param question  用户问题
     * @param sessionId 会话ID（可选）
     * @return AI生成的回答
     */
    @Operation(summary = "提交问题获取AI回答", description = "向AI提交问题并获取智能回答，支持上下文对话")
    @PostMapping("/ask")
    public ResponseEntity<String> askQuestion(
            @RequestBody ChatAskingRequest request) {
        try {
            String answer = chatService.processQuestion(request.getUserId(), request.getQuestion(),
                    request.getSessionId());
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("处理问题时发生错误: " + e.getMessage());
        }
    }

    /**
     * 保存问答历史记录
     *
     * @param request 保存请求
     * @return 保存后的历史记录
     */
    @PostMapping("/save")
    public ResponseEntity<ChatHistoryDTO> saveHistory(@RequestBody SaveHistoryRequest request) {
        SaveHistoryCommand command = new SaveHistoryCommand();
        command.setUserId(request.getUserId());
        command.setQuestion(request.getQuestion());
        command.setAnswer(request.getAnswer());
        command.setSessionId(request.getSessionId());
        command.setRagContext(request.getRagContext());

        ChatHistoryDTO dto = chatHistoryService.saveHistory(command);
        return ResponseEntity.ok(dto);
    }

    /**
     * 根据ID获取问答记录
     *
     * @param id 记录ID
     * @return 问答记录
     */
    @Operation(summary = "获取用户问答历史", description = "获取指定用户的所有问答记录，按时间倒序排列")
    @GetMapping("/history/{id}")
    public ResponseEntity<ChatHistoryDTO> getHistoryById(@PathVariable String id) {
        try {
            ChatHistoryDTO dto = chatHistoryService.getHistoryById(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查询用户问答历史
     *
     * @param userId    用户ID
     * @param sessionId 会话ID（可选）
     * @param page      页码（可选，默认1）
     * @param size      每页大小（可选，默认10）
     * @return 问答历史列表
     */
    @GetMapping("/history/user/{userId}")
    public ResponseEntity<List<ChatHistoryDTO>> getUserHistory(
            @PathVariable String userId,
            @RequestParam(required = false) String sessionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        ChatHistoryQuery query = new ChatHistoryQuery();
        query.setUserId(userId);
        query.setSessionId(sessionId);
        query.setPage(page);
        query.setSize(size);
        query.setDesc(true);

        List<ChatHistoryDTO> historyList = chatHistoryService.queryUserHistory(query);
        return ResponseEntity.ok(historyList);
    }

    /**
     * 查询会话问答历史
     *
     * @param sessionId 会话ID
     * @return 会话问答历史
     */
    @GetMapping("/history/session/{sessionId}")
    public ResponseEntity<List<ChatHistoryDTO>> getSessionHistory(@PathVariable String sessionId) {
        List<ChatHistoryDTO> historyList = chatHistoryService.querySessionHistory(sessionId);
        return ResponseEntity.ok(historyList);
    }

    /**
     * 删除问答记录
     *
     * @param id 记录ID
     * @return 操作结果
     */
    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable String id) {
        try {
            chatHistoryService.deleteHistory(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 清空会话历史
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/history/session/{sessionId}")
    public ResponseEntity<Void> clearSessionHistory(@PathVariable String sessionId) {
        try {
            chatHistoryService.clearSessionHistory(sessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户问答统计
     *
     * @param userId 用户ID
     * @return 问答记录数量
     */
    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<Long> getUserHistoryCount(@PathVariable String userId) {
        try {
            long count = chatHistoryService.getUserHistoryCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Feign客户端测试接口
     *
     * @return 测试结果
     */
    @GetMapping("/test")
    public String testFeign() {
        System.out.println("测试feign");
        // return chatService.processQuestion(1L, "测试问题", "test-session");
        return "OK";
    }
}
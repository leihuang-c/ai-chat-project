package com.ai.chat.service.api.dto;

import lombok.Data;

/**
 * 聊天问答请求DTO 
 * 用于接收前端发送的问答请求参数
 */
@Data
public class ChatAskingRequest {

    private Long userId;
    private String question;
    private String sessionId;
}

package com.ai.chat.service.domain.exception;

/**
 * Chat领域异常
 * 用于表示问答业务逻辑中的错误情况
 */
public class ChatDomainException extends RuntimeException {

    /**
     * 构造带有详细消息的领域异常
     *
     * @param message 异常详细信息
     */
    public ChatDomainException(String message) {
        super(message);
    }

    /**
     * 构造带有详细消息和原因的领域异常
     *
     * @param message 异常详细信息
     * @param cause 异常原因
     */
    public ChatDomainException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 获取异常类型
     *
     * @return 异常类型标识
     */
    public String getExceptionType() {
        return "CHAT_DOMAIN_EXCEPTION";
    }
}
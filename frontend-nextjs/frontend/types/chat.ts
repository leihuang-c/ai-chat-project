export interface Conversation {
  id: string;
  title: string;
  messages: Array<{
    id: string;
    role: "user" | "assistant";
    content: string;
    timestamp: Date;
  }>;
  createdAt: Date;
  updatedAt: Date;
  isHistory?: boolean; // 添加可选字段
}

export interface ChatMessage {
  id: string;
  role: "user" | "assistant";
  content: string;
  timestamp: Date;
}

// 后端历史对话类型
export interface HistoryConversation {
  id: string;
  userId: string;
  sessionId: string;
  question: string;
  answer: string;
  createTime: string;
  // 根据后端 ChatHistoryDTO 的实际字段调整
}

export interface ChatRequest {
  userId: number; // 用户唯一标识符
  question: string;
  sessionId: string;
}

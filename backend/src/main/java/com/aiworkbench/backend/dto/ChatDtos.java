package com.aiworkbench.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public final class ChatDtos {

	private ChatDtos() {}

	public record ConversationResponse(
			Long id,
			String title,
			Long projectId,
			Long modelProviderId,
			String modelName,
			LocalDateTime createdAt,
			LocalDateTime updatedAt
	) {}

	public record CreateConversationRequest(
			String title,
			Long projectId,
			Long modelProviderId,
			String modelName
	) {}

	public record MessageResponse(
			Long id,
			String role,
			String content,
			Integer promptTokens,
			Integer completionTokens,
			String modelName,
			LocalDateTime createdAt
	) {}

	public record SendMessageRequest(
			@NotBlank String content
	) {}

	/** SSE 推送的流式 chunk */
	public record ChatChunk(
			String content,          // 文本片段
			boolean done,            // 是否结束
			Long messageId,          // 结束时返回 assistant message ID
			Integer promptTokens,
			Integer completionTokens
	) {}
}

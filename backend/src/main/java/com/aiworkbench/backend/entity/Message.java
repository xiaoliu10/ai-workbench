package com.aiworkbench.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conversation_id", nullable = false)
	private Conversation conversation;

	/**
	 * 角色: user, assistant, system
	 */
	@Column(nullable = false, length = 20)
	private String role;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(name = "prompt_tokens")
	private Integer promptTokens;

	@Column(name = "completion_tokens")
	private Integer completionTokens;

	/**
	 * AI 来源的模型供应商 ID
	 */
	@Column(name = "model_provider_id")
	private Long modelProviderId;

	@Column(length = 100)
	private String modelName;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}

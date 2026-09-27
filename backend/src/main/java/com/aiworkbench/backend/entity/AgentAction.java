package com.aiworkbench.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agent_action", indexes = {
		@Index(name = "idx_action_conversation", columnList = "conversation_id, created_at"),
		@Index(name = "idx_action_status", columnList = "status")
})
public class AgentAction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "conversation_id", nullable = false)
	private Long conversationId;

	@Column(name = "message_id")
	private Long messageId;

	@Column(name = "agent_type", nullable = false, length = 32)
	private String agentType;

	@Column(name = "action_type", nullable = false, length = 64)
	private String actionType;

	@Column(name = "target_module", length = 16)
	private String targetModule;

	@Column(name = "risk_level", nullable = false, length = 16)
	private String riskLevel;

	@Column(nullable = false, length = 16)
	@Builder.Default
	private String status = "pending";

	@Column(columnDefinition = "jsonb", nullable = false)
	@Builder.Default
	private String params = "{}";

	@Column(columnDefinition = "jsonb")
	private String preview;

	@Column(columnDefinition = "jsonb")
	private String result;

	@Column(columnDefinition = "TEXT")
	private String error;

	@Column(name = "confirmed_by")
	private Long confirmedBy;

	@Column(name = "confirmed_at")
	private OffsetDateTime confirmedAt;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;
}

package com.aiworkbench.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "conversation")
public class Conversation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "created_by", nullable = false)
	private Long createdBy;

	@Column(length = 256)
	private String title;

	@Column(name = "project_id")
	private Long projectId;

	@Column(name = "model_provider_id")
	private Long modelProviderId;

	@Column(length = 100)
	private String modelName;

	@Column(nullable = false, length = 16)
	@Builder.Default
	private String status = "active";

	@Column(columnDefinition = "jsonb", nullable = false)
	@Builder.Default
	private String context = "{}";

	@Column(name = "last_message_at")
	private OffsetDateTime lastMessageAt;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;
}

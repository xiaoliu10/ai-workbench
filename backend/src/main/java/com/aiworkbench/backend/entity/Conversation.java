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

import java.time.LocalDateTime;

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

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(nullable = false, length = 300)
	private String title;

	/**
	 * 关联项目 ID（可选，对话可能在项目上下文中进行）
	 */
	@Column(name = "project_id")
	private Long projectId;

	/**
	 * 当前使用的模型供应商 ID
	 */
	@Column(name = "model_provider_id")
	private Long modelProviderId;

	/**
	 * 当前使用的模型名称
	 */
	@Column(length = 100)
	private String modelName;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}

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
@Table(name = "model_provider")
public class ModelProvider {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	/**
	 * 类型: COMMERCIAL_API, DOMESTIC_PLATFORM, PRIVATE_DEPLOYMENT
	 */
	@Column(nullable = false, length = 50)
	private String type;

	@Column(nullable = false, length = 500)
	private String endpoint;

	@Column(length = 500)
	private String apiKey;

	/**
	 * 状态: ACTIVE, INACTIVE, PENDING
	 */
	@Column(nullable = false, length = 20)
	@Builder.Default
	private String status = "PENDING";

	/**
	 * 支持的模型列表，JSON 数组格式 ["gpt-4", "gpt-3.5-turbo"]
	 */
	@Column(columnDefinition = "TEXT")
	private String models;

	@Column(length = 100)
	private String owner;

	@Column(name = "quota_percent")
	@Builder.Default
	private Integer quotaPercent = 0;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}

package com.aiworkbench.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requirement")
public class Requirement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 业务编号，如 REQ-2026-001
	 */
	@Column(nullable = false, unique = true, length = 30)
	private String reqCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@Column(nullable = false, length = 300)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	/**
	 * 优先级: LOW, MEDIUM, HIGH, URGENT
	 */
	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private Priority priority = Priority.MEDIUM;

	/**
	 * 状态
	 */
	@Column(nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private ReqStatus status = ReqStatus.DRAFT;

	/**
	 * 创建人 ID
	 */
	@Column(name = "creator_id")
	private Long creatorId;

	/**
	 * 负责人 ID
	 */
	@Column(name = "assignee_id")
	private Long assigneeId;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	public enum Priority {
		LOW, MEDIUM, HIGH, URGENT
	}

	public enum ReqStatus {
		DRAFT,           // 草稿
		IN_REVIEW,       // 评审中
		APPROVED,        // 已批准
		IN_DEVELOPMENT,  // 开发中
		IN_TESTING,      // 测试中
		COMPLETED,       // 已完成
		CLOSED           // 已关闭
	}
}

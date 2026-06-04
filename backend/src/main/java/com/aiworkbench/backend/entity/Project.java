package com.aiworkbench.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "project")
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 业务编号，如 PROJ-2026-001
	 */
	@Column(nullable = false, unique = true, length = 30)
	private String projectCode;

	@Column(nullable = false, length = 200)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	/**
	 * 状态
	 */
	@Column(nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private ProjectStatus status = ProjectStatus.PLANNING;

	/**
	 * 负责人 ID
	 */
	@Column(name = "owner_id")
	private Long ownerId;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	public enum ProjectStatus {
		PLANNING,       // 规划中
		IN_PROGRESS,    // 进行中
		COMPLETED,      // 已完成
		ARCHIVED        // 已归档
	}
}

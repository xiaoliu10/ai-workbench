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
@Table(name = "artifact", indexes = {
		@Index(name = "idx_artifact_project", columnList = "project_id, type")
})
public class Artifact {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "project_id", nullable = false)
	private Long projectId;

	@Column(nullable = false, length = 32)
	private String type;

	@Column(name = "storage_backend", nullable = false, length = 16)
	private String storageBackend;

	@Column(columnDefinition = "TEXT")
	private String uri;

	@Column(name = "object_key", length = 512)
	private String objectKey;

	@Column(length = 64)
	private String version;

	@Column(columnDefinition = "jsonb", nullable = false)
	@Builder.Default
	private String meta = "{}";

	@Column(name = "created_by", nullable = false)
	private Long createdBy;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;
}

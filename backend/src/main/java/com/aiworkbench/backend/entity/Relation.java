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

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "relation", indexes = {
		@Index(name = "idx_relation_from", columnList = "from_type, from_id"),
		@Index(name = "idx_relation_to", columnList = "to_type, to_id")
})
public class Relation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "from_type", nullable = false, length = 32)
	private String fromType;

	@Column(name = "from_id", nullable = false)
	private Long fromId;

	@Column(name = "to_type", nullable = false, length = 32)
	private String toType;

	@Column(name = "to_id", nullable = false)
	private Long toId;

	@Column(name = "relation_kind", nullable = false, length = 32)
	private String relationKind;

	@Column(name = "created_by")
	private Long createdBy;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;
}

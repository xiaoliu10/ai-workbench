package com.aiworkbench.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class RequirementDtos {

	private RequirementDtos() {}

	public record RequirementResponse(
			Long id,
			String reqCode,
			Long projectId,
			String projectCode,
			String projectName,
			String title,
			String description,
			String priority,
			String status,
			Long creatorId,
			Long assigneeId,
			LocalDateTime createdAt,
			LocalDateTime updatedAt
	) {}

	public record CreateRequirementRequest(
			@NotNull Long projectId,
			@NotBlank String title,
			String description,
			String priority,
			Long assigneeId
	) {}

	public record UpdateRequirementRequest(
			String title,
			String description,
			String priority,
			String status,
			Long assigneeId
	) {}
}

package com.aiworkbench.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public final class ProjectDtos {

	private ProjectDtos() {}

	public record ProjectResponse(
			Long id,
			String projectCode,
			String name,
			String description,
			String status,
			Long ownerId,
			String ownerName,
			LocalDateTime createdAt,
			LocalDateTime updatedAt
	) {}

	public record CreateProjectRequest(
			@NotBlank String name,
			String description
	) {}

	public record UpdateProjectRequest(
			String name,
			String description,
			String status
	) {}
}

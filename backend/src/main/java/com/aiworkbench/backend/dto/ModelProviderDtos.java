package com.aiworkbench.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public final class ModelProviderDtos {

	private ModelProviderDtos() {}

	public record ProviderResponse(
			Long id,
			String name,
			String type,
			String endpoint,
			String status,
			String models,
			String owner,
			Integer quotaPercent,
			LocalDateTime createdAt,
			LocalDateTime updatedAt
	) {}

	public record CreateProviderRequest(
			@NotBlank String name,
			@NotBlank String type,
			@NotBlank String endpoint,
			String apiKey,
			String models,
			String owner
	) {}

	public record UpdateProviderRequest(
			String name,
			String type,
			String endpoint,
			String apiKey,
			String models,
			String owner,
			String status,
			Integer quotaPercent
	) {}
}

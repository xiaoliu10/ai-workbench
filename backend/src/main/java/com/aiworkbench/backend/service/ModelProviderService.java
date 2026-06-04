package com.aiworkbench.backend.service;

import com.aiworkbench.backend.common.BizException;
import com.aiworkbench.backend.dto.ModelProviderDtos;
import com.aiworkbench.backend.entity.ModelProvider;
import com.aiworkbench.backend.repository.ModelProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelProviderService {

	private final ModelProviderRepository providerRepository;

	@Transactional(readOnly = true)
	public List<ModelProviderDtos.ProviderResponse> listAll() {
		return providerRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public List<ModelProviderDtos.ProviderResponse> listActive() {
		return providerRepository.findByStatusOrderByCreatedAtDesc("ACTIVE").stream()
				.map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public ModelProviderDtos.ProviderResponse getById(Long id) {
		return toResponse(providerRepository.findById(id)
				.orElseThrow(() -> BizException.notFound("供应商不存在")));
	}

	@Transactional
	public ModelProviderDtos.ProviderResponse create(ModelProviderDtos.CreateProviderRequest req) {
		ModelProvider provider = ModelProvider.builder()
				.name(req.name())
				.type(req.type())
				.endpoint(req.endpoint())
				.apiKey(req.apiKey())
				.status("PENDING")
				.models(req.models())
				.owner(req.owner())
				.build();
		return toResponse(providerRepository.save(provider));
	}

	@Transactional
	public ModelProviderDtos.ProviderResponse update(Long id, ModelProviderDtos.UpdateProviderRequest req) {
		ModelProvider p = providerRepository.findById(id)
				.orElseThrow(() -> BizException.notFound("供应商不存在"));

		if (req.name() != null) p.setName(req.name());
		if (req.type() != null) p.setType(req.type());
		if (req.endpoint() != null) p.setEndpoint(req.endpoint());
		if (req.apiKey() != null) p.setApiKey(req.apiKey());
		if (req.models() != null) p.setModels(req.models());
		if (req.owner() != null) p.setOwner(req.owner());
		if (req.status() != null) p.setStatus(req.status());
		if (req.quotaPercent() != null) p.setQuotaPercent(req.quotaPercent());

		return toResponse(providerRepository.save(p));
	}

	@Transactional
	public void delete(Long id) {
		if (!providerRepository.existsById(id)) throw BizException.notFound("供应商不存在");
		providerRepository.deleteById(id);
	}

	private ModelProviderDtos.ProviderResponse toResponse(ModelProvider p) {
		return new ModelProviderDtos.ProviderResponse(
				p.getId(), p.getName(), p.getType(), p.getEndpoint(),
				p.getStatus(), p.getModels(), p.getOwner(), p.getQuotaPercent(),
				p.getCreatedAt(), p.getUpdatedAt());
	}
}

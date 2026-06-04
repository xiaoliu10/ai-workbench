package com.aiworkbench.backend.repository;

import com.aiworkbench.backend.entity.ModelProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelProviderRepository extends JpaRepository<ModelProvider, Long> {

	List<ModelProvider> findByStatusOrderByCreatedAtDesc(String status);

	boolean existsByName(String name);
}

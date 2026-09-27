package com.aiworkbench.backend.repository;

import com.aiworkbench.backend.entity.Artifact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtifactRepository extends JpaRepository<Artifact, Long> {

	List<Artifact> findByProjectIdAndType(Long projectId, String type);

	List<Artifact> findByProjectId(Long projectId);
}

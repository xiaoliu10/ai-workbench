package com.aiworkbench.backend.repository;

import com.aiworkbench.backend.entity.Requirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Long> {

	List<Requirement> findByProjectIdOrderByCreatedAtDesc(Long projectId);

	@Query("SELECT r FROM Requirement r WHERE " +
			"r.project.id = :projectId " +
			"AND (:keyword IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
			"OR LOWER(r.reqCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
			"AND (:status IS NULL OR r.status = :status) " +
			"AND (:priority IS NULL OR r.priority = :priority) " +
			"ORDER BY r.createdAt DESC")
	Page<Requirement> searchRequirements(@Param("projectId") Long projectId,
										 @Param("keyword") String keyword,
										 @Param("status") Requirement.ReqStatus status,
										 @Param("priority") Requirement.Priority priority,
										 Pageable pageable);

	@Query("SELECT MAX(CAST(SUBSTRING(r.reqCode, LENGTH(r.reqCode) - 2) AS integer)) " +
			"FROM Requirement r WHERE r.reqCode LIKE CONCAT(:prefix, '%')")
	Integer findMaxSeqByPrefix(@Param("prefix") String prefix);
}

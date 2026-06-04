package com.aiworkbench.backend.repository;

import com.aiworkbench.backend.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

	@Query("SELECT p FROM Project p WHERE " +
			"(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
			"OR LOWER(p.projectCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
			"AND (:status IS NULL OR p.status = :status) " +
			"AND (:ownerId IS NULL OR p.ownerId = :ownerId) " +
			"ORDER BY p.createdAt DESC")
	Page<Project> searchProjects(@Param("keyword") String keyword,
								 @Param("status") Project.ProjectStatus status,
								 @Param("ownerId") Long ownerId,
								 Pageable pageable);

	/**
	 * 查找当前年份最大编号，用于生成 PROJ-yyyy-NNN
	 */
	@Query("SELECT MAX(CAST(SUBSTRING(p.projectCode, LENGTH(p.projectCode) - 2) AS integer)) " +
			"FROM Project p WHERE p.projectCode LIKE CONCAT(:prefix, '%')")
	Integer findMaxSeqByPrefix(@Param("prefix") String prefix);
}

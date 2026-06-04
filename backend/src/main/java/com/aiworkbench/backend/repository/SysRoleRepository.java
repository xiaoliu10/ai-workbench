package com.aiworkbench.backend.repository;

import com.aiworkbench.backend.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {

	boolean existsByRoleCode(String roleCode);

	Optional<SysRole> findByRoleCode(String roleCode);

	@Query("SELECT r FROM SysRole r " +
			"WHERE (:keyword IS NULL OR LOWER(r.roleName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
			"OR LOWER(r.roleCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
			"AND (:status IS NULL OR r.status = :status)")
	Page<SysRole> searchRoles(@Param("keyword") String keyword,
							  @Param("status") Integer status,
							  Pageable pageable);
}

package com.aiworkbench.backend.repository;

import com.aiworkbench.backend.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {

	@EntityGraph(attributePaths = "roles")
	Optional<SysUser> findByUsername(String username);

	boolean existsByUsername(String username);

	@EntityGraph(attributePaths = "roles")
	Optional<SysUser> findWithRolesById(Long id);

	@Query("SELECT u FROM SysUser u LEFT JOIN FETCH u.roles r " +
			"WHERE (:keyword IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
			"OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
			"AND (:status IS NULL OR u.status = :status)")
	Page<SysUser> searchUsers(@Param("keyword") String keyword,
							  @Param("status") Integer status,
							  Pageable pageable);
}

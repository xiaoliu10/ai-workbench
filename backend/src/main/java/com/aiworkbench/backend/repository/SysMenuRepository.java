package com.aiworkbench.backend.repository;

import com.aiworkbench.backend.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysMenuRepository extends JpaRepository<SysMenu, Long> {

	List<SysMenu> findByParentIsNullOrderBySortOrder();

	List<SysMenu> findByParentIdOrderBySortOrder(Long parentId);

	boolean existsByMenuCode(String menuCode);

	Optional<SysMenu> findByMenuCode(String menuCode);

	/**
	 * 查询指定角色关联的菜单（含按钮权限）
	 */
	@Query("SELECT DISTINCT m FROM SysRole r JOIN r.menus m WHERE r.id IN :roleIds AND m.status = 1 ORDER BY m.sortOrder")
	List<SysMenu> findByRoleIds(@Param("roleIds") List<Long> roleIds);

	/**
	 * 查询所有启用的菜单（树形结构）
	 */
	@Query("SELECT m FROM SysMenu m WHERE m.status = 1 ORDER BY m.sortOrder")
	List<SysMenu> findAllActive();
}

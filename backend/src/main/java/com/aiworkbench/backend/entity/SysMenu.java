package com.aiworkbench.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_menu")
public class SysMenu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private SysMenu parent;

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	@Builder.Default
	private List<SysMenu> children = new ArrayList<>();

	@Column(nullable = false, length = 100)
	private String menuName;

	@Column(nullable = false, unique = true, length = 100)
	private String menuCode;

	@Column(length = 255)
	private String path;

	@Column(length = 255)
	private String component;

	@Column(length = 100)
	private String icon;

	@Column(name = "sort_order")
	@Builder.Default
	private Integer sortOrder = 0;

	/**
	 * 菜单类型: DIRECTORY=目录, MENU=菜单, BUTTON=按钮/权限点
	 */
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private MenuType menuType = MenuType.MENU;

	/**
	 * 权限标识，如 sys:user:list, sys:user:create
	 */
	@Column(length = 100)
	private String permission;

	/**
	 * 是否可见: 1=显示, 0=隐藏
	 */
	@Column(nullable = false)
	@Builder.Default
	private Integer visible = 1;

	/**
	 * 状态: 1=正常, 0=禁用
	 */
	@Column(nullable = false)
	@Builder.Default
	private Integer status = 1;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	public enum MenuType {
		DIRECTORY, MENU, BUTTON
	}
}

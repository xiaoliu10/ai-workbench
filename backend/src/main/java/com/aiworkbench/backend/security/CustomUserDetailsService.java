package com.aiworkbench.backend.security;

import com.aiworkbench.backend.entity.SysMenu;
import com.aiworkbench.backend.entity.SysRole;
import com.aiworkbench.backend.entity.SysUser;
import com.aiworkbench.backend.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final SysUserRepository sysUserRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SysUser user = sysUserRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

		if (user.getStatus() != 1) {
			throw new UsernameNotFoundException("用户已被禁用: " + username);
		}

		List<GrantedAuthority> authorities = new ArrayList<>();

		for (SysRole role : user.getRoles()) {
			// 添加角色权限 (ROLE_ 前缀)
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));

			// 添加菜单/按钮权限
			for (SysMenu menu : role.getMenus()) {
				if (menu.getPermission() != null && !menu.getPermission().isBlank()) {
					authorities.add(new SimpleGrantedAuthority(menu.getPermission()));
				}
			}
		}

		return new User(user.getUsername(), user.getPassword(), authorities);
	}

	/**
	 * 获取用户的角色编码列表
	 */
	@Transactional(readOnly = true)
	public List<String> getUserRoleCodes(SysUser user) {
		return user.getRoles().stream()
				.map(SysRole::getRoleCode)
				.collect(Collectors.toList());
	}
}

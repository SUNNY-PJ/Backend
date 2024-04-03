package com.sunny.backend.auth.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.sunny.backend.user.domain.Users;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CustomUserPrincipal implements OAuth2User, UserDetails {
	private final Users users;
	private final Long id;
	private final Collection<GrantedAuthority> authorities;
	@Setter
	private Map<String, Object> attributes;

	public CustomUserPrincipal(Users users) {
		this.users = users;
		this.id = users.getId();
		authorities = new ArrayList<>();
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + users.getRole());
		authorities.add(authority);
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return users.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getName() {
		return null;
	}

	//	@Override
	//	public String getName() {
	//		return users.getName();
	//	}
}

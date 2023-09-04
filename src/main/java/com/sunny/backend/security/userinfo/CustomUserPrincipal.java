package com.sunny.backend.security.userinfo;

import java.util.*;

import com.sunny.backend.user.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CustomUserPrincipal implements OAuth2User, UserDetails {
	private Users users;

	private Collection<? extends GrantedAuthority> authorities;
	@Setter
	private Map<String, Object> attributes;

	public CustomUserPrincipal(Users users) {
		this.users = users;
	}

	public CustomUserPrincipal(Users users, Collection<? extends GrantedAuthority> authorities,
		Map<String, Object> attributes) {
		this.users = users;
		this.authorities = authorities;
		this.attributes = attributes;
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
		return users.getName();
	}
}

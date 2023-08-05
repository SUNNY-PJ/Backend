package com.sunny.backend.security.userinfo;

import java.util.*;

import com.sunny.backend.user.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal implements OAuth2User, UserDetails {
	private Long id;
	private String email;

	private Collection<? extends GrantedAuthority> authorities;
	@Setter
	private Map<String, Object> attributes;
	private Users users;

	public Users getUser() {
		return users;
	}
	public CustomUserPrincipal(Long id, String email,  Collection<? extends GrantedAuthority> authorities, Users users) {
		this.id = id;
		this.email = email;
		this.authorities = authorities;
		this.users = users;
	}
	public static CustomUserPrincipal create(Users users) {
		List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

		return new CustomUserPrincipal(users.getId(), users.getEmail(), authorities, users);
	}

	public static CustomUserPrincipal create(Users users, Map<String, Object> attributes) {
		CustomUserPrincipal customUserPrincipal = CustomUserPrincipal.create(users);
		customUserPrincipal.setAttributes(attributes);
		return customUserPrincipal;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return email;
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
		return id.toString();
	}
}

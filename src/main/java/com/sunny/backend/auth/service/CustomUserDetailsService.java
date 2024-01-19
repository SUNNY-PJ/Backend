package com.sunny.backend.auth.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public CustomUserPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
		Users users = userRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
		return new CustomUserPrincipal(users);
	}
}

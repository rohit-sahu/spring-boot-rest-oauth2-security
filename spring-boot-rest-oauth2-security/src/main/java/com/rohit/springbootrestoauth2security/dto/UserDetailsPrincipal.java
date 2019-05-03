package com.rohit.springbootrestoauth2security.dto;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.rohit.springbootrestoauth2security.model.User;

public class UserDetailsPrincipal extends User implements UserDetails {

	private static final long serialVersionUID = 1L;

	public UserDetailsPrincipal() {
		super();
	}

	public UserDetailsPrincipal(User user) {
		super(user.getUser_id(), user.getPassword(), user.getUsername(), user.isAccountNonExpired(),
				user.isAccountNonLocked(), user.isCredentialsNonExpired(), user.isEnabled(), user.getRoles());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return super.getRoles()
			.stream()
			.map(role ->
				new SimpleGrantedAuthority("ROLE_" + role.getRoleName())
			).collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return super.getPassword();
	}

	@Override
	public String getUsername() {
		return super.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}
}

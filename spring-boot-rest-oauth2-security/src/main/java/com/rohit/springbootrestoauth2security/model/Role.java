package com.rohit.springbootrestoauth2security.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

@Entity
@Table
public class Role implements Serializable, GrantedAuthority {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "role_id")
	private Long role_id;

	@Column(name = "roleName")
	private String roleName;

	public Role() {
		super();
	}

	public Role(Long role_id, String roleName) {
		super();
		this.role_id = role_id;
		this.roleName = roleName;
	}

	public Long getRole_id() {
		return role_id;
	}

	public void setRole_id(Long role_id) {
		this.role_id = role_id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return "Role [role_id=" + role_id + ", roleName=" + roleName + "]";
	}

	@Override
	public String getAuthority() {
		return roleName;
	}
}

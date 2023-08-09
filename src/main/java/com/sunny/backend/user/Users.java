package com.sunny.backend.user;

import javax.persistence.*;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sunny.backend.entity.BaseTime;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Consumption;
import com.sunny.backend.entity.Friends;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column
	private String email;

	@Column
	private String name;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Role role;

	@Column//(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private AuthProvider authProvider;
	@OneToMany(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	@Builder.Default
	private List<Community> communityList =new ArrayList<>();

	@OneToMany(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	@Builder.Default
	private List<Consumption> consumptionList =new ArrayList<>();
	@Column
	private String providerId;

	@Column
	private String profile;

	@OneToMany(mappedBy = "users")
	private List<Friends> userList = new ArrayList<>();

	@OneToMany(mappedBy = "friends")
	private List<Friends> friendsList = new ArrayList<>();

}
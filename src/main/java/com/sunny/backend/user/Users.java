package com.sunny.backend.user;

import javax.persistence.*;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sunny.backend.entity.BaseTime;
import com.sunny.backend.entity.Comment;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Consumption;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(nullable = false)
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

	@OneToMany(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	@Builder.Default
	@JsonIgnore
	private List<Comment> commentList =new ArrayList<>();
	@Column
	private String providerId;

	@Column
	private String profile;

}
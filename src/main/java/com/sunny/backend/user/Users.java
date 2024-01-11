package com.sunny.backend.user;

import com.sunny.backend.save.domain.Save;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunny.backend.entity.*;
import com.sunny.backend.friends.domain.Friend;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	@Column(unique = true)
	private String email;

	@Size(min=2,max=10)

	@Column(unique = true,nullable = false)
	private String name;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Role role;

	@Column
	@Enumerated(value = EnumType.STRING)
	private AuthProvider authProvider;
	@OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
	private List<Community> communityList;

	@OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
	private List<Consumption> consumptionList;

	@OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Comment> commentList;
	@OneToOne(mappedBy = "users")
	private Save save;
	@OneToMany(mappedBy = "users")
	private List<Scrap> scrapList;

	@Column
	private String providerId;

	@Column
	private String profile;

	@OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
	private List<Friend> friends = new ArrayList<>();

	@OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
	private List<Notification> notification;

	public void addComment(Comment comment) {
		this.commentList = new ArrayList<>();
		this.commentList.add(comment);
	}

	public void addCommunity(Community community) {
		this.communityList = new ArrayList<>();
		this.communityList.add(community);
	}

	public void addConsumption(Consumption consumption) {
		this.consumptionList = new ArrayList<>();
		this.consumptionList.add(consumption);
	}

	public void addSave(Save save) {
		if (save == null) {
			return;
		}
		if (this.save != null) {

			throw new IllegalStateException("이미 절약 목표가 존재합니다.");
		} else {
			this.save = save;
		}
	}
}
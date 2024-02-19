package com.sunny.backend.user.domain;

import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.common.BaseTime;
import com.sunny.backend.community.domain.Community;

import com.sunny.backend.consumption.domain.Consumption;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.save.domain.Save;
import com.sunny.backend.scrap.domain.Scrap;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunny.backend.friends.domain.Friend;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Builder
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(unique = true)
	private String email;


	@Column(nullable = false)
	private String name;

	@Size(min=2,max=10)
	@Column(unique = true)
	private String nickname;

	private String oauthId;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Role role;

	@OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
	private List<Community> communityList;

	@OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
	private List<Consumption> consumptionList;

	@OneToMany(mappedBy = "users")
	@JsonIgnore
	private List<Comment> commentList;

	@OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
	private List<Save> saveList;

	@OneToMany(mappedBy = "users")
	private List<Scrap> scrapList;

	@Column
	private String providerId;

	@Column
	private String profile;

	@ColumnDefault("0")
	private int reportCount;

	@OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
	private List<Friend> friends = new ArrayList<>();

	@OneToMany(mappedBy = "users")
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
		this.saveList = new ArrayList<>();
		this.saveList.add(save);
		}

	public void updateName(String name) {
		this.nickname = name;
	}

	public void updateProfile(String profile) {
		this.profile = profile;
	}

	public void increaseReportCount() {
		reportCount++;
	}
}


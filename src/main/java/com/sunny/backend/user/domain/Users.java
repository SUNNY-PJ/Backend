package com.sunny.backend.user.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import lombok.Builder.Default;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.common.BaseTime;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.consumption.domain.Consumption;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.save.domain.Save;
import com.sunny.backend.scrap.domain.Scrap;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
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

	@Column
	private String oauthId;

	@Size(min = 2, max = 10)
	@Column(unique = true)
	private String nickname;


	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Role role;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Community> communityList;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Consumption> consumptionList;

	@OneToMany(mappedBy = "users")
	@JsonIgnore
	private List<Comment> commentList;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Save> saveList;

	@OneToMany(mappedBy = "users")
	private List<Scrap> scrapList;

	@Column
	private String profile;

	@ColumnDefault("0")
	private int reportCount;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
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

	@Builder
	public  Users(String email,String oauthId,Role role){
		this.email=email;
		this.oauthId=oauthId;
		this.role=role;
		this.profile="https://sunny-pj.s3.ap-northeast-2.amazonaws.com/Profile+Image.png";
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

	public boolean isOwner(Long id) {
		return this.id.equals(id);
	}
}


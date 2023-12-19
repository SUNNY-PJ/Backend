package com.sunny.backend.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunny.backend.entity.BaseTime;
import com.sunny.backend.entity.Comment;
import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Consumption;
import com.sunny.backend.entity.Save;
import com.sunny.backend.entity.Scrap;
import com.sunny.backend.entity.friends.Friends;

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

	@Column
	private String email;

	@Column
	private String name;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Role role;

	@Column
	@Enumerated(value = EnumType.STRING)
	private AuthProvider authProvider;
	@OneToMany(mappedBy = "users")
	private List<Community> communityList;

	@OneToMany(mappedBy = "users")
	private List<Consumption> consumptionList;

	@OneToMany(mappedBy = "users")
	@JsonIgnore
	private  List<Comment> commentList;
	@OneToOne(mappedBy = "users")
	private Save save;

	@OneToMany(mappedBy = "users")
	private List<Scrap> scrapList;

	@Column
	private String providerId;

	@Column
	private String profile;

	@OneToMany(mappedBy = "users")
	private List<Friends> userList = new ArrayList<>();

	@OneToMany(mappedBy = "friend")
	private List<Friends> friendsList = new ArrayList<>();

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
}

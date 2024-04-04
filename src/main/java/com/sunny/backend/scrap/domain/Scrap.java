package com.sunny.backend.scrap.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.sunny.backend.community.domain.Community;
import com.sunny.backend.user.domain.Users;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Users users;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Community community;

	private Scrap(Users users, Community community) {
		this.users = users;
		this.community = community;
	}

	public static Scrap of(Users users, Community community) {
		Scrap scrap = new Scrap(users, community);
		users.addScrap(scrap);
		return scrap;
	}

	public boolean isScrapByCommunityId(Long communityId) {
		return id.equals(communityId);
	}
}

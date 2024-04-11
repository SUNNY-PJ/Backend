package com.sunny.backend.scrap.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
	private Users users;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id")
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
		return community.getId().equals(communityId);
	}
}

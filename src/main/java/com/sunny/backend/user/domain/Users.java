package com.sunny.backend.user.domain;

import static com.sunny.backend.common.ComnConstant.*;
import static com.sunny.backend.scrap.exception.ScrapErrorCode.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;

import com.sunny.backend.auth.exception.UserErrorCode;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.common.BaseTime;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.consumption.domain.Consumption;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.save.domain.Save;
import com.sunny.backend.save.exception.SaveErrorCode;
import com.sunny.backend.scrap.domain.Scrap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@DynamicInsert
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
	private final List<Community> communityList = new ArrayList<>();

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Consumption> consumptionList = new ArrayList<>();

	@OneToMany(mappedBy = "users")
	private final List<Comment> commentList = new ArrayList<>();

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Save> saves = new ArrayList<>();

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Scrap> scraps = new ArrayList<>();

	@Column
	private String profile;

	@Embedded
	private UserReport userReport;

	@OneToMany(mappedBy = "users")
	private final List<Friend> friends = new ArrayList<>();

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Notification> notification = new ArrayList<>();

	public Users(String email, String oauthId, String nickname, Role role, String profile, UserReport userReport) {
		this.email = email;
		this.oauthId = oauthId;
		this.nickname = nickname;
		this.role = role;
		this.profile = profile;
		this.userReport = userReport;
	}

	public static Users of(String email, String oauthId) {
		return new Users(email, oauthId, null, Role.USER, SUNNY_DEFAULT_IMAGE, UserReport.from(0));
	}

	public void addComment(Comment comment) {
		this.commentList.add(comment);
	}

	public void addCommunity(Community community) {
		this.communityList.add(community);
	}

	public void addConsumption(Consumption consumption) {
		this.consumptionList.add(consumption);
	}

	public void addSave(Save save) {
		this.saves.add(save);
	}

	public void addScrap(Scrap scrap) {
		scraps.add(scrap);
	}

	public int getSaveSize() {
		return saves.size();
	}

	public Save getLastSaveOrException() {
		if (getSaveSize() <= 0) {
			throw new CustomException(SaveErrorCode.SAVE_NOT_FOUND);
		}

		return getSaves().get(getSaveSize() - 1);
	}

	public boolean isExistLastSave() {
		if (!saves.isEmpty()) {
			Save save = getSaves().get(getSaveSize() - 1);
			return save.isValidSave();
		}
		return false;
	}

	public boolean isScrapByCommunity(Long communityId) {
		return scraps.stream()
			.anyMatch(scrap -> scrap.isScrapByCommunityId(communityId));
	}

	public Scrap findScrapByCommunity(Long communityId) {
		return scraps.stream()
			.filter(scrap -> scrap.isScrapByCommunityId(communityId))
			.findAny()
			.orElseThrow(() -> new CustomException(SCRAP_NOT_FOUND));
	}

	public void validateScrapByCommunity(Long communityId) {
		for (Scrap scrap : scraps) {
			if (scrap.isScrapByCommunityId(communityId)) {
				throw new CustomException(SCRAP_ALREADY);
			}
		}
	}

	public void updateName(String name) {
		this.nickname = name;
	}

	public void updateProfile(String profile) {
		this.profile = profile;
	}

	public boolean isOwner(Long id) {
		return this.id.equals(id);
	}

	public void canNotMySelf(Long id) {
		if (this.id.equals(id)) {
			throw new CustomException(UserErrorCode.CANNOT_MYSELF);
		}
	}

	public void increaseReportCount() {
		this.userReport.increase();
	}

	public boolean isReportLimitReached() {
		return this.userReport.isReportLimitReached();
	}

	public int getReportCount() {
		return userReport.getReportCount();
	}

	public void deleteScrap(Scrap scrap) {
		this.scraps.remove(scrap);
	}
}


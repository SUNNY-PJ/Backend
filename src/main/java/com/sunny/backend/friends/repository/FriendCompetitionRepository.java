package com.sunny.backend.friends.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.FriendCompetition;
import com.sunny.backend.friends.domain.FriendCompetitionStatus;
import com.sunny.backend.friends.dto.response.FriendCompetitionQuery;
import com.sunny.backend.user.domain.Users;

public interface FriendCompetitionRepository
	extends JpaRepository<FriendCompetition, Long>, FriendCompetitionCustomRepository {
	void deleteAllByFriend(Friend friend);

	Optional<FriendCompetition> findByFriendAndCompetition(Friend friend, Competition competition);

	Optional<FriendCompetition> findFirstByFriendOrderByCreatedDateDesc(Friend friend);

	List<FriendCompetition> findByFriend_Users(Users users);

	List<FriendCompetition> findByFriend(Friend friend);

	FriendCompetition findFirstByCompetition(Competition competition);

	FriendCompetition findFirstByFriendAndFriendCompetitionStatusOrderByCreatedDateDesc(Friend friend,
		FriendCompetitionStatus friendCompetitionStatus);

	@Query(
		value =
			"select f.id as friendId, f.user_friend_id as userFriend, fc.competition_id as competitionId, u2.nickname, u2.profile, "
				+ "f.friend_status as friendStatus, fc.friend_competition_status as friendCompetitionStatus, "
				+ "fc.competition_output_status as output "
				+ "from friend f "
				+ "join users u on f.user_id = u.user_id "
				+ "join users u2 on f.user_friend_id = u2.user_id "
				+ "left join friend_competition fc ON fc.friend_id = f.id "
				+ "where u.user_id = :userId "
				+ "and f.friend_status = 'FRIEND' "
				+ "and fc.friend_competition_status = 'PROCEEDING'", nativeQuery = true
	)
	List<FriendCompetitionQuery> getFriendCompetitionProceeding(@Param(value = "userId") Long userId);

	@Query(
		value =
			"select f.id as friendId, f.user_friend_id as userFriend, fc.competition_id as competitionId, u2.nickname, u2.profile, f.friend_status as friendStatus, fc.friend_competition_status as friendCompetitionStatus, fc.competition_output_status as competitionOutputStatus "
				+ "from friend f "
				+ "join users u on f.user_id = u.user_id "
				+ "join users u2 on f.user_friend_id = u2.user_id "
				+ "left join friend_competition fc on fc.friend_id = f.id "
				+ "where (f.id, fc.id) in ( "
				+ "select f.id, max(fc.id) "
				+ "from friend f "
				+ "join users u on f.user_id = u.user_id "
				+ "join users u2 on f.user_friend_id = u2.user_id "
				+ "left join friend_competition fc ON fc.friend_id = f.id "
				+ "where u.user_id = :userId and f.friend_status = 'FRIEND' "
				+ "group by f.id)", nativeQuery = true
	)
	List<FriendCompetitionQuery> getFriendCompetitionFriend(@Param(value = "userId") Long userId);

}

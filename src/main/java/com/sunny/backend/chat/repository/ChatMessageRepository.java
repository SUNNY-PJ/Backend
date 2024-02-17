package com.sunny.backend.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunny.backend.chat.domain.ChatMessage;
import com.sunny.backend.chat.dto.response.ChatRoomRes;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageRepositoryCustom {
	@Modifying
	@Query(value = "UPDATE chat_message cm SET cm.read_cnt = 0 where cm.id = :id ", nativeQuery = true)
	void readMessage(@Param(value = "id") Long id);

	@Query(value =
		"select A.chat_room_id as chatRoomId, u.user_id as userFriendId, u.name as friendName, u.profile as friendProfile, A.readCnt, C.message "
			+ "from chat_user cu "
			+ "join users u on cu.friends_id = u.user_id "
			+ "join (select cm.chat_room_id, count(*) as readCnt, max(cm.created_date) as time "
			+ "from chat_user cu "
			+ "join chat_message cm on cu.chat_room_id = cm.chat_room_id "
			+ "where cu.user_id = :id and cm.user_id != :id and cm.read_cnt = 1 "
			+ "group by cm.chat_room_id) A on A.chat_room_id = cu.chat_room_id "
			+ "join ( "
			+ "select * "
			+ "from ( "
			+ "select *, ROW_NUMBER() over (PARTITION by cm.chat_room_id ORDER by cm.created_date desc) as row_num "
			+ "from chat_message cm  "
			+ ") B "
			+ "where row_num = 1 "
			+ ") C on C.chat_room_id = cu.chat_room_id "
			+ "where cu.user_id = :id", nativeQuery = true)
	List<ChatRoomRes> findByChatRoomResponse(@Param(value = "id") Long id);

}

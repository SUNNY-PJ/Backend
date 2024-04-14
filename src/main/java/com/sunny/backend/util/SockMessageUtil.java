package com.sunny.backend.util;

import static com.sunny.backend.common.ComnConstant.*;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.sunny.backend.competition.domain.Competition;
import com.sunny.backend.competition.dto.response.CompetitionResultResponse;
import com.sunny.backend.consumption.dto.response.SaveGoalAlertResponse;
import com.sunny.backend.save.domain.Save;
import com.sunny.backend.user.domain.Users;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SockMessageUtil {
	private final SimpMessagingTemplate template;

	public void sendCompetitionUserWinner(Users users, Users userFriend, Competition competition) {
		sendCompetitionResult(users.getId(), users.getNickname(), userFriend.getNickname(), "님이 대결에서 이겼어요! \n 축하드려요!",
			competition);
		sendCompetitionResult(userFriend.getId(), userFriend.getNickname(), users.getNickname(),
			"님이 대결에서 졌어요! \n 다음에는 조금 더 노력해 보아요!", competition);
	}

	public void sendCompetitionUserFriendWinner(Users users, Users userFriend, Competition competition) {
		sendCompetitionResult(userFriend.getId(), userFriend.getNickname(), users.getNickname(),
			"님이 대결에서 이겼어요! \n 축하드려요!", competition);
		sendCompetitionResult(users.getId(), users.getNickname(), userFriend.getNickname(),
			"님이 대결에서 졌어요! \n 다음에는 조금 더 노력해 보아요!", competition);
	}

	public void sendCompetitionDraw(Users users, Users userFriend, Competition competition) {
		sendCompetitionResult(users.getId(), users.getNickname(), userFriend.getNickname(), "비겼습니다", competition);
		sendCompetitionResult(userFriend.getId(), userFriend.getNickname(), users.getNickname(), "비겼습니다", competition);
	}

	@Async
	public void sendCompetitionResult(Long id, String userName, String friendName, String message,
		Competition competition) {
		template.convertAndSend("/sub/user/" + id,
			CompetitionResultResponse.of(competition, friendName, userName + message));
	}

	@Async
	public void sendWaringSavingGoal(double percentage, Users users, Save save) {
		if (percentage <= 80) {
			String message;
			if (percentage <= 0) {
				message = SAVE_MESSAGE_BELOW_0;
			} else if (percentage <= 20) {
				message = SAVE_MESSAGE_BELOW_20;
			} else if (percentage <= 50) {
				message = SAVE_MESSAGE_BELOW_50;
			} else {
				message = SAVE_MESSAGE_BELOW_80;
			}
			message = String.format("목표금액 %d원까지 %.2f%% 남았어요! %s", save.getCost(), percentage, message);
			template.convertAndSend("/sub/user/" + users.getId(),
				new SaveGoalAlertResponse(save.getCost(), percentage, message));
		}
	}
}

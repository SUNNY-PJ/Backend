package com.sunny.backend.report.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 신고 상태
 * 신고 검토중 <- 접수 or 검토는 같은 의미라고 생각해서 상태 하나 제외해도 될 듯
 * 신고 접수중
 * 신고 거절
 * 신고 승인
 * */
@Getter
@RequiredArgsConstructor
public enum ReportStatus {
	PENDING, //TODO 삭제될 수 있음
	RECEIVING,
	REFUSED,
	APPROVED
}

//package com.sunny.backend.user;
//
//import java.time.LocalDateTime;
//
//import javax.persistence.EntityListeners;
//import javax.persistence.MappedSuperclass;
//
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
//public abstract class BaseTime {
//
//	@CreatedDate
//	private LocalDateTime createdDate; // 생성시간
//
//	@LastModifiedDate
//	private LocalDateTime updatedDate; // 수정시간
//}
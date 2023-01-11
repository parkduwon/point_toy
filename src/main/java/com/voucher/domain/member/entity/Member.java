package com.voucher.domain.member.entity;

import com.voucher.global.common.entity.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends AuditEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long memberId;

	@Column(name = "member_name", length = 100)
	private String memberName;

	@Builder
	public Member(Long memberId, String memberName) {
		this.memberId = memberId;
		this.memberName = memberName;
	}
}

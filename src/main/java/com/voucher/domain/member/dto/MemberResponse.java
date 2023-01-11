package com.voucher.domain.member.dto;

import com.voucher.domain.member.entity.Member;
import lombok.*;

public class MemberResponse {

	@Getter
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	@Builder
	public static class MemberInfo {
		private Long memberId;
		private String memberName;

		public static MemberInfo of(Member member) {
			return MemberInfo.builder()
					.memberId(member.getMemberId())
					.memberName(member.getMemberName())
					.build();
		}
	}
}

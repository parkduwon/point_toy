package com.voucher.domain.point.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.voucher.domain.point.core.enums.PointStatusType;
import com.voucher.domain.point.core.enums.PointTransactionType;
import com.voucher.domain.point.entity.PointLedger;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PointResponse {
	@Getter
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	@Builder
	public static class PointBalanceTotalDto {
		private Long memberId;
		private BigDecimal pointBalanceTotal;

	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Builder
	public static class ListPointLedgersDto {
		private Long pointLedgerId;
		private Long memberId;
		private PointTransactionType pointTransactionType;
		private PointStatusType pointStatusType;
		private BigDecimal pointAmount;
		private BigDecimal remainPointAmount;
		private BigDecimal resultBalanceTotal;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		private LocalDateTime pointExpireDate;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		private LocalDateTime createdDate;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		private LocalDateTime modifiedDate;

		public static ListPointLedgersDto of(PointLedger pointLedger) {
			return ListPointLedgersDto.builder()
					.pointLedgerId(pointLedger.getPointLedgerId())
					.memberId(pointLedger.getMemberId())
					.pointTransactionType(pointLedger.getPointTransactionType())
					.pointStatusType(pointLedger.getPointStatusType())
					.pointAmount(pointLedger.getPointAmount())
					.remainPointAmount(pointLedger.getRemainPointAmount())
					.resultBalanceTotal(pointLedger.getPointWallet().getResultPointBalance())
					.pointExpireDate(pointLedger.getPointExpireDate())
					.createdDate(pointLedger.getCreatedDate())
					.modifiedDate(pointLedger.getModifiedDate())
					.build();
		}
	}
}

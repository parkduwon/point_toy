package com.voucher.domain.point.entity;

import com.voucher.domain.point.core.enums.PointTransactionType;
import com.voucher.global.common.entity.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_wallet")
public class PointWallet extends AuditEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_wallet_id")
	private Long pointWalletId;

	@Column(name = "member_id", nullable = false, updatable = false)
	private Long memberId;

	@Enumerated(EnumType.STRING)
	@Column(name = "point_transaction_type", length = 50, updatable = false)
	private PointTransactionType pointTransactionType;

	//포인트 적립/차감 금액
	@Column(name = "point_amount", nullable = false, updatable = false)
	@PositiveOrZero
	private BigDecimal pointAmount;

	//포인트 잔액
	@Column(name = "result_point_balance", nullable = false, updatable = false)
	@PositiveOrZero
	private BigDecimal resultPointBalance;

	@Builder
	public PointWallet(Long pointWalletId, Long memberId, PointTransactionType pointTransactionType, BigDecimal pointAmount, BigDecimal resultPointBalance) {
		this.pointWalletId = pointWalletId;
		this.memberId = memberId;
		this.pointTransactionType = pointTransactionType;
		this.pointAmount = pointAmount;
		this.resultPointBalance = resultPointBalance;
	}
}

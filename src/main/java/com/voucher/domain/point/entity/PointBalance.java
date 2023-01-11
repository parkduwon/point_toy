package com.voucher.domain.point.entity;

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
@Table(name = "point_balance", indexes = @Index(name = "idx_point_balance_member", columnList = "member_id", unique = true))
public class PointBalance extends AuditEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_balance_id")
	private Long pointBalanceId;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "last_point_ledger_id", nullable = false)
	private Long lastPointLedgerId;

	//포인트 잔액
	@Column(name = "point_balance_total")
	@PositiveOrZero
	private BigDecimal pointBalanceTotal;

	@Builder
	public PointBalance(Long pointBalanceId, Long memberId, BigDecimal pointBalanceTotal, Long lastPointLedgerId) {
		this.pointBalanceId = pointBalanceId;
		this.memberId = memberId;
		this.pointBalanceTotal = pointBalanceTotal;
		this.lastPointLedgerId = lastPointLedgerId;
	}

	public void setPointBalanceTotal(BigDecimal pointBalanceTotal, Long lastPointLedgerId) {
		this.pointBalanceTotal = pointBalanceTotal;
		this.lastPointLedgerId = lastPointLedgerId;
	}
}

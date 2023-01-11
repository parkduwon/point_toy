package com.voucher.domain.point.entity;

import com.voucher.domain.point.core.enums.PointStatusType;
import com.voucher.domain.point.core.enums.PointTransactionType;
import com.voucher.global.common.entity.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_ledger", indexes = {
		@Index(name = "idx_point_ledger", columnList = "member_id, point_ledger_id, point_status_type", unique = true)})
public class PointLedger extends AuditEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_ledger_id")
	private Long pointLedgerId;

	@Column(name = "member_id")
	private Long memberId;

	@Enumerated(EnumType.STRING)
	@Column(name = "point_status_type", length = 50)
	private PointStatusType pointStatusType;

	@Enumerated(EnumType.STRING)
	@Column(name = "point_transaction_type", length = 50)
	private PointTransactionType pointTransactionType;

	//최초 적립/차감 포인트
	@Column(name = "point_amount")
	@Positive
	private BigDecimal pointAmount;

	//남은 적립/차감 포인트
	@Column(name = "remain_point_amount")
	@PositiveOrZero
	private BigDecimal remainPointAmount;

	@Column(name = "point_expire_date")
	private LocalDateTime pointExpireDate;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "point_wallet_id", updatable = false, foreignKey = @ForeignKey(name = "fk_point_ledger_point_wallet"))
	private PointWallet pointWallet;

	@OneToMany(mappedBy = "pointLedger")
	private final List<PointDetail> pointDetails = new ArrayList<>();

	@Builder
	public PointLedger(Long pointLedgerId, Long memberId, PointStatusType pointStatusType, PointTransactionType pointTransactionType, BigDecimal pointAmount, BigDecimal remainPointAmount, LocalDateTime pointExpireDate, PointWallet pointWallet) {
		this.pointLedgerId = pointLedgerId;
		this.memberId = memberId;
		this.pointStatusType = pointStatusType;
		this.pointTransactionType = pointTransactionType;
		this.pointAmount = pointAmount;
		this.remainPointAmount = remainPointAmount;
		this.pointExpireDate = pointExpireDate;
		this.pointWallet = pointWallet;
	}

	public void updatePointLedger(PointStatusType pointStatusType, BigDecimal remainPointAmount) {
		this.pointStatusType = pointStatusType;
		this.remainPointAmount = remainPointAmount;
	}

	public void addPointDetail(PointDetail pointDetail) {
		this.getPointDetails().add(pointDetail);
		pointDetail.setPointLedger(this);
	}
}

package com.voucher.domain.point.entity;

import com.voucher.global.common.entity.AuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_detail")
public class PointDetail extends AuditEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_detail_id")
	private Long pointDetailId;

	//적립/차감 거래의 원장 id
	@ManyToOne
	@JoinColumn(name = "point_ledger_id", foreignKey = @ForeignKey(name = "fk_point_detail_point_ledger"))
	private PointLedger pointLedger;

	//거래에 의해 적립/차감 대상인 포인트의 원장 id
	@Column(name = "source_ledger_id")
	private Long sourceLedgerId;

	//적립/차감 전 포인트
	@Column(name = "origin_point_amount")
	private BigDecimal originPointAmount;

	//적립/차감 후 포인트
	@Column(name = "result_point_amount")
	private BigDecimal resultPointAmount;

	@Builder
	public PointDetail(Long pointDetailId, PointLedger pointLedger, Long sourceLedgerId, BigDecimal originPointAmount, BigDecimal resultPointAmount) {
		this.pointDetailId = pointDetailId;
		this.pointLedger = pointLedger;
		this.sourceLedgerId = sourceLedgerId;
		this.originPointAmount = originPointAmount;
		this.resultPointAmount = resultPointAmount;
	}

	public void setPointLedger(PointLedger pointLedger) {
		this.pointLedger = pointLedger;
	}
}

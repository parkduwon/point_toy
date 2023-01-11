package com.voucher.domain.point.helper;

import com.voucher.domain.point.core.enums.PointStatusType;
import com.voucher.domain.point.core.enums.PointTransactionType;
import com.voucher.domain.point.dto.request.PointRequest;
import com.voucher.domain.point.entity.PointBalance;
import com.voucher.domain.point.entity.PointDetail;
import com.voucher.domain.point.entity.PointLedger;
import com.voucher.domain.point.entity.PointWallet;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@TestComponent
public class PointDataHelper {

	public static PointRequest.PointTransactionDto createPointRequest(PointTransactionType pointTransactionType, BigDecimal pointAmount) {
		return new PointRequest.PointTransactionDto(pointTransactionType, pointAmount);
	}

	public static PointWallet createPointWallet(Long memberId, PointRequest.PointTransactionDto request, BigDecimal pointBalanceTotal) {
		return PointWallet.builder()
				.memberId(memberId)
				.pointTransactionType(request.pointTransactionType())
				.pointAmount(request.pointAmount())
				.resultPointBalance(pointBalanceTotal)
				.build();
	}

	public static PointLedger createPointLedger(Long pointLedgerId, PointWallet pointWallet) {
		return PointLedger.builder()
				.pointLedgerId(pointLedgerId)
				.pointTransactionType(pointWallet.getPointTransactionType())
				.pointStatusType(pointWallet.getPointTransactionType().equals(PointTransactionType.EARN_POINT) ? PointStatusType.AVAILABLE : PointStatusType.REDEEMED)
				.pointAmount(pointWallet.getPointAmount())
				.pointExpireDate(LocalDateTime.now().plusYears(1))
				.memberId(pointWallet.getMemberId())
				.pointWallet(pointWallet)
				.build();
	}

	public static Optional<PointBalance> createPointBalance(Long memberId, BigDecimal pointBalance) {
		return Optional.ofNullable(PointBalance.builder().memberId(memberId).lastPointLedgerId(1L).pointBalanceTotal(pointBalance).build());
	}

	public static List<PointLedger> createPointLedgerEarn(int bound) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		long pointLedgerId = 1L;
		List<PointLedger> pointLedgers = new ArrayList<>();
		while (bound > 0) {
			int pointAmount = random.nextInt(bound > 500 ? bound / 4 : bound);
			if (bound < 10) {
				pointAmount = bound;
				bound = 0;
			} else {
				bound = bound - pointAmount;
			}
			pointLedgers.add(PointLedger.builder()
					.memberId(1L)
					.pointLedgerId(pointLedgerId)
					.pointAmount(BigDecimal.valueOf(pointAmount))
					.remainPointAmount(BigDecimal.valueOf(pointAmount))
					.pointStatusType(PointStatusType.AVAILABLE)
					.pointTransactionType(PointTransactionType.EARN_POINT)
					.pointExpireDate(LocalDateTime.now().plusYears(1))
					.build());
			pointLedgerId = pointLedgerId + 1L;

		}
		return pointLedgers;
	}

	public static List<PointDetail> createPointLedgerCancelTargets() {
		long pointLedgerId = 10L;
		List<PointDetail> pointDetails = new ArrayList<>();
		int roof = 0;
		for (int i = 1; i < 5; i++) {
			BigDecimal pointAmount = BigDecimal.valueOf(roof * 100L);
			BigDecimal remainPointAmount = roof == 5 ? BigDecimal.valueOf(roof * 34L) : BigDecimal.ZERO;
			PointStatusType pointStatusType = roof == 5 ? PointStatusType.AVAILABLE : PointStatusType.USED;
			PointLedger sourcePointLedger = PointLedger.builder()
					.memberId(1L)
					.pointLedgerId(pointLedgerId)
					.pointAmount(BigDecimal.valueOf(roof * 100L))
					.remainPointAmount(remainPointAmount)
					.pointStatusType(pointStatusType)
					.pointTransactionType(PointTransactionType.EARN_POINT)
					.pointExpireDate(LocalDateTime.now().plusYears(1))
					.build();
			pointDetails.add(PointDetail.builder()
					.pointDetailId((long) i)
					.pointLedger(sourcePointLedger)
					.originPointAmount(pointAmount)
					.resultPointAmount(remainPointAmount)
					.sourceLedgerId(sourcePointLedger.getPointLedgerId())
					.build());
			pointLedgerId = pointLedgerId + 1L;
			roof++;
		}
		return pointDetails;
	}
}

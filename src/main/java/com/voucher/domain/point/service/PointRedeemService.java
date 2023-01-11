package com.voucher.domain.point.service;

import com.voucher.domain.member.repository.MemberRepository;
import com.voucher.domain.point.core.enums.PointStatusType;
import com.voucher.domain.point.entity.PointBalance;
import com.voucher.domain.point.entity.PointDetail;
import com.voucher.domain.point.entity.PointLedger;
import com.voucher.domain.point.entity.PointWallet;
import com.voucher.domain.point.repository.PointBalanceRepository;
import com.voucher.domain.point.repository.PointDetailRepository;
import com.voucher.domain.point.repository.PointLedgerRepository;
import com.voucher.domain.point.repository.PointWalletRepository;
import com.voucher.domain.point.service.intfc.PointTransactionFactory;
import com.voucher.global.common.error.ErrorCode;
import com.voucher.global.common.error.exeption.VoucherPointException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service(value = "REDEEM_POINT")
@Scope("prototype")
public class PointRedeemService extends PointTransactionFactory {

	private PointBalance pointBalance = null;

	protected PointRedeemService(MemberRepository memberRepository, PointLedgerRepository pointLedgerRepository, PointDetailRepository pointDetailRepository, PointBalanceRepository pointBalanceRepository, PointWalletRepository pointWalletRepository) {
		super(memberRepository, pointLedgerRepository, pointDetailRepository, pointBalanceRepository, pointWalletRepository);
	}

	@Override
	protected void validationCore() {
		pointBalanceRepository.pointBalanceForUpdate(pointTransactionFactor.getMemberId()).ifPresentOrElse(
				balance -> this.pointBalance = balance,
				() -> this.pointBalance = PointBalance.builder()
						.memberId(pointTransactionFactor.getMemberId())
						.pointBalanceTotal(BigDecimal.ZERO)
						.build()
		);

		BigDecimal pointBalanceAfterCalculate = pointTransactionFactor.getPointTransactionType().calculateBalance(pointBalance.getPointBalanceTotal(), pointTransactionFactor.getPointAmount());
		if (pointBalanceAfterCalculate.signum() < 0) {
			throw new VoucherPointException(String.format("포인트 잔액이 부족합니다. 부족한 포인트 : %s", pointBalanceAfterCalculate.multiply(BigDecimal.valueOf(-1L))), ErrorCode.INVALID_INPUT_VALUE);
		}

		pointTransactionFactor.setResultPointBalance(pointBalanceAfterCalculate);
	}

	@Override
	protected void transactionCore() {
		List<PointLedger> sourcePointLedgers = pointLedgerRepository.findEarnLedgerByMemberId(pointTransactionFactor.getMemberId());

		PointWallet pointWallet = PointWallet.builder()
				.pointTransactionType(pointTransactionFactor.getPointTransactionType())
				.memberId(pointTransactionFactor.getMemberId())
				.pointAmount(pointTransactionFactor.getPointAmount())
				.resultPointBalance(pointTransactionFactor.getResultPointBalance())
				.build();
		PointLedger pointLedger = pointLedgerRepository.save(PointLedger.builder()
				.memberId(pointBalance.getMemberId())
				.pointStatusType(PointStatusType.REDEEMED)
				.pointTransactionType(pointTransactionFactor.getPointTransactionType())
				.pointAmount(pointTransactionFactor.getPointAmount())
				.remainPointAmount(BigDecimal.ZERO)
				.pointWallet(pointWallet)
				.build());

		BigDecimal pointRedeemAmount = pointTransactionFactor.getPointAmount();

		for (PointLedger sourcePointLedger : sourcePointLedgers) {
			BigDecimal resultPointAmount = BigDecimal.ZERO;

			if (pointRedeemAmount.compareTo(sourcePointLedger.getPointAmount()) >= 0) {
				pointRedeemAmount = pointRedeemAmount.subtract(sourcePointLedger.getPointAmount());
				sourcePointLedger.updatePointLedger(PointStatusType.USED, resultPointAmount);
			} else {
				resultPointAmount = sourcePointLedger.getPointAmount().subtract(pointRedeemAmount);
				sourcePointLedger.updatePointLedger(PointStatusType.AVAILABLE, resultPointAmount);
				pointRedeemAmount = BigDecimal.ZERO;
			}

			pointDetailRepository.save(PointDetail.builder()
					.pointLedger(pointLedger)
					.sourceLedgerId(sourcePointLedger.getPointLedgerId())
					.originPointAmount(sourcePointLedger.getPointAmount())
					.resultPointAmount(resultPointAmount)
					.build());

			if (pointRedeemAmount.signum() == 0) {
				break;
			}

		}
		this.pointBalance.setPointBalanceTotal(pointTransactionFactor.getResultPointBalance(), pointLedger.getPointLedgerId());
		pointBalanceRepository.save(pointBalance);
	}

}

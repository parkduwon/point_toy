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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service(value = "EARN_POINT")
@Scope("prototype")
public class PointEarnService extends PointTransactionFactory {

	private PointBalance pointBalance = null;

	protected PointEarnService(MemberRepository memberRepository, PointLedgerRepository pointLedgerRepository, PointDetailRepository pointDetailRepository, PointBalanceRepository pointBalanceRepository, PointWalletRepository pointWalletRepository) {
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
	}

	@Override
	protected void transactionCore() {
		BigDecimal pointBalanceAfterCalculate = pointTransactionFactor.getPointTransactionType().calculateBalance(pointBalance.getPointBalanceTotal(), pointTransactionFactor.getPointAmount());
		pointTransactionFactor.setResultPointBalance(pointBalanceAfterCalculate);

		PointWallet pointWallet = PointWallet.builder()
				.pointTransactionType(pointTransactionFactor.getPointTransactionType())
				.memberId(pointTransactionFactor.getMemberId())
				.pointAmount(pointTransactionFactor.getPointAmount())
				.resultPointBalance(pointTransactionFactor.getResultPointBalance())
				.build();
		PointLedger pointLedger = pointLedgerRepository.save(PointLedger.builder()
				.pointAmount(pointTransactionFactor.getPointAmount())
				.remainPointAmount(pointTransactionFactor.getPointAmount())
				.memberId(pointTransactionFactor.getMemberId())
				.pointTransactionType(pointTransactionFactor.getPointTransactionType())
				.pointExpireDate(LocalDateTime.now().plusYears(1))
				.pointStatusType(PointStatusType.AVAILABLE)
				.pointWallet(pointWallet)
				.build());

		this.pointBalance.setPointBalanceTotal(pointTransactionFactor.getResultPointBalance(), pointLedger.getPointLedgerId());
		pointBalanceRepository.save(pointBalance);


		pointDetailRepository.save(PointDetail.builder()
				.pointLedger(pointLedger)
				.originPointAmount(pointTransactionFactor.getPointAmount())
				.resultPointAmount(pointTransactionFactor.getPointAmount())
				.sourceLedgerId(pointLedger.getPointLedgerId())
				.build());
	}
}

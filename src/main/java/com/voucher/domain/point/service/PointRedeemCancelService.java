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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service(value = "CANCEL_REDEEM_POINT")
@Scope("prototype")
public class PointRedeemCancelService extends PointTransactionFactory {

	private PointBalance pointBalance = null;
	private PointLedger sourcePointLedger = null;
	private final List<PointLedger> targetPointLedgers = new ArrayList<>();

	protected PointRedeemCancelService(MemberRepository memberRepository, PointLedgerRepository pointLedgerRepository, PointDetailRepository pointDetailRepository, PointBalanceRepository pointBalanceRepository, PointWalletRepository pointWalletRepository) {
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

		sourcePointLedger = pointLedgerRepository.getTargetLedgerForRedeemCancel(pointTransactionFactor.getMemberId(), pointTransactionFactor.getPointLedgerId())
				.orElseThrow(() -> new VoucherPointException("요청한 포인트 거래가 없습니다. 포인트 거래 id : " + pointTransactionFactor.getPointLedgerId(), ErrorCode.NO_SUCH_ELEMENT_EXCEPTION));

		if (!PointStatusType.REDEEMED.equals(sourcePointLedger.getPointStatusType())) {
			throw new VoucherPointException("사용취소 할 수 없는 포인트 거래입니다. 포인트 상태 : " + sourcePointLedger.getPointStatusType(), ErrorCode.INVALID_INPUT_VALUE);
		}

		List<Long> targetPointLedgerIds = sourcePointLedger.getPointDetails().stream()
				.map(PointDetail::getSourceLedgerId)
				.toList();
		targetPointLedgers.addAll(pointLedgerRepository.findAllById(targetPointLedgerIds));
		if (targetPointLedgers.stream().anyMatch(f -> LocalDateTime.now().isAfter(f.getPointExpireDate()))) {
			throw new VoucherPointException("사용된 포인트 중 유효기간이 지난 포인트가 있습니다.", ErrorCode.INVALID_INPUT_VALUE);
		}

		BigDecimal pointBalanceAfterCalculate = pointTransactionFactor.getPointTransactionType().calculateBalance(pointBalance.getPointBalanceTotal(), sourcePointLedger.getPointAmount());
		pointTransactionFactor.setResultPointBalance(pointBalanceAfterCalculate);
	}

	@Override
	protected void transactionCore() {
		PointWallet pointWallet = PointWallet.builder()
				.pointTransactionType(pointTransactionFactor.getPointTransactionType())
				.memberId(pointTransactionFactor.getMemberId())
				.pointAmount(sourcePointLedger.getPointAmount())
				.resultPointBalance(pointTransactionFactor.getResultPointBalance())
				.build();
		PointLedger pointLedger = pointLedgerRepository.save(PointLedger.builder()
				.pointAmount(sourcePointLedger.getPointAmount())
				.memberId(pointBalance.getMemberId())
				.pointStatusType(PointStatusType.CANCEL_REDEEM_SUCCESS)
				.pointTransactionType(pointTransactionFactor.getPointTransactionType())
				.pointExpireDate(LocalDateTime.now())
				.remainPointAmount(BigDecimal.ZERO)
				.pointWallet(pointWallet)
				.build());

		sourcePointLedger.updatePointLedger(PointStatusType.REDEEM_CANCELED, BigDecimal.ZERO);
		for (PointLedger targetPointLedger : targetPointLedgers) {
			targetPointLedger.updatePointLedger(PointStatusType.AVAILABLE, targetPointLedger.getPointAmount());
			pointDetailRepository.save(PointDetail.builder()
					.pointLedger(pointLedger)
					.sourceLedgerId(sourcePointLedger.getPointLedgerId())
					.originPointAmount(targetPointLedger.getRemainPointAmount())
					.resultPointAmount(targetPointLedger.getPointAmount())
					.build());
		}

		this.pointBalance.setPointBalanceTotal(pointTransactionFactor.getResultPointBalance(), pointLedger.getPointLedgerId());
		pointBalanceRepository.save(pointBalance);
	}

}

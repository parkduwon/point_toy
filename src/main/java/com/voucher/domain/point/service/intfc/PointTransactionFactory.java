package com.voucher.domain.point.service.intfc;

import com.voucher.domain.member.repository.MemberRepository;
import com.voucher.domain.point.repository.PointBalanceRepository;
import com.voucher.domain.point.repository.PointDetailRepository;
import com.voucher.domain.point.repository.PointLedgerRepository;
import com.voucher.domain.point.repository.PointWalletRepository;
import com.voucher.global.common.error.ErrorCode;
import com.voucher.global.common.error.exeption.VoucherPointException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class PointTransactionFactory {

	protected PointTransactionFactor pointTransactionFactor;
	protected final MemberRepository memberRepository;
	protected final PointLedgerRepository pointLedgerRepository;
	protected final PointDetailRepository pointDetailRepository;
	protected final PointBalanceRepository pointBalanceRepository;
	protected final PointWalletRepository pointWalletRepository;

	public void setPointTransactionFactor(PointTransactionFactor pointTransactionFactor) {
		this.pointTransactionFactor = pointTransactionFactor;
		this.pointTransactionFactor.setPointTransaction(this);
	}

	@Transactional
	public PointTransactionFactor doTransaction() {
		commonValidation();
		validationCore();
		transactionCore();
		return pointTransactionFactor;
	}

	private void commonValidation() {
		memberRepository.findById(pointTransactionFactor.getMemberId()).orElseThrow(() -> new VoucherPointException("등록되지 않은 멤버 입니다.", ErrorCode.INVALID_INPUT_VALUE));
	}

	protected abstract void validationCore();

	protected abstract void transactionCore();
}

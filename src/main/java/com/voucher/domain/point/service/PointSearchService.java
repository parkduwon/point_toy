package com.voucher.domain.point.service;

import com.voucher.domain.member.repository.MemberRepository;
import com.voucher.domain.point.dto.request.PointRequest;
import com.voucher.domain.point.dto.response.PointResponse;
import com.voucher.domain.point.entity.PointBalance;
import com.voucher.domain.point.repository.PointBalanceRepository;
import com.voucher.domain.point.repository.PointLedgerRepository;
import com.voucher.global.common.dto.CommonPageRequest;
import com.voucher.global.common.error.ErrorCode;
import com.voucher.global.common.error.exeption.VoucherPointException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PointSearchService {
	private final PointBalanceRepository pointBalanceRepository;
	private final PointLedgerRepository pointLedgerRepository;
	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public PointResponse.PointBalanceTotalDto getMemberPointBalanceTotal(Long memberId) {
		memberRepository.findById(memberId).orElseThrow(() -> new VoucherPointException("없는 회원 입니다.", ErrorCode.NO_SUCH_ELEMENT_EXCEPTION));
		PointBalance pointBalance = pointBalanceRepository.getPointBalanceByMemberId(memberId);
		return PointResponse.PointBalanceTotalDto.builder()
				.memberId(memberId)
				.pointBalanceTotal(pointBalance == null ? BigDecimal.ZERO : pointBalance.getPointBalanceTotal())
				.build();
	}

	@Transactional(readOnly = true)
	public Page<PointResponse.ListPointLedgersDto> findPointLedgersList(Long memberId, PointRequest.ListPointLedgerSearchDto request) {
		Pageable pageable = new CommonPageRequest(request.page(), request.size()).of();
		return pointLedgerRepository.findPointLedgersList(memberId, request, pageable);
	}
}

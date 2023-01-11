package com.voucher.domain.point.repository.custom;

import com.voucher.domain.point.dto.request.PointRequest;
import com.voucher.domain.point.dto.response.PointResponse;
import com.voucher.domain.point.entity.PointLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PointLedgerRepositoryCustom {
	List<PointLedger> findEarnLedgerByMemberId(Long memberId);
	Optional<PointLedger> getTargetLedgerForRedeemCancel(Long memberId, Long pointLedgerId);
	Page<PointResponse.ListPointLedgersDto> findPointLedgersList(Long memberId, PointRequest.ListPointLedgerSearchDto request, Pageable pageable);
}

package com.voucher.domain.point.repository.custom;

import com.voucher.domain.point.entity.PointBalance;

import java.util.Optional;

public interface PointBalanceRepositoryCustom {
	Optional<PointBalance> pointBalanceForUpdate(Long memberId);
}

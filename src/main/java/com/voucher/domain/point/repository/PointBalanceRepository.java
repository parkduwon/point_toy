package com.voucher.domain.point.repository;

import com.voucher.domain.point.entity.PointBalance;
import com.voucher.domain.point.repository.custom.PointBalanceRepositoryCustom;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointBalanceRepository extends JpaRepository<PointBalance, Long>, PointBalanceRepositoryCustom {

	@Cacheable(key = "#memberId", cacheNames = "pointBalance")
	PointBalance getPointBalanceByMemberId(Long memberId);
}

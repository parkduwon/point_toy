package com.voucher.domain.point.repository.implement;

import com.voucher.domain.point.entity.PointBalance;
import com.voucher.domain.point.repository.custom.PointBalanceRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.voucher.domain.point.entity.QPointBalance.pointBalance;

@RequiredArgsConstructor
public class PointBalanceRepositoryImpl implements PointBalanceRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<PointBalance> pointBalanceForUpdate(Long memberId) {
		return Optional.ofNullable(queryFactory
						.selectFrom(pointBalance)
						.where(pointBalance.memberId.eq(memberId))
						.setLockMode(LockModeType.PESSIMISTIC_WRITE)
						.setHint("jakarta.persistence.lock.timeout", "2000")
						.fetchOne());
	}
}

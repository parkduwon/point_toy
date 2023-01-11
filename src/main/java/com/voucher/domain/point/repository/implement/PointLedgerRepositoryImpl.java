package com.voucher.domain.point.repository.implement;

import com.voucher.domain.point.core.enums.PointStatusType;
import com.voucher.domain.point.dto.request.PointRequest;
import com.voucher.domain.point.dto.response.PointResponse;
import com.voucher.domain.point.entity.PointLedger;
import com.voucher.domain.point.repository.custom.PointLedgerRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.voucher.domain.point.entity.QPointLedger.pointLedger;
import static com.voucher.domain.point.entity.QPointWallet.pointWallet;

@RequiredArgsConstructor
public class PointLedgerRepositoryImpl implements PointLedgerRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<PointLedger> findEarnLedgerByMemberId(Long memberId) {
		return queryFactory.selectFrom(pointLedger)
				.where(pointLedger.memberId.eq(memberId),
						pointLedger.pointStatusType.eq(PointStatusType.AVAILABLE),
						pointLedger.pointExpireDate.after(LocalDateTime.now())
				)
				.fetch();
	}

	@Override
	public Optional<PointLedger> getTargetLedgerForRedeemCancel(Long memberId, Long pointLedgerId) {
		return Optional.ofNullable(queryFactory.selectFrom(pointLedger)
				.where(pointLedger.memberId.eq(memberId),
						pointLedger.pointLedgerId.eq(pointLedgerId))
				.fetchOne());
	}

	@Override
	@Cacheable(key = "#memberId", cacheNames = "pointLedger")
	public Page<PointResponse.ListPointLedgersDto> findPointLedgersList(Long memberId, PointRequest.ListPointLedgerSearchDto request, Pageable pageable) {
		List<PointResponse.ListPointLedgersDto> listPointEarnRedeemsDto = queryFactory.select(Projections.fields(PointResponse.ListPointLedgersDto.class,
						pointLedger.pointLedgerId,
						pointLedger.memberId,
						pointLedger.pointTransactionType,
						pointLedger.pointStatusType,
						pointLedger.pointAmount,
						pointLedger.remainPointAmount,
						pointWallet.resultPointBalance.as("resultBalanceTotal"),
						pointLedger.pointExpireDate,
						pointLedger.createdDate,
						pointLedger.modifiedDate
				)).from(pointLedger)
				.innerJoin(pointLedger.pointWallet, pointWallet)
				.where(pointLedger.memberId.eq(memberId))
				.orderBy(request.direction().equals(Sort.Direction.DESC) ? pointLedger.createdDate.desc() : pointLedger.createdDate.asc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPQLQuery<PointLedger> count = queryFactory
				.selectFrom(pointLedger)
				.innerJoin(pointLedger.pointWallet, pointWallet)
				.where(pointLedger.memberId.eq(memberId));

		return PageableExecutionUtils.getPage(listPointEarnRedeemsDto, pageable, count::fetchCount);
	}
}

package com.voucher.domain.point.service;

import com.voucher.domain.point.service.intfc.PointTransactionFactory;
import com.voucher.domain.point.service.intfc.PointTransactionFactor;
import com.voucher.domain.point.service.intfc.PointTransactionHandlerLocator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointTransactionService {
	private final PointTransactionHandlerLocator pointTransactionHandlerLocator;

	@Caching(evict = {
			@CacheEvict(key = "#factor.memberId", value = "pointBalance"),
			@CacheEvict(value = "pointLedger", allEntries = true)
	})
	public void prepare(PointTransactionFactor factor) {
		PointTransactionFactory pointTransactionFactory = pointTransactionHandlerLocator.getPointTransactionHandler(factor.getPointTransactionType());
		pointTransactionFactory.setPointTransactionFactor(factor);
		pointTransactionFactory.doTransaction();
	}
}

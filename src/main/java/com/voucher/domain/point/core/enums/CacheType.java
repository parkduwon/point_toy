package com.voucher.domain.point.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CacheType {
	POINT_LEDGER("pointLedger", 5 * 60, 10000),
	POINT_BALANCE("pointBalance", 5 * 60, 10000);

	private final String cacheName;     // 캐시 이름
	private final int expireAfterWrite; // 만료시간
	private final int maximumSize;      // 최대 갯수
}
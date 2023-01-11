package com.voucher.domain.point.core.enums;

import com.voucher.global.common.utils.EnumMapperType;

import java.math.BigDecimal;

public enum PointTransactionType implements EnumMapperType {
	EARN_POINT("포인트 적립"),
	REDEEM_POINT("포인트 사용"),
	CANCEL_REDEEM_POINT("포인트 사용 취소");
	private final String value;

	PointTransactionType(String value) {
		this.value = value;
	}

	@Override
	public String getCode() {
		return this.name();
	}

	@Override
	public String getValue() {
		return this.value;
	}

	public BigDecimal calculateBalance(BigDecimal pointBalanceTotal, BigDecimal amount) {
		return switch (this) {
			case EARN_POINT, CANCEL_REDEEM_POINT -> pointBalanceTotal.add(amount);
			case REDEEM_POINT -> pointBalanceTotal.subtract(amount);
		};
	}
}

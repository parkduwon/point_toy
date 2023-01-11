package com.voucher.domain.point.core.enums;


import com.voucher.global.common.utils.EnumMapperType;

public enum PointStatusType implements EnumMapperType {
	AVAILABLE("사용가능"),
	REDEEMED("포인트 차감됨"),
	USED("사용완료"),
	REDEEM_CANCELED("포인트 적립 취소됨"),
	CANCEL_REDEEM_SUCCESS("포인트 적립 취소 성공");
	private final String value;
	PointStatusType(String value) {
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

}

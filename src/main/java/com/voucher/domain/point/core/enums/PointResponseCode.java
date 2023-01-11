package com.voucher.domain.point.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PointResponseCode {

	POINT_EARN_SUCCESS(201, "POINT_S_001", "포인트 적립이 성공하였습니다."),
	POINT_REDEEM_SUCCESS(201, "POINT_S_002", "포인트 사용이 성공하였습니다."),
	POINT_REDEEM_CANCEL_SUCCESS(201, "POINT_S_003", "포인트 사용 취소가 성공하였습니다."),
	GET_POINT_BALANCE_SUCCESS(200, "POINT_S_004", "포인트 잔액 조회가 성공하였습니다."),
	LIST_POINT_EARN_REDEEM_SUCCESS(200, "POINT_S_005", "포인트 적립/사용 내역 조회가 성공하였습니다."),
    ;

    private final String code;
    private final String message;
    private final int status;

    PointResponseCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}

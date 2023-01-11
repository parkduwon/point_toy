package com.voucher.global.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
	//공통오류
	INVALID_INPUT_VALUE(400, "COMMON_E_001", "잘못된 입력 값 입니다."),
	MISSING_REQUEST_VALUE(400, "COMMON_E_002", "필수 입력 값이 빠졌습니다."),
	METHOD_NOT_ALLOWED(405, "COMMON_E_003", "허용되지 않은 요청 방식 입니다."),
	BAD_REQUEST(400, "COMMON_E_004", "잘못된 요청 입니다."),
	INTERNAL_SERVER_ERROR(500, "COMMON_E_005", "서버에서 발생한 오류입니다. 운영자에게 신고부탁드립니다."),
	DATA_INTEGRITY_VIOLATION_EXCEPTION(400, "COMMON_E_006", "데이터 값이 잘못되어 DB에 입력할 수 없습니다."),
	RESOURCE_ACCESS_ERROR(500, "COMMON_E_007", "서버 통신에 장애가 생겼습니다."),
	HANDLE_NOT_FOUND_EXCEPTION(404, "COMMON_E_008", "없는 api 주소 입니다. 고객지원센터에 문의 주세요."),
	SERVLET_ERROR(500, "COMMON_E_009", "시스템 오류입니다. 고객지원센터에 문의 주세요."),
	UNSUPPORTED_MEDIA_TYPE(415, "COMMON_E_010", "허용되지 않은 Content-Type 방식 입니다."),
	INVALID_FILE_STORAGE_EXCEPTION(400, "COMMON_E_011", "잘못된 요청헤더, 용량, 파일형식 입니다."),
	NO_SUCH_ELEMENT_EXCEPTION(400, "COMMON_E_012", "값을 찾을 수 없습니다.");

	private final String code;
	private final String message;
	private final int status;

	ErrorCode(final int status, final String code, final String message) {
		this.status = status;
		this.message = message;
		this.code = code;
	}
}

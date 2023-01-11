package com.voucher.global.common.error.exeption;


import com.voucher.global.common.error.ErrorCode;

import java.io.Serial;

public class VoucherPointException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = -2899447744777394044L;

	private ErrorCode errorCode;

    public VoucherPointException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public VoucherPointException(String message) {
        super(message);
    }

    public VoucherPointException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

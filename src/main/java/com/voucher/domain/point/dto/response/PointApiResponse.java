package com.voucher.domain.point.dto.response;

import com.voucher.domain.point.core.enums.PointResponseCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PointApiResponse {

    private final Object result;

    private final String code;

    private final int status;

    private final String resultMsg;

    public PointApiResponse(PointResponseCode code) {
        this.result = null;
        this.code = code.getCode();
        this.status = code.getStatus();
        this.resultMsg = code.getMessage();
    }

    public PointApiResponse(Object result, PointResponseCode code) {
        this.result = result;
		this.code = code.getCode();
		this.status = code.getStatus();
		this.resultMsg = code.getMessage();

    }
}

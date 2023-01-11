package com.voucher.domain.point.dto.request;

import com.voucher.domain.point.core.enums.PointTransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;

public class PointRequest {
	public record PointTransactionDto(
			@NotNull(message = "포인트의 거래 유형은 필수 값 입니다.")
			PointTransactionType pointTransactionType,
			@Positive(message = "0이상의 값을 넣어주세요.")
			BigDecimal pointAmount
	) {
	}

	public record ListPointLedgerSearchDto(
			@NotNull(message = "목록 크기는 필수 값 입니다.")
			@Min(value = 1)
			Integer size,
			@NotNull(message = "페이지는 필수 값 입니다.")
			@Min(value = 1)
			Integer page,
			@NotNull(message = "정렬순서는 필수 값 입니다.")
			Sort.Direction direction
	) {
	}
}

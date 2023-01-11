package com.voucher.domain.point.controller;

import com.voucher.domain.point.core.constants.ApiPath;
import com.voucher.domain.point.core.enums.PointResponseCode;
import com.voucher.domain.point.core.enums.PointTransactionType;
import com.voucher.domain.point.dto.request.PointRequest;
import com.voucher.domain.point.dto.response.PointApiResponse;
import com.voucher.domain.point.service.PointTransactionService;
import com.voucher.domain.point.service.intfc.PointTransactionFactor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PointTransactionController {

	private final PointTransactionService pointTransactionService;

	//포인트 적립/사용
	@PostMapping(path = ApiPath.POINT.TRANSACTION)
	public PointApiResponse transactionPoint(@PathVariable Long memberId, @RequestBody @Valid PointRequest.PointTransactionDto request) {
		PointTransactionFactor factor = PointTransactionFactor.builder()
				.memberId(memberId)
				.pointTransactionType(request.pointTransactionType())
				.pointAmount(request.pointAmount())
				.build();
		pointTransactionService.prepare(factor);
		return new PointApiResponse(getPointTransactionResponseCode(request.pointTransactionType()));
	}

	//포인트 사용취소
	@DeleteMapping(path = ApiPath.POINT.TRANSACTION_CANCEL)
	public PointApiResponse transactionPoint(@PathVariable Long memberId, @PathVariable Long pointLedgerId) {
		PointTransactionFactor factor = PointTransactionFactor.builder()
				.memberId(memberId)
				.pointTransactionType(PointTransactionType.CANCEL_REDEEM_POINT)
				.pointLedgerId(pointLedgerId)
				.build();
		pointTransactionService.prepare(factor);
		return new PointApiResponse(getPointTransactionResponseCode(PointTransactionType.CANCEL_REDEEM_POINT));
	}

	private PointResponseCode getPointTransactionResponseCode(PointTransactionType pointTransactionType) {
		return switch (pointTransactionType) {
			case EARN_POINT -> PointResponseCode.POINT_EARN_SUCCESS;
			case REDEEM_POINT -> PointResponseCode.POINT_REDEEM_SUCCESS;
			case CANCEL_REDEEM_POINT -> PointResponseCode.POINT_REDEEM_CANCEL_SUCCESS;
		};
	}
}

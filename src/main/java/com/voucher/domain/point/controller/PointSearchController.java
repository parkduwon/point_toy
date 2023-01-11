package com.voucher.domain.point.controller;

import com.voucher.domain.point.core.constants.ApiPath;
import com.voucher.domain.point.core.enums.PointResponseCode;
import com.voucher.domain.point.dto.request.PointRequest;
import com.voucher.domain.point.dto.response.PointApiResponse;
import com.voucher.domain.point.service.PointSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PointSearchController {
	private final PointSearchService pointSearchService;

	//포인트 잔액조회
	@GetMapping(path = ApiPath.POINT.BALANCE)
	public PointApiResponse getPointBalance(@PathVariable Long memberId) {
		return new PointApiResponse(pointSearchService.getMemberPointBalanceTotal(memberId), PointResponseCode.GET_POINT_BALANCE_SUCCESS);
	}

	@GetMapping(path = ApiPath.POINT.TRANSACTION)
	public PointApiResponse findPointLedgersList(@PathVariable Long memberId, @Valid PointRequest.ListPointLedgerSearchDto request) {
		return new PointApiResponse(pointSearchService.findPointLedgersList(memberId, request), PointResponseCode.LIST_POINT_EARN_REDEEM_SUCCESS);
	}
}

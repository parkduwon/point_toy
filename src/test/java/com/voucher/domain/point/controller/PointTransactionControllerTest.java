package com.voucher.domain.point.controller;

import com.google.gson.Gson;
import com.voucher.domain.point.core.enums.PointTransactionType;
import com.voucher.domain.point.dto.request.PointRequest;
import com.voucher.domain.point.service.PointTransactionService;
import com.voucher.global.common.error.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PointTransactionControllerTest {
	@InjectMocks
	PointTransactionController pointTransactionController;

	@Mock
	PointTransactionService pointTransactionService;

	private MockMvc mockMvc;
	private Gson gson;

	@BeforeEach
	public void init() {
		gson = new Gson();
		mockMvc = MockMvcBuilders.standaloneSetup(pointTransactionController)
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	@DisplayName("포인트 적립 실패 : 멤버 id 입력 예외 테스트")
	void point_save_fail_member_id_invalid_exception_test() throws Exception {
		// given
		final String url = "/api/v1/point/''/transaction";

		// when
		final ResultActions resultActions = mockMvc.perform(
				post(url)
						.content(gson.toJson(new PointRequest.PointTransactionDto(PointTransactionType.EARN_POINT, BigDecimal.TEN)))
						.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@MethodSource("invalidPointAddParameter")
	@DisplayName("포인트 적립 실패 : 잘못된 파라미터")
	void point_save_fail_invalid_parameter(PointTransactionType pointTransactionType, BigDecimal pointAmount) throws Exception {
		// given
		final String url = "/api/v1/point/1/transaction";

		// when
		final ResultActions resultActions = mockMvc.perform(
				post(url)
						.content(gson.toJson(new PointRequest.PointTransactionDto(pointTransactionType, pointAmount)))
						.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("포인트 적립 성공")
	public void point_transaction_success_test() throws Exception {
		// given
		final String url = "/api/v1/point/1/transaction";

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.post(url)
						.content(gson.toJson(new PointRequest.PointTransactionDto(PointTransactionType.EARN_POINT, BigDecimal.TEN)))
						.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		System.out.println(resultActions.andReturn().getResponse().getContentAsString());
		resultActions.andExpect(status().isOk());
	}

	@Test
	@DisplayName("포인트 사용취소 성공")
	public void point_cancel_transaction_success_test() throws Exception {
		// given
		final String url = "/api/v1/point/1/transaction/1";

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.delete(url)
						.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		System.out.println(resultActions.andReturn().getResponse().getContentAsString());
		resultActions.andExpect(status().isOk());
	}


	private static Stream<Arguments> invalidPointAddParameter() {
		return Stream.of(
				Arguments.of(PointTransactionType.EARN_POINT, BigDecimal.valueOf(-1)),
				Arguments.of(null, BigDecimal.valueOf(1000))

		);
	}

}
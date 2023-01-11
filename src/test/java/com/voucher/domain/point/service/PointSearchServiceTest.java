package com.voucher.domain.point.service;

import com.voucher.domain.member.entity.Member;
import com.voucher.domain.member.repository.MemberRepository;
import com.voucher.domain.point.core.enums.PointTransactionType;
import com.voucher.domain.point.dto.request.PointRequest;
import com.voucher.domain.point.dto.response.PointResponse;
import com.voucher.domain.point.entity.PointBalance;
import com.voucher.domain.point.entity.PointLedger;
import com.voucher.domain.point.entity.PointWallet;
import com.voucher.domain.point.helper.PointDataHelper;
import com.voucher.domain.point.repository.PointBalanceRepository;
import com.voucher.domain.point.repository.PointLedgerRepository;
import com.voucher.domain.point.service.intfc.PointTransactionFactor;
import com.voucher.global.common.dto.CommonPageRequest;
import com.voucher.global.common.error.exeption.VoucherPointException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.LongSupplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PointSearchServiceTest {

	@InjectMocks
	PointSearchService pointSearchService;

	@Mock
	PointLedgerRepository pointLedgerRepository;

	@Mock
	MemberRepository memberRepository;

	@Mock
	PointBalanceRepository pointBalanceRepository;

	@Test
	@DisplayName("포인트 잔액 조회 멤버 없음 예외 테스트")
	public void member_not_fount_test() {
		//given
		doReturn(Optional.empty()).when(memberRepository).findById(1L);

		//when
		VoucherPointException exception = Assertions.assertThrows(VoucherPointException.class, () -> {
			Long memberId = 1L;
			pointSearchService.getMemberPointBalanceTotal(memberId);
		});

		//then
		String expectedMessage = "없는 회원 입니다.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}
	@Test
	@DisplayName("포인트 잔액 조회 테스트")
	void get_point_balance_test() {
		//given
		Long memberId = 1L;
		BigDecimal pointBalanceTotal = BigDecimal.valueOf(1000);
		PointBalance pointBalance = PointBalance.builder().memberId(memberId).pointBalanceTotal(pointBalanceTotal).build();
		Member member = Member.builder().memberId(memberId).memberName("송준기").build();
		when(memberRepository.findById(memberId)).thenReturn(Optional.ofNullable(member));
		when(pointBalanceRepository.getPointBalanceByMemberId(memberId)).thenReturn(pointBalance);

		//when
		PointResponse.PointBalanceTotalDto pointBalanceDto = pointSearchService.getMemberPointBalanceTotal(memberId);

		//then
		assertThat(pointBalanceDto.getPointBalanceTotal()).isEqualTo(pointBalanceTotal);

	}

	@Test
	@DisplayName("벤치마크 목록 조회 테스트")
	void find_point_ledgers_list_test() {
		//given
		PointRequest.ListPointLedgerSearchDto listParams = new PointRequest.ListPointLedgerSearchDto(10, 1, Sort.Direction.DESC);
		Pageable pageable = new CommonPageRequest(listParams.page(), listParams.size()).of();
		List<PointResponse.ListPointLedgersDto> listPointLedgersDto = new ArrayList<>();
		Long memberId = 1L;
		BigDecimal resultPointBalance = BigDecimal.ZERO;
		for (int i = 1; i <= 30; i++) {
			PointTransactionType pointTransactionType = 5 % (i + 1) == 0 ? PointTransactionType.REDEEM_POINT : PointTransactionType.EARN_POINT;
			BigDecimal pointAmount = BigDecimal.valueOf(i * 100);
			resultPointBalance = pointTransactionType.calculateBalance(resultPointBalance, pointAmount);

			PointRequest.PointTransactionDto request = new PointRequest.PointTransactionDto(pointTransactionType, pointAmount);
			PointWallet pointWallet = PointDataHelper.createPointWallet(memberId, request, resultPointBalance);
			PointLedger pointLedger = PointDataHelper.createPointLedger((long) i, pointWallet);
			listPointLedgersDto.add(PointResponse.ListPointLedgersDto.of(pointLedger));
		}
		LongSupplier longSupplier = listPointLedgersDto::size;
		Page<PointResponse.ListPointLedgersDto> expectedList = PageableExecutionUtils.getPage(listPointLedgersDto, pageable, longSupplier);
		when(pointLedgerRepository.findPointLedgersList(any(Long.class), any(PointRequest.ListPointLedgerSearchDto.class), any(Pageable.class))).thenReturn(expectedList);

		//when
		Page<PointResponse.ListPointLedgersDto> result = pointSearchService.findPointLedgersList(memberId, listParams);

		//then
		List<PointResponse.ListPointLedgersDto> resultList = result.getContent();
		for (int i = 0; i < resultList.size(); i++) {
			assertThat(resultList.get(i).getPointLedgerId()).isEqualTo(listPointLedgersDto.get(i).getPointLedgerId());
			assertThat(resultList.get(i).getPointTransactionType()).isEqualTo(listPointLedgersDto.get(i).getPointTransactionType());
			assertThat(resultList.get(i).getPointStatusType()).isEqualTo(listPointLedgersDto.get(i).getPointStatusType());
			assertThat(resultList.get(i).getPointAmount()).isEqualTo(listPointLedgersDto.get(i).getPointAmount());
			assertThat(resultList.get(i).getRemainPointAmount()).isEqualTo(listPointLedgersDto.get(i).getRemainPointAmount());
		}
	}
}
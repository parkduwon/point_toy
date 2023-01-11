package com.voucher.domain.point.service;

import com.voucher.domain.member.entity.Member;
import com.voucher.domain.member.repository.MemberRepository;
import com.voucher.domain.point.core.enums.PointTransactionType;
import com.voucher.domain.point.dto.request.PointRequest;
import com.voucher.domain.point.entity.PointLedger;
import com.voucher.domain.point.entity.PointWallet;
import com.voucher.domain.point.helper.PointDataHelper;
import com.voucher.domain.point.repository.PointBalanceRepository;
import com.voucher.domain.point.repository.PointDetailRepository;
import com.voucher.domain.point.repository.PointLedgerRepository;
import com.voucher.domain.point.service.intfc.PointTransactionFactor;
import com.voucher.global.common.error.exeption.VoucherPointException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointRedeemServiceTest {

	@InjectMocks
	private PointRedeemService pointRedeemService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PointLedgerRepository pointLedgerRepository;

	@Mock
	private PointDetailRepository pointDetailRepository;

	@Mock
	private PointBalanceRepository pointBalanceRepository;

	@Test
	@DisplayName("포인트 적립 멤버 없음 예외 테스트")
	public void member_not_fount_test() {
		//given
		doReturn(Optional.empty()).when(memberRepository).findById(1L);

		//when
		VoucherPointException exception = Assertions.assertThrows(VoucherPointException.class, () -> {
			PointTransactionFactor pointTransactionFactor = PointTransactionFactor.builder()
					.memberId(1L)
					.pointTransactionType(PointTransactionType.REDEEM_POINT)
					.pointAmount(BigDecimal.TEN)
					.build();
			pointRedeemService.setPointTransactionFactor(pointTransactionFactor);
			pointRedeemService.doTransaction();
		});

		//then
		String expectedMessage = "등록되지 않은 멤버 입니다.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	@DisplayName("포인트 부족 예외 테스트")
	public void not_enough_point_exception_test() {
		//given
		Long memberId = 1L;
		BigDecimal pointBalanceTotal = BigDecimal.valueOf(999);
		BigDecimal pointAmount = BigDecimal.valueOf(1000);
		doReturn(Optional.of(Member.builder().memberId(memberId).memberName("테스트").build())).when(memberRepository).findById(1L);
		doReturn(PointDataHelper.createPointBalance(memberId, pointBalanceTotal)).when(pointBalanceRepository).pointBalanceForUpdate(memberId);

		//when
		VoucherPointException exception = Assertions.assertThrows(VoucherPointException.class, () -> {
			PointTransactionFactor pointTransactionFactor = PointTransactionFactor.builder()
					.memberId(memberId)
					.pointTransactionType(PointTransactionType.REDEEM_POINT)
					.pointAmount(pointAmount)
					.build();
			pointRedeemService.setPointTransactionFactor(pointTransactionFactor);
			pointRedeemService.doTransaction();
		});

		//then
		BigDecimal resultPointBalance = PointTransactionType.REDEEM_POINT.calculateBalance(pointBalanceTotal, pointAmount);
		String expected = String.format("포인트 잔액이 부족합니다. 부족한 포인트 : %s", resultPointBalance.multiply(BigDecimal.valueOf(-1L)));
		String actual = exception.getMessage();
		System.out.println(exception.getMessage());
		assertEquals(expected, actual);
	}

	@Test
	@DisplayName("포인트 차감 테스트")
	void point_redeem_service_test() {
		//given
		Long memberId = 1L;
		int bound = 1000;
		BigDecimal pointBalanceTotal = BigDecimal.valueOf(bound);
		//포인트 적립 목록 생성
		List<PointLedger> pointLedgers = PointDataHelper.createPointLedgerEarn(bound);
		when(pointLedgerRepository.findEarnLedgerByMemberId(memberId)).thenReturn(pointLedgers);

		BigDecimal pointAmount = BigDecimal.valueOf(500);
		PointTransactionType pointTransactionType = PointTransactionType.REDEEM_POINT;

		//멤버 생성
		Member member = Member.builder().memberId(memberId).build();
		when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(member));

		//잔액 생성
		when(pointBalanceRepository.pointBalanceForUpdate(memberId)).thenReturn(PointDataHelper.createPointBalance(memberId, pointBalanceTotal));

		//포인트 원장 생성
		PointRequest.PointTransactionDto request = PointDataHelper.createPointRequest(pointTransactionType, pointAmount);
		PointWallet pointWallet = PointDataHelper.createPointWallet(memberId, request, pointBalanceTotal);
		when(pointLedgerRepository.save(any())).thenReturn(PointDataHelper.createPointLedger(1L, pointWallet));


		PointTransactionFactor pointTransactionFactor = PointTransactionFactor.builder()
				.memberId(1L)
				.pointTransactionType(pointTransactionType)
				.pointAmount(pointAmount)
				.build();
		pointRedeemService.setPointTransactionFactor(pointTransactionFactor);

		//when
		PointTransactionFactor resultFactor = pointRedeemService.doTransaction();

		//then
		assertThat(resultFactor.getResultPointBalance()).isEqualTo(pointBalanceTotal.subtract(pointAmount));
	}

}
package com.voucher.domain.point.service;

import com.voucher.domain.member.entity.Member;
import com.voucher.domain.member.repository.MemberRepository;
import com.voucher.domain.point.core.enums.PointTransactionType;
import com.voucher.domain.point.dto.request.PointRequest;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointEarnServiceTest {

	@InjectMocks
	private PointEarnService pointEarnService;

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
					.pointTransactionType(PointTransactionType.EARN_POINT)
					.pointAmount(BigDecimal.TEN)
					.build();
			pointEarnService.setPointTransactionFactor(pointTransactionFactor);
			pointEarnService.doTransaction();
		});

		//then
		String expectedMessage = "등록되지 않은 멤버 입니다.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	@DisplayName("포인트 적립 테스트")
	public void point_earn_service_test() {
		//given
		Long memberId = 1L;
		BigDecimal pointBalanceTotal = BigDecimal.valueOf(1000);
		BigDecimal pointAmount = BigDecimal.TEN;
		PointTransactionType pointTransactionType = PointTransactionType.EARN_POINT;

		Member member = Member.builder().memberId(memberId).build();
		when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(member));

		when(pointBalanceRepository.pointBalanceForUpdate(memberId)).thenReturn(PointDataHelper.createPointBalance(memberId, pointBalanceTotal));

		PointRequest.PointTransactionDto request = PointDataHelper.createPointRequest(pointTransactionType, pointAmount);
		PointWallet pointWallet = PointDataHelper.createPointWallet(memberId, request, pointBalanceTotal);
		when(pointLedgerRepository.save(any())).thenReturn(PointDataHelper.createPointLedger(1L, pointWallet));

		PointTransactionFactor pointTransactionFactor = PointTransactionFactor.builder()
				.memberId(1L)
				.pointTransactionType(pointTransactionType)
				.pointAmount(pointAmount)
				.build();
		pointEarnService.setPointTransactionFactor(pointTransactionFactor);

		//when
		PointTransactionFactor resultFactor = pointEarnService.doTransaction();

		//then
		assertThat(resultFactor.getResultPointBalance()).isEqualTo(pointBalanceTotal.add(pointAmount));
	}

}
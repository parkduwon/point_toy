package com.voucher.domain.point.service;

import com.voucher.domain.member.entity.Member;
import com.voucher.domain.member.repository.MemberRepository;
import com.voucher.domain.point.core.enums.PointStatusType;
import com.voucher.domain.point.core.enums.PointTransactionType;
import com.voucher.domain.point.dto.request.PointRequest;
import com.voucher.domain.point.entity.PointDetail;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PointRedeemCancelServiceTest {

	@InjectMocks
	private PointRedeemCancelService pointRedeemCancelService;

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
	public void member_not_fount_exception_test() {
		//given
		doReturn(Optional.empty()).when(memberRepository).findById(1L);

		//when
		VoucherPointException exception = Assertions.assertThrows(VoucherPointException.class, () -> {
			PointTransactionFactor pointTransactionFactor = PointTransactionFactor.builder()
					.memberId(1L)
					.pointTransactionType(PointTransactionType.CANCEL_REDEEM_POINT)
					.pointAmount(BigDecimal.TEN)
					.build();
			pointRedeemCancelService.setPointTransactionFactor(pointTransactionFactor);
			pointRedeemCancelService.doTransaction();
		});

		//then
		String expectedMessage = "등록되지 않은 멤버 입니다.";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	@DisplayName("포인트 거래 id 없음 예외 테스트")
	public void invalid_point_id_exception_test() {
		//given
		Long memberId = 1L;
		Long pointLedgerId = 1L;
		doReturn(Optional.of(Member.builder().memberId(1L).memberName("테스트멤버").build())).when(memberRepository).findById(memberId);
		doReturn(Optional.empty()).when(pointLedgerRepository).getTargetLedgerForRedeemCancel(memberId, pointLedgerId);

		//when
		VoucherPointException exception = Assertions.assertThrows(VoucherPointException.class, () -> {
			PointTransactionFactor pointTransactionFactor = PointTransactionFactor.builder()
					.memberId(memberId)
					.pointLedgerId(pointLedgerId)
					.pointTransactionType(PointTransactionType.CANCEL_REDEEM_POINT)
					.build();
			pointRedeemCancelService.setPointTransactionFactor(pointTransactionFactor);
			pointRedeemCancelService.doTransaction();
		});

		//then
		String expectedMessage = "요청한 포인트 거래가 없습니다. 포인트 거래 id : " + pointLedgerId;
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	@DisplayName("잘못된 상태의 포인트 거래 사용취소 예외 테스트")
	public void invalid_point_status_type_exception_test() {
		//given
		Long memberId = 1L;
		Long pointLedgerId = 1L;
		PointStatusType pointStatusType = PointStatusType.REDEEM_CANCELED;
		doReturn(Optional.of(Member.builder().memberId(1L).memberName("테스트멤버").build())).when(memberRepository).findById(memberId);
		PointLedger pointLedger = PointLedger.builder()
				.pointLedgerId(pointLedgerId)
				.pointTransactionType(PointTransactionType.REDEEM_POINT)
				.pointStatusType(pointStatusType)
				.pointAmount(BigDecimal.TEN)
				.memberId(memberId)
				.pointWallet(null)
				.build();
		doReturn(Optional.of(pointLedger)).when(pointLedgerRepository).getTargetLedgerForRedeemCancel(memberId, pointLedgerId);

		//when
		VoucherPointException exception = Assertions.assertThrows(VoucherPointException.class, () -> {
			PointTransactionFactor pointTransactionFactor = PointTransactionFactor.builder()
					.memberId(memberId)
					.pointLedgerId(pointLedgerId)
					.pointTransactionType(PointTransactionType.CANCEL_REDEEM_POINT)
					.build();
			pointRedeemCancelService.setPointTransactionFactor(pointTransactionFactor);
			pointRedeemCancelService.doTransaction();
		});

		//then
		String expectedMessage = "사용취소 할 수 없는 포인트 거래입니다. 포인트 상태 : " + pointStatusType;
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	@DisplayName("사용취소 대상 포인트의 유효기간 만료 예외 테스트")
	public void cancel_point_ledger_target_expired_exception_test() {
		//given
		Long memberId = 1L;
		Long pointLedgerId = 1L;
		doReturn(Optional.of(Member.builder().memberId(1L).memberName("테스트멤버").build())).when(memberRepository).findById(memberId);
		PointLedger pointLedger = PointLedger.builder()
				.pointLedgerId(pointLedgerId)
				.pointTransactionType(PointTransactionType.REDEEM_POINT)
				.pointStatusType(PointStatusType.REDEEMED)
				.pointAmount(BigDecimal.TEN)
				.memberId(memberId)
				.pointWallet(null)
				.build();
		doReturn(Optional.of(pointLedger)).when(pointLedgerRepository).getTargetLedgerForRedeemCancel(memberId, pointLedgerId);
		PointLedger targetPointLedger = PointLedger.builder()
				.pointLedgerId(pointLedgerId)
				.pointTransactionType(PointTransactionType.EARN_POINT)
				.pointStatusType(PointStatusType.USED)
				.pointAmount(BigDecimal.TEN)
				.memberId(memberId)
				.pointWallet(null)
				.pointExpireDate(LocalDateTime.now().minusDays(3))
				.build();
		doReturn(Collections.singletonList(targetPointLedger)).when(pointLedgerRepository).findAllById(any());

		//when
		VoucherPointException exception = Assertions.assertThrows(VoucherPointException.class, () -> {
			PointTransactionFactor pointTransactionFactor = PointTransactionFactor.builder()
					.memberId(memberId)
					.pointLedgerId(pointLedgerId)
					.pointTransactionType(PointTransactionType.CANCEL_REDEEM_POINT)
					.build();
			pointRedeemCancelService.setPointTransactionFactor(pointTransactionFactor);
			pointRedeemCancelService.doTransaction();
		});

		//then
		String expectedMessage = "사용된 포인트 중 유효기간이 지난 포인트가 있습니다." ;
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}



	@Test
	@DisplayName("포인트 시용 취소 테스트")
	public void point_redeem_cancel_test() {
		//given
		Long memberId = 1L;
		BigDecimal pointBalanceTotal = BigDecimal.valueOf(10000);
		BigDecimal pointAmount = BigDecimal.valueOf(999);
		doReturn(Optional.of(Member.builder().memberId(memberId).memberName("테스트").build())).when(memberRepository).findById(1L);
		doReturn(PointDataHelper.createPointBalance(memberId, pointBalanceTotal)).when(pointBalanceRepository).pointBalanceForUpdate(memberId);
		PointRequest.PointTransactionDto request = PointDataHelper.createPointRequest(PointTransactionType.REDEEM_POINT, pointAmount);
		PointWallet pointWallet = PointDataHelper.createPointWallet(memberId, request, pointBalanceTotal);
		PointLedger pointLedger = PointDataHelper.createPointLedger(1L, pointWallet);
		List<PointDetail> pointDetails = PointDataHelper.createPointLedgerCancelTargets();
		pointDetails.forEach(pointLedger::addPointDetail);
		doReturn(Optional.of(pointLedger)).when(pointLedgerRepository).getTargetLedgerForRedeemCancel(memberId, 1L);

		PointRequest.PointTransactionDto requestInstance = PointDataHelper.createPointRequest(PointTransactionType.CANCEL_REDEEM_POINT, pointAmount);
		PointWallet pointWalletInstance = PointDataHelper.createPointWallet(memberId, requestInstance, pointBalanceTotal);
		doReturn(PointDataHelper.createPointLedger(2L, pointWalletInstance)).when(pointLedgerRepository).save(any());


		//when
		PointTransactionFactor pointTransactionFactor = PointTransactionFactor.builder()
				.memberId(memberId)
				.pointTransactionType(PointTransactionType.CANCEL_REDEEM_POINT)
				.pointLedgerId(1L)
				.build();
		pointRedeemCancelService.setPointTransactionFactor(pointTransactionFactor);
		PointTransactionFactor resultFactor = pointRedeemCancelService.doTransaction();

		//then
		assertThat(resultFactor.getResultPointBalance()).isEqualTo(pointBalanceTotal.add(pointAmount));
	}
}
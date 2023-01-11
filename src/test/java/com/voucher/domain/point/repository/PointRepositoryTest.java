package com.voucher.domain.point.repository;

import com.voucher.domain.point.config.TestJpaConfig;
import com.voucher.domain.point.core.enums.PointTransactionType;
import com.voucher.domain.point.dto.request.PointRequest;
import com.voucher.domain.point.entity.PointDetail;
import com.voucher.domain.point.entity.PointLedger;
import com.voucher.domain.point.entity.PointWallet;
import com.voucher.domain.point.helper.PointDataHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")
@EnableJpaAuditing
@Import(TestJpaConfig.class)
class PointRepositoryTest {
	@Autowired
	private PointLedgerRepository pointLedgerRepository;

	@Test
	@DisplayName("포인트 원장 목록 조회")
	void find_point_ledger_list_test() {
		//given
		Long memberId = 1L;
		BigDecimal resultPointBalance = BigDecimal.ZERO;
		for (int i = 1; i <= 30; i++) {
			PointTransactionType pointTransactionType = i % 5 == 0 ? PointTransactionType.REDEEM_POINT : PointTransactionType.EARN_POINT;
			BigDecimal pointAmount = BigDecimal.valueOf(i * 100);
			resultPointBalance = pointTransactionType.calculateBalance(resultPointBalance, pointAmount);

			PointRequest.PointTransactionDto request = new PointRequest.PointTransactionDto(pointTransactionType, pointAmount);
			PointWallet pointWallet = PointDataHelper.createPointWallet(memberId, request, resultPointBalance);
			PointLedger pointLedger = PointDataHelper.createPointLedger((long) i, pointWallet);
			pointLedgerRepository.save(pointLedger);
		}

		//when
		List<PointLedger> pointLedgers = pointLedgerRepository.findEarnLedgerByMemberId(memberId);

		//then
		assertThat(pointLedgers.size()).isEqualTo(30 - (30/5));
	}
}
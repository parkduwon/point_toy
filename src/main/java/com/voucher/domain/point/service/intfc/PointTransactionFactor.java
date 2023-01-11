package com.voucher.domain.point.service.intfc;

import com.voucher.domain.point.core.enums.PointResponseCode;
import com.voucher.domain.point.core.enums.PointTransactionType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointTransactionFactor {
    private Long memberId;
    private Long pointLedgerId;
    private PointTransactionType pointTransactionType;
    private BigDecimal pointAmount;
    private BigDecimal resultPointBalance;
    private PointTransactionFactory pointTransactionFactory;
	private PointResponseCode pointResponseCode;

    public void setPointTransaction(PointTransactionFactory pointTransactionFactory) {
        this.pointTransactionFactory = pointTransactionFactory;
    }

	public void setResultPointBalance(BigDecimal resultPointBalance) {
		this.resultPointBalance = resultPointBalance;
	}

}

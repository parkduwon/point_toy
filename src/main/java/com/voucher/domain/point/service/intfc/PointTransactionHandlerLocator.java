package com.voucher.domain.point.service.intfc;

import com.voucher.domain.point.core.enums.PointTransactionType;
import com.voucher.global.common.error.ErrorCode;
import com.voucher.global.common.error.exeption.VoucherPointException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointTransactionHandlerLocator {
	private final ListableBeanFactory beanFactory;

	public PointTransactionFactory getPointTransactionHandler(PointTransactionType type) {
		String beanName = type.toString();
		try {
			return beanFactory.getBean(beanName, PointTransactionFactory.class);
		} catch (BeansException ex) {
			throw new VoucherPointException(type + "은/는 지원하지 않는 포인트 거래타입 입니다.", ErrorCode.INVALID_INPUT_VALUE);
		}
	}
}
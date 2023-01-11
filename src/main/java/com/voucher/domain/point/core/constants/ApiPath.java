package com.voucher.domain.point.core.constants;

public class ApiPath {
	public static class POINT {
		public static final String TRANSACTION = "api/v1/point/{memberId}/transaction";
		public static final String TRANSACTION_CANCEL = "api/v1/point/{memberId}/transaction/{pointLedgerId}";
		public static final String BALANCE = "api/v1/point/{memberId}/balance";
	}

	public static class MEMBER {
		public static final String SIGN_UP = "api/v1/members/sign-up";
		public static final String MEMBERS = "api/v1/members";
	}
}

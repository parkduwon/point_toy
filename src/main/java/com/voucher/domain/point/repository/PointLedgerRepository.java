package com.voucher.domain.point.repository;

import com.voucher.domain.point.entity.PointLedger;
import com.voucher.domain.point.repository.custom.PointLedgerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointLedgerRepository extends JpaRepository<PointLedger, Long>, PointLedgerRepositoryCustom {
}

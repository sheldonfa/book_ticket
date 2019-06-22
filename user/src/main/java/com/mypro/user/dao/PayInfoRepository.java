package com.mypro.user.dao;

import com.mypro.user.domain.Customer;
import com.mypro.user.domain.PayInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayInfoRepository extends JpaRepository<PayInfo, Long> {

    PayInfo findOneByOrderId(Long orderId);
}

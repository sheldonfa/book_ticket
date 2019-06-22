package com.mypro.user.dao;

import com.mypro.user.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findOneByCustomerId(Long customerId);

    Customer findOneByUsername(String username);

    @Modifying
    @Query("UPDATE customer set  deposit = deposit-?2 where id =?1")
    int charge(Long customerId, int amount);
}

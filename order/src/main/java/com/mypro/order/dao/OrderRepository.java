package com.mypro.order.dao;

import com.mypro.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Mapper Interface:Order
 *
 * @author fangxin
 * @date 2019-4-10
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findOneByUuid(String uuid);

    Order findOneById(Long id);
}
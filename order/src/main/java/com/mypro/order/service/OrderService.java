package com.mypro.order.service;

import com.mypro.order.dao.OrderRepository;
import com.mypro.order.model.Order;
import com.mypro.service.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * Service Implementation:Order
 *
 * @author fangxin
 * @date 2019-4-10
 */
@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = "order:locked", containerFactory = "msgFactory")
    public void handleOrderNew(OrderDTO dto) {
        log.info("Got new order to create:{}", dto);
        if (orderRepository.findOneByUuid(dto.getUuid()) != null) {
            log.info("Msg already processed:{}", dto);
        } else {
            Order order = createOrder(dto);
            orderRepository.save(order);
            dto.setId(order.getId());
        }
        dto.setStatus("NEW");
        jmsTemplate.convertAndSend("order:pay", dto);
    }

    @Transactional
    @JmsListener(destination = "order:finish", containerFactory = "msgFactory")
    public void handleOrderFinish(OrderDTO dto) {
        log.info("Got order for finish:{}", dto);
        Order order = orderRepository.findOneById(dto.getId());
        order.setStatus("FINISH");
        orderRepository.save(order);
    }

    private Order createOrder(OrderDTO dto) {
        Order order = new Order();
        order.setUuid(dto.getUuid());
        order.setAmount(dto.getAmount());
        order.setTitle(dto.getTitle());
        order.setCustomerId(dto.getCustomerId());
        order.setTicketNum(dto.getTicketNum());
        order.setStatus("NEW");
        order.setCreateDate(ZonedDateTime.now());
        return order;
    }


}

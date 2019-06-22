package com.mypro.order.service;

import com.mypro.order.dao.OrderRepository;
import com.mypro.order.model.Order;
import com.mypro.service.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

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

    /**
     * order错误情况：
     * 1 锁票失败
     * 2 扣费失败后，解锁票
     * 3 定时任务检测到订单超时
     *
     * @param dto
     */
    @Transactional
    @JmsListener(destination = "order:fail", containerFactory = "msgFactory")
    public void handleOrderFail(OrderDTO dto) {
        log.info("Got order for fail:{}", dto);
        Order order;
        if (dto.getId() == null) {
            order = createOrder(dto);
            order.setReason("TICKET_LOCK_FAIL");
        } else {
            order = orderRepository.findOneById(dto.getId());
            if (dto.getStatus().equals("NOT_ENOUGH_DEPOSIT")) {
                order.setReason("NOT_ENOUGH_DEPOSIT");
            }
            if(dto.getStatus().equals("TIMEOUT")){
                order.setReason("TIMEOUT");
            }
        }
        order.setStatus("FAIL");
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

    @Scheduled(fixedDelay = 10000L)
    public void checkTimeoutOrders() {
        ZonedDateTime checkTime = ZonedDateTime.now().minusMinutes(1L);
        List<Order> orders = orderRepository.findAllByStatusAndCreateDateBefore("NEW", checkTime);
        orders.forEach(order -> {
            log.error("Order timeout:{}", order);
            OrderDTO dto = new OrderDTO();
            dto.setId(order.getId());
            dto.setTicketNum(order.getTicketNum());
            dto.setUuid(order.getUuid());
            dto.setAmount(order.getAmount());
            dto.setTitle(order.getTitle());
            dto.setCustomerId(order.getCustomerId());
            dto.setStatus("TIMEOUT");
            jmsTemplate.convertAndSend("order:fail", dto);
        });
    }

}

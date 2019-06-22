package com.mypro.order.web;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.mypro.order.dao.OrderRepository;
import com.mypro.service.IOrderService;
import com.mypro.service.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller of Order
 *
 * @author fangxin
 * @date 2019-4-10
 */
@RestController
@RequestMapping("/api/order")
public class OrderResource implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    private TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();

    @PostMapping("")
    public void create(@RequestBody OrderDTO order) {
        order.setUuid(uuidGenerator.generate().toString());
        jmsTemplate.convertAndSend("order:new", order);
    }
}

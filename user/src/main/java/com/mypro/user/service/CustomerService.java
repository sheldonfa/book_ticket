package com.mypro.user.service;

import com.mypro.service.dto.OrderDTO;
import com.mypro.user.dao.CustomerRepository;
import com.mypro.user.dao.PayInfoRepository;
import com.mypro.user.domain.Customer;
import com.mypro.user.domain.PayInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation:User
 *
 * @author fangxin
 * @date 2019-4-9
 */
@Service
@Slf4j
public class CustomerService {

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PayInfoRepository payInfoRepository;

    @Transactional
    @JmsListener(destination = "order:pay", containerFactory = "msgFactory")
    public void handleOrderPay(OrderDTO dto) {
        log.info("Get new order for pay:{}", dto);

        PayInfo pay = payInfoRepository.findOneByOrderId(dto.getId());
        if (pay != null) {
            log.warn("Order already paid, duplicated message:{}", dto);
            return;
        }
        Customer customer = customerRepository.findOneById(dto.getCustomerId());
        if (customer.getDeposit() < dto.getAmount()) {
            log.warn("Not enough deposit.");
            dto.setStatus("NOT_ENOUGH_DEPOSIT");
            jmsTemplate.convertAndSend("order:ticket_error", dto);
            return;
        }
        pay = new PayInfo();
        pay.setOrderId(dto.getId());
        pay.setAmount(dto.getAmount());
        pay.setStatus("PAID");
        payInfoRepository.save(pay);
        customerRepository.charge(customer.getId(), dto.getAmount());
        dto.setStatus("PAID");
        jmsTemplate.convertAndSend("order:ticket_move", dto);
    }
}

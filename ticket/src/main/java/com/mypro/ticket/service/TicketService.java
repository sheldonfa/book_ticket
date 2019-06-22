package com.mypro.ticket.service;

import com.mypro.service.dto.OrderDTO;
import com.mypro.ticket.dao.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = "order:new", containerFactory = "msgFactory")
    public void handleTicketLock(OrderDTO dto) {
        log.info("Got new order for ticket lock:{}", dto);
        int lockCount = ticketRepository.lockTicket(dto.getCustomerId(), dto.getTicketNum());
        if (lockCount == 1) {
            dto.setStatus("TICKET_LOCKED");
            jmsTemplate.convertAndSend("order:locked", dto);
        } else {
            dto.setStatus("TICKET_LOCK_FAIL");
            jmsTemplate.convertAndSend("order:fail", dto);
        }
    }

    @Transactional
    @JmsListener(destination = "order:ticket_move", containerFactory = "msgFactory")
    public void handleTicketMove(OrderDTO dto) {
        log.info("Got new order for ticket move:{}", dto);
        int count = ticketRepository.moveTicket(dto.getCustomerId(), dto.getTicketNum());
        if (count == 0) {
            log.warn("Ticket already moved:{}", dto);
        }
        dto.setStatus("TICKET_MOVED");
        jmsTemplate.convertAndSend("order:finish", dto);
    }

    @Transactional
    @JmsListener(destination = "order:ticket_error", containerFactory = "msgFactory")
    public void handleTicketUnlock(OrderDTO dto) {
        int count = ticketRepository.unlockTicket(dto.getCustomerId(), dto.getTicketNum());
        if (count == 0) {
            log.info("Ticket already unlocked:{}", dto);
        }
        count = ticketRepository.unMoveTicket(dto.getCustomerId(), dto.getTicketNum());
        if (count == 0) {
            log.info("Ticket already unmoved, or not moved:{}", dto);
        }
        jmsTemplate.convertAndSend("order:fail", dto);
    }

    /**
     * 锁票
     */
    @Transactional
    public int ticketLock(OrderDTO dto) {
        int lockCount = ticketRepository.lockTicket(dto.getCustomerId(), dto.getTicketNum());
        log.info("Update ticket lock count:{}", lockCount);
        // 延迟
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        return lockCount;
    }
}

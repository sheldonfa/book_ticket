package com.mypro.ticket.web;

import com.mypro.service.dto.OrderDTO;
import com.mypro.ticket.dao.TicketRepository;
import com.mypro.ticket.model.Ticket;
import com.mypro.ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * Controller of Order
 *
 * @author fangxin
 * @date 2019-4-10
 */
@RestController
@RequestMapping("/api/ticket")
public class TicketResource {


    @PostConstruct
    public void init() {
        if (ticketRepository.count() > 0) {
            return;
        }
        Ticket ticket = new Ticket();
        ticket.setName("Num.1");
        ticket.setTicketNum(100L);
        ticketRepository.save(ticket);
    }

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @PostMapping("/lock")
    public void lock(@RequestBody OrderDTO dto) {
        ticketService.ticketLock(dto);
    }
}

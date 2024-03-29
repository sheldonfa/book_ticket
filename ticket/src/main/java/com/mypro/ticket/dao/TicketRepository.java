package com.mypro.ticket.dao;

import com.mypro.ticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Mapper Interface:Order
 *
 * @author fangxin
 * @date 2019-4-10
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findOneByOwner(Long owner);

    Ticket findOneByTicketNumAndLockUser(Long ticketNum, Long lockUser);

    @Modifying
    @Query("UPDATE ticket SET lockUser = ?1 WHERE ticketNum = ?2 and lockUser is null and owner is null ")
    int lockTicket(Long customerId, Long ticketNum);

    @Modifying
    @Query("UPDATE ticket SET owner = ?1, lockUser = null WHERE lockUser = ?1 AND ticketNum = ?2")
    int moveTicket(Long customerId, Long ticketNum);

    @Modifying
    @Query("UPDATE ticket SET lockUser = null where lockUser = ?1 and ticketNum = ?2")
    int unlockTicket(Long customerId, Long ticketNum);

    @Modifying
    @Query("UPDATE ticket SET owner = null where owner = ?1 and ticketNum = ?2")
    int unMoveTicket(Long customerId, Long ticketNum);
}
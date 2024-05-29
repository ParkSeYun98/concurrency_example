package org.example.concurrency_example.domain.ticket.repository;

import jakarta.persistence.LockModeType;
import org.example.concurrency_example.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query(value = "select t from Ticket t where t.id = :ticketId")
    Optional<Ticket> findByIdOnOptimisticLock(@Param("ticketId") Long ticketId);
}

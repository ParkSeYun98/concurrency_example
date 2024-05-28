package org.example.concurrency_example.domain.ticket.service;

import lombok.RequiredArgsConstructor;
import org.example.concurrency_example.domain.ticket.entity.Ticket;
import org.example.concurrency_example.domain.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional
    public void ticketing(Long ticketId, Long quantity) {
        Ticket ticket = ticketRepository.findByIdOnPessimisticLock(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("해당 티켓이 존재하지 않습니다."));

        ticket.decreaseTicketAmount(quantity);

        ticketRepository.saveAndFlush(ticket);
    }
}

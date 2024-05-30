package org.example.concurrency_example.domain.ticket.service;

import lombok.RequiredArgsConstructor;
import org.example.concurrency_example.domain.ticket.entity.Ticket;
import org.example.concurrency_example.domain.ticket.repository.RedisRepository;
import org.example.concurrency_example.domain.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final RedisRepository redisRepository;

    @Transactional
    public void ticketing(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("해당 티켓이 존재하지 않습니다."));

        System.out.println("Current ticket amount: " + ticket.getAmount());

        ticket.decreaseTicketAmount();

        ticketRepository.saveAndFlush(ticket);
        System.out.println("Decreased ticket amount: " + ticket.getAmount());
    }

    @Transactional
    public void ticketingWithRedis(Long ticketId) throws InterruptedException {
        while (!redisRepository.lock(ticketId)) {
            Thread.sleep(100);
        }

        try {
            ticketing(ticketId);
        } finally {
            redisRepository.unlock(ticketId);
        }
    }
}

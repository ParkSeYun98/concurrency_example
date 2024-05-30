package org.example.concurrency_example.domain.ticket.service;

import lombok.RequiredArgsConstructor;
import org.example.concurrency_example.domain.ticket.entity.Ticket;
import org.example.concurrency_example.domain.ticket.repository.TicketRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;

    private final RedissonClient redissonClient;

    @Transactional
    public void ticketing(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("해당 티켓이 존재하지 않습니다."));

        ticket.decreaseTicketAmount();
    }

    @Transactional
    public void ticketingWithRedisson(Long ticketId) throws InterruptedException {
        RLock rLock = redissonClient.getLock(ticketId.toString());

        try {
            boolean flag = rLock.tryLock(10, 1, TimeUnit.SECONDS);

            if(!flag) {
                System.out.println("Lock 실패");
                return;
            }

            ticketing(ticketId);
        }
        finally {
            rLock.unlock();
        }

    }
}

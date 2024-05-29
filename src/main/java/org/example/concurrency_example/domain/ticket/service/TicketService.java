package org.example.concurrency_example.domain.ticket.service;

import lombok.RequiredArgsConstructor;
import org.example.concurrency_example.domain.ticket.entity.Ticket;
import org.example.concurrency_example.domain.ticket.repository.TicketRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional
    public void ticketing(Long ticketId) {
        Ticket ticket = ticketRepository.findByIdOnOptimisticLock(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("해당 티켓이 존재하지 않습니다."));

        ticket.decreaseTicketAmount();
    }

    // update 실패 대비
    public void ticketingByOptimisticLock(Long ticketId) throws InterruptedException {
        int retryCount = 0;
        int maxRetries = 10; // 재시도 횟수 제한 설정

        while (retryCount < maxRetries) {
            try {
                ticketing(ticketId);
                break;
            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                Thread.sleep(50); // 지수 백오프 전략도 고려 가능
            }
        }

        if (retryCount == maxRetries) {
            throw new RuntimeException("Failed to update ticket after " + maxRetries + " retries");
        }
    }
}

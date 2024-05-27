package org.example.concurrency_example.domain.ticket.service;

import lombok.RequiredArgsConstructor;
import org.example.concurrency_example.domain.ticket.entity.Ticket;
import org.example.concurrency_example.domain.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

/*
* 트랜잭션의 격리 수준을 가장 높은 수준으로 적용
*
* 현재 트랜잭션이 완료되기 전 까지 다른 트랜잭션에서 변경된 데이터를 읽을 수 없다.
* 그렇지만 여전히 Deadlock이 발생한다.
* 왜?
* serializable은 공유 락이다. 즉, 트랜잭션이 진행되는 동안 다른 트랜잭션에서 쓰기를 못하지만 읽기는 가능하다.
* 그렇기 때문에 트랜잭션이 끝날 때 까지 다른 트랜잭션에서 수정할 수 없다.
*
* 그렇지만 첫 Read에서는 제한이 없기 때문에, 동시에 요청이 들어와 처음 상태를 모두 읽어버린다.
* */
@Transactional(isolation = Isolation.SERIALIZABLE, readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional
    public void ticketing(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("해당 티켓이 존재하지 않습니다."));

        ticket.decreaseTicketAmount();
    }
}

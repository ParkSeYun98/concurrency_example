package org.example.concurrency_example.domain.ticket.service;

import jakarta.persistence.EntityManager;
import org.example.concurrency_example.domain.ticket.entity.Ticket;
import org.example.concurrency_example.domain.ticket.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TicketServiceTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    private int memberCount = 120;
    private int ticketAmount = 100;

    @BeforeEach
    public void init() {
        ticketRepository.deleteAll(); // 기존 데이터를 삭제하고
        ticketRepository.save(new Ticket(1L, ticketAmount));
    }

    @Test
    @DisplayName("순차적 티케팅")
    void Test1() {
        // given
        Ticket findTicket = ticketRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디에 맞는 티켓을 찾지 못했습니다."));

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i=0; i<memberCount; i++) {
            try {
                ticketService.ticketing(findTicket.getId());
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        }

        System.out.println("successCount = " + successCount);
        System.out.println("failCount = " + failCount);

        Ticket updatedTicket = ticketRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 티켓이 존재하지 않습니다."));

        // then
        assertThat(updatedTicket.getAmount()).isEqualTo(0);
    }

    @Test
    @DisplayName("멀티쓰레드 환경에서 동시 티켓팅 : Race Condition 발생")
    void Test2() throws InterruptedException {
        // 시작 시간 기록
        long startTime = System.currentTimeMillis();

        int threadCnt = 100;

        // 스레드 풀 객체 : 32개의 스레드 관리
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 각 스레드의 작업이 종료될 때까지 기다리는 동기화 수행
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

        List<Ticket> ticketList = ticketRepository.findAll();

        for(int i=0; i<threadCnt; i++) {
            // 동시 티케팅
            executorService.submit(() -> {
                try {
                    ticketService.ticketingWithRedisson(ticketList.get(0).getId());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    // 각 스레드의 작업 종료 명시
                    countDownLatch.countDown();
                }
            });
        }

        // 메인 스레드는 countDownLatch의 count가 0이 될 때 까지 기다린다.
        countDownLatch.await();

        Ticket ticket = ticketRepository.findById(ticketList.get(0).getId())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 티켓이 존재하지 않습니다."));

        // 종료 시간 기록
        long endTime = System.currentTimeMillis();

        // 소요 시간 계산
        long duration = endTime - startTime;
        System.out.println("Test duration: " + duration + " ms");

        assertThat(ticket.getAmount()).isEqualTo(0);

        entityManager.flush();
    }
}
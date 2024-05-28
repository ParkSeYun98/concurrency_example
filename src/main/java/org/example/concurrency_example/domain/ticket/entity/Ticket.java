package org.example.concurrency_example.domain.ticket.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    private int amount;

    @Builder
    public Ticket(Long id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public void decreaseTicketAmount(Long quantity) {
        if(this.amount - quantity < 0) {
            throw new RuntimeException("재고는 0개 미만이 될 수 없습니다");
        }

        this.amount -= quantity;
    }
}

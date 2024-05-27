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

    public void decreaseTicketAmount() {
        if(this.amount == 0)
            throw new IllegalArgumentException("예매 마감!");

        this.amount--;
    }
}

package org.example.concurrency_example.domain.ticket.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int amount;

    public Ticket(Long id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public void decreaseTicketAmount() {
        if (this.amount > 0) {
            this.amount--;
        } else {
            throw new IllegalStateException("티켓이 모두 소진되었습니다.");
        }
    }
}

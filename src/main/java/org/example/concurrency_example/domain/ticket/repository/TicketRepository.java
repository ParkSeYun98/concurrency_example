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

    /*
    *
    @Lock() 어노테이션은 JPA에서 사용되는 어노테이션으로, 데이터베이스 락을 설정할 때 사용됩니다. 이 락은 트랜잭션 간에 데이터베이스 레코드에 대한 접근을 제어하는 데 사용됩니다.
    일반적으로 @Lock() 어노테이션을 사용하는 경우에는 해당 메소드가 데이터베이스 레코드에 대한 업데이트나 변경을 수행할 때입니다. 이런 경우 Spring Data JPA는 메소드 파라미터와 쿼리에 대해 일치하는 네임드 파라미터를 찾으려고 시도하게 됩니다. 하지만 여기서 문제가 발생할 수 있습니다.
    데이터베이스 락을 설정하는 경우, JPA가 내부적으로 쿼리를 생성하는 과정에서 네임드 파라미터를 사용할 때 발생하는 문제가 있을 수 있습니다. 이는 JPA의 동작 방식과 관련이 있습니다.
    반면에 @Lock() 어노테이션이 없는 경우에는 메소드가 단순히 데이터를 조회하는 용도로 사용될 가능성이 높습니다. 이 경우 JPA는 쿼리를 생성할 때 네임드 파라미터를 사용할 필요가 없을 수 있습니다. 그래서 네임드 파라미터 없이도 잘 작동할 수 있습니다.
    결론적으로 @Lock() 어노테이션을 사용하는 경우, Spring Data JPA는 메소드 파라미터와 쿼리에 대해 더 엄격하게 검사하게 됩니다. 그래서 @Param 어노테이션을 사용하여 네임드 파라미터를 명시적으로 지정하는 것이 좋습니다. 이렇게 하면 코드의 가독성을 높이고 오류를 방지할 수 있습니다.
    * */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select t from Ticket t where t.id = :ticketId")
    Optional<Ticket> findByIdOnPessimisticLock(@Param("ticketId") Long ticketId);
}

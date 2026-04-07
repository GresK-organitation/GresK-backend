package com.gresk.modules.ticket.infrastructure;

import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import com.gresk.modules.ticket.application.usecase.PurchaseTicketCommand;
import com.gresk.modules.ticket.domain.exception.DuplicateTicketException;
import com.gresk.modules.event.domain.exception.EventSoldOutException;
import com.gresk.modules.ticket.domain.model.PaymentResult;
import com.gresk.modules.ticket.domain.port.out.PaymentGateway;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        properties = {
                "jwt.secret=dGVzdHNlY3JldGtleWZvcnRlc3RpbmdwdXJwb3Nlc29ubHlub3Rmb3Jwcm9kdWN0aW9u",
                "jwt.expiration-ms=86400000",
                "spring.flyway.enabled=true",
                "spring.autoconfigure.exclude="
        }
)
@Testcontainers
class PurchaseTicketConcurrencyIT {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        PaymentGateway stubPaymentGateway() {
            return (userId, eventId, amount) -> new PaymentResult(true, UUID.randomUUID().toString());
        }
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired
    private TransactionalPurchaseTicketService purchaseTicketService;

    @Autowired
    private EventRepository eventRepository;

    @Test
    void concurrentPurchase_lastTicket_exactlyOneSucceeds() throws InterruptedException {
        Event event = createAndSavePublishedEvent(1);
        EventId eventId = event.getId();

        UserId user1 = UserId.generate();
        UserId user2 = UserId.generate();

        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<?>> futures = new ArrayList<>();

        for (UserId userId : List.of(user1, user2)) {
            futures.add(executor.submit(() -> {
                try {
                    startLatch.await();
                    purchaseTicketService.execute(new PurchaseTicketCommand(
                            userId.value().toString(), eventId.value().toString()));
                    successCount.incrementAndGet();
                } catch (EventSoldOutException | DuplicateTicketException e) {
                    failureCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }

        startLatch.countDown();
        for (Future<?> f : futures) {
            try { f.get(); } catch (Exception ignored) {}
        }
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(1);

        Event finalEvent = eventRepository.findById(eventId).orElseThrow();
        assertThat(finalEvent.getCapacity().available()).isEqualTo(0);
    }

    @Test
    void concurrentPurchase_partialCapacity_exactlyFiveSucceed() throws InterruptedException {
        Event event = createAndSavePublishedEvent(5);
        EventId eventId = event.getId();

        int threadCount = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            UserId userId = UserId.generate();
            futures.add(executor.submit(() -> {
                try {
                    startLatch.await();
                    purchaseTicketService.execute(new PurchaseTicketCommand(
                            userId.value().toString(), eventId.value().toString()));
                    successCount.incrementAndGet();
                } catch (EventSoldOutException | DuplicateTicketException e) {
                    failureCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }

        startLatch.countDown();
        for (Future<?> f : futures) {
            try { f.get(); } catch (Exception ignored) {}
        }
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(5);
        assertThat(failureCount.get()).isEqualTo(5);

        Event finalEvent = eventRepository.findById(eventId).orElseThrow();
        assertThat(finalEvent.getCapacity().available()).isEqualTo(0);
    }

    private Event createAndSavePublishedEvent(int capacity) {
        Event event = Event.create("Concurrent Test Event", PromoterId.generate())
                .withGenre(Genre.ELECTRONIC)
                .withPrice(new Price(new BigDecimal("20.00"), "EUR"))
                .withCapacity(Capacity.of(capacity))
                .withEventDate(LocalDateTime.now().plusMonths(1))
                .withLocation(new Location("Madrid", "Gran Via 1", "Sala"));
        event.publish();
        return eventRepository.save(event);
    }
}

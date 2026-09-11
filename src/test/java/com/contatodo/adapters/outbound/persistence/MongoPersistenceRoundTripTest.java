package com.contatodo.adapters.outbound.persistence;

import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests verifying that repository adapters round-trip immutable
 * domain entities through real MongoDB.
 *
 * <p>The container starts automatically when Docker is available and the
 * tests are skipped gracefully otherwise.</p>
 */
@SpringBootTest(properties = "JWT_SECRET=integration-test-secret-0123456789abcdef0123456789abcdef")
@Testcontainers(disabledWithoutDocker = true)
class MongoPersistenceRoundTripTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void userRoundTripPreservesImmutableEntity() {
        User user = User.builder()
                .userName("roundtrip")
                .email("roundtrip@example.com")
                .password("hashed")
                .name("Round Trip")
                .build();

        User saved = userRepository.save(user);
        Optional<User> reloaded = userRepository.findByEmail("roundtrip@example.com");

        assertTrue(reloaded.isPresent());
        assertEquals(saved.getId(), reloaded.get().getId());
        assertEquals("roundtrip", reloaded.get().getUserName());
        assertEquals("hashed", reloaded.get().getPassword());
    }

    @Test
    void productRoundTripResolvesByCode() {
        Product product = Product.builder()
                .name("Cafe Roundtrip")
                .stock(7)
                .code("9998")
                .realCost(70.0)
                .unitRealCost(10.0)
                .unitPublicCost(15.0)
                .isActive(true)
                .userOid("user-roundtrip")
                .createdDate(java.time.LocalDateTime.now())
                .updatedDate(java.time.LocalDateTime.now())
                .build();

        Product saved = productRepository.save(product);
        Optional<Product> reloaded = productRepository.findByCode("9998");

        assertTrue(reloaded.isPresent());
        assertEquals(saved.getId(), reloaded.get().getId());
        assertEquals(7, reloaded.get().getStock());
        assertEquals(Double.valueOf(10.0), reloaded.get().getUnitRealCost());
    }
}

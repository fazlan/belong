package au.com.belong.customerphoneapi.repository;

import au.com.belong.customerphoneapi.domain.PhoneNumber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataR2dbcTest
@ActiveProfiles("test")
public class PhoneNumberRepositoryTest {

    @Autowired
    private PhoneNumberRepository repository;

    @Test
    void testing_fetching_all_phone_numbers_from_db_with_pagination() {
        Mono<List<PhoneNumber>> phoneNumbersStream = repository
                .findAllBy(PageRequest.of(0, 10))
                .collectList();

        StepVerifier
                .create(phoneNumbersStream)
                .assertNext(pages -> assertEquals(4, pages.size()))
                .verifyComplete();
    }

    @Test
    void testing_updating_phone_number_activation_storing_to_db() {
        Mono<PhoneNumber> phoneNumberMono = repository.findById(1L)
                .map(phone -> {
                    phone.setActive(true);
                    return phone;
                })
                .flatMap(phone -> repository.save(phone));

        StepVerifier
                .create(phoneNumberMono)
                .assertNext(phone -> assertTrue(phone.isActive()))
                .verifyComplete();
    }

    @Test
    void testing_updating_phone_number_deactivation_storing_to_db() {
        Mono<PhoneNumber> phoneNumberMono = repository.findById(1L)
                .map(phone -> {
                    phone.setActive(false);
                    return phone;
                })
                .flatMap(phone -> repository.save(phone));

        StepVerifier
                .create(phoneNumberMono)
                .assertNext(phone -> assertFalse(phone.isActive()))
                .verifyComplete();
    }
}

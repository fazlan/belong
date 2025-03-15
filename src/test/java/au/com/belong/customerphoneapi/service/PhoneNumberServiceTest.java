package au.com.belong.customerphoneapi.service;

import au.com.belong.customerphoneapi.domain.PhoneNumber;
import au.com.belong.customerphoneapi.exception.ResourceNotFoundException;
import au.com.belong.customerphoneapi.exception.ResourceStateConflictException;
import au.com.belong.customerphoneapi.repository.PhoneNumberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static au.com.belong.customerphoneapi.PhoneNumberFixtures.CUST_ONE_PHONE_ONE;
import static au.com.belong.customerphoneapi.PhoneNumberFixtures.CUST_ONE_PHONE_TWO;
import static au.com.belong.customerphoneapi.PhoneNumberFixtures.CUST_THREE_PHONE_ONE;
import static au.com.belong.customerphoneapi.PhoneNumberFixtures.CUST_TWO_PHONE_ONE;
import static au.com.belong.customerphoneapi.PhoneNumberFixtures.CUST_TWO_PHONE_TWO;
import static au.com.belong.customerphoneapi.PhoneNumberFixtures.getPhoneNumbersAsFluxFor;
import static au.com.belong.customerphoneapi.PhoneNumberFixtures.getPhoneNumbersAsFluxForCustomer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PhoneNumberServiceTest {

    @Mock
    private PhoneNumberRepository repository;

    private PhoneNumberService phoneNumberService;

    @BeforeEach
    void beforeEachTest() {
        phoneNumberService = new PhoneNumberServiceImpl(repository);
    }

    // Mono<PageDTO<PhoneNumber>> getPhoneNumbers(int page, int size)

    @Test
    void testing_get_all_for_when_repository_throws_error() {
        int page = 0;
        int size = 3;
        RuntimeException databaseUnreachableError = new RuntimeException("Database unreachable");
        when(repository.count()).thenReturn(Mono.error(databaseUnreachableError));
        when(repository.findAllBy(PageRequest.of(page, size))).thenReturn(Flux.error(databaseUnreachableError));

        StepVerifier
                .create(phoneNumberService.getAllFor(page, size))
                // verifying same exception instance
                .expectErrorMatches(ex -> databaseUnreachableError == ex)
                .verify();
    }

    @Test
    void testing_get_all_for_when_no_records_in_database() {
        int page = 0;
        int size = 3;

        Flux<PhoneNumber> records = Flux.empty();
        when(repository.count()).thenReturn(records.count());
        when(repository.findAllBy(PageRequest.of(page, size))).thenReturn(records);

        StepVerifier
                .create(phoneNumberService.getAllFor(page, size))
                .assertNext(dto -> {
                    assertTrue(dto.isLast());
                    assertEquals(0, dto.getPage());
                    assertEquals(3, dto.getSize());
                    assertEquals(0, dto.getTotalPages());
                    assertEquals(0, dto.getTotalRecords());
                    assertTrue(dto.getContent().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void testing_get_all_for_when_records_less_than_page_size() {
        int page = 0;
        int size = 3;

        Flux<PhoneNumber> records = getPhoneNumbersAsFluxFor(2);
        when(repository.count()).thenReturn(records.count());
        when(repository.findAllBy(PageRequest.of(page, size))).thenReturn(records);

        StepVerifier
                .create(phoneNumberService.getAllFor(page, size))
                .assertNext(dto -> {
                    assertTrue(dto.isLast());
                    assertEquals(0, dto.getPage());
                    assertEquals(3, dto.getSize());
                    assertEquals(1, dto.getTotalPages());
                    assertEquals(2, dto.getTotalRecords());
                    assertEquals(2, dto.getContent().size());
                    assertTrue(dto.getContent().contains(CUST_ONE_PHONE_ONE));
                    assertTrue(dto.getContent().contains(CUST_ONE_PHONE_TWO));
                })
                .verifyComplete();
    }

    @Test
    void testing_get_all_for_when_records_equal_to_page_size() {
        int page = 0;
        int size = 3;

        Flux<PhoneNumber> records = getPhoneNumbersAsFluxFor(3);
        when(repository.count()).thenReturn(records.count());
        when(repository.findAllBy(PageRequest.of(page, size))).thenReturn(records);

        StepVerifier
                .create(phoneNumberService.getAllFor(page, size))
                .assertNext(dto -> {
                    assertTrue(dto.isLast());
                    assertEquals(0, dto.getPage());
                    assertEquals(3, dto.getSize());
                    assertEquals(1, dto.getTotalPages());
                    assertEquals(3, dto.getTotalRecords());
                    assertEquals(3, dto.getContent().size());
                    assertTrue(dto.getContent().contains(CUST_ONE_PHONE_ONE));
                    assertTrue(dto.getContent().contains(CUST_ONE_PHONE_TWO));
                    assertTrue(dto.getContent().contains(CUST_TWO_PHONE_ONE));
                })
                .verifyComplete();
    }

    @Test
    void testing_get_all_for_when_records_greater_than_page_size() {
        int page = 0;
        int size = 3;

        Flux<PhoneNumber> records = getPhoneNumbersAsFluxFor(5);
        when(repository.count()).thenReturn(records.count());
        when(repository.findAllBy(PageRequest.of(page, size))).thenReturn(records);

        StepVerifier
                .create(phoneNumberService.getAllFor(page, size))
                .assertNext(dto -> {
                    assertFalse(dto.isLast());
                    assertEquals(0, dto.getPage());
                    assertEquals(3, dto.getSize());
                    assertEquals(2, dto.getTotalPages());
                    assertEquals(5, dto.getTotalRecords());
                    assertEquals(5, dto.getContent().size());
                    assertTrue(dto.getContent().contains(CUST_ONE_PHONE_ONE));
                    assertTrue(dto.getContent().contains(CUST_ONE_PHONE_TWO));
                    assertTrue(dto.getContent().contains(CUST_TWO_PHONE_ONE));
                    assertTrue(dto.getContent().contains(CUST_TWO_PHONE_TWO));
                    assertTrue(dto.getContent().contains(CUST_THREE_PHONE_ONE));
                })
                .verifyComplete();
    }

    // Flux<PhoneNumber> getPhoneNumbers(long customerId)

    @Test
    void testing_get_all_for_customer_id_when_repository_throws_error() {
        long customerId = 1;

        RuntimeException databaseUnreachableError = new RuntimeException("Database unreachable");
        when(repository.findByCustomerId(customerId)).thenReturn(Flux.error(databaseUnreachableError));

        StepVerifier
                .create(phoneNumberService.getAllFor(customerId))
                // verifying same exception instance
                .expectErrorMatches(ex -> databaseUnreachableError == ex)
                .verify();
    }

    @Test
    void testing_get_all_for_customer_id_when_no_records_in_database() {
        long customerId = 1;

        when(repository.findByCustomerId(customerId)).thenReturn(Flux.empty());

        StepVerifier
                .create(phoneNumberService.getAllFor(customerId))
                .verifyComplete();
    }

    @Test
    void testing_get_all_for_customer_id_when_records_in_database() {
        long customerId = 1;

        Flux<PhoneNumber> records = getPhoneNumbersAsFluxForCustomer(customerId);
        when(repository.findByCustomerId(customerId)).thenReturn(records);

        StepVerifier
                .create(phoneNumberService.getAllFor(customerId))
                .assertNext(phoneOne -> assertEquals(CUST_ONE_PHONE_ONE, phoneOne))
                .assertNext(phoneTwo -> assertEquals(CUST_ONE_PHONE_TWO, phoneTwo))
                .verifyComplete();
    }

    // Mono<PhoneNumber> updatePhoneNumberActiveStatus(long phoneId, boolean status)

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testing_update_for_phone_number_and_status_when_repository_throws_error(boolean isActive) {
        long phoneId = 1;

        RuntimeException databaseUnreachableError = new RuntimeException("Database unreachable");
        when(repository.findById(phoneId)).thenReturn(Mono.error(databaseUnreachableError));

        StepVerifier
                .create(phoneNumberService.updateFor(phoneId, isActive))
                // verifying same exception instance
                .expectErrorMatches(ex -> databaseUnreachableError == ex)
                .verify();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testing_update_for_phone_number_and_status_throws_not_found_error_when_repository_has_not_phone_record(boolean isActive) {
        long phoneId = 100;

        when(repository.findById(phoneId)).thenReturn(Mono.empty());

        StepVerifier
                .create(phoneNumberService.updateFor(phoneId, isActive))
                // verifying same exception instance
                .expectErrorMatches(ex ->
                        ex instanceof ResourceNotFoundException &&
                        "Record not found with ID 100".equals(ex.getMessage()))
                .verify();
    }

    @Test
    void testing_update_for_phone_number_and_status_throws_state_conflict_error_when_repository_has_phone_record_already_in_desired_state() {
        long phoneId = CUST_ONE_PHONE_ONE.getId();
        boolean newStatus = CUST_ONE_PHONE_ONE.isActive();

        when(repository.findById(phoneId)).thenReturn(Mono.just(CUST_ONE_PHONE_ONE));

        StepVerifier
                // same status as persisted record
                .create(phoneNumberService.updateFor(phoneId, newStatus))
                // verifying same exception instance
                .expectErrorMatches(ex ->
                        ex instanceof ResourceStateConflictException &&
                        "Phone number is already in the desired state.".equals(ex.getMessage()))
                .verify();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testing_update_for_phone_number_and_status_should_sucessfully_update_when_repository_has_phone_record_not_in_desired_state(boolean currentStatus) {
        boolean newStatus = !currentStatus;
        long phoneId = CUST_ONE_PHONE_ONE.getId();
        CUST_ONE_PHONE_ONE.setActive(currentStatus);

        when(repository.findById(phoneId)).thenReturn(Mono.just(CUST_ONE_PHONE_ONE));
        when(repository.save(CUST_ONE_PHONE_ONE)).thenReturn(Mono.just(CUST_ONE_PHONE_ONE));

        StepVerifier
                .create(phoneNumberService.updateFor(phoneId, newStatus))
                // verifying same state instance
                .assertNext(phone -> assertEquals(newStatus, phone.isActive()))
                .verifyComplete();
    }
}

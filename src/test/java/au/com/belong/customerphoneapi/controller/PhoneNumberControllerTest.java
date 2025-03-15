package au.com.belong.customerphoneapi.controller;


import au.com.belong.customerphoneapi.exception.ResourceNotFoundException;
import au.com.belong.customerphoneapi.exception.ResourceStateConflictException;
import au.com.belong.customerphoneapi.service.PhoneNumberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static au.com.belong.customerphoneapi.PhoneNumberFixtures.CUST_ONE_PHONE_ONE;
import static au.com.belong.customerphoneapi.PhoneNumberFixtures.CUST_ONE_PHONE_TWO;
import static au.com.belong.customerphoneapi.PhoneNumberFixtures.getPhoneNumbersAsFluxForCustomer;
import static au.com.belong.customerphoneapi.PhoneNumberFixtures.getPhoneNumbersAsPageFor;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(PhoneNumberController.class)
class PhoneNumberControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PhoneNumberService phoneNumberService;

    public static Stream<Arguments> getPaginationInputs() {
        return Stream.of(
                Arguments.of(0, 3, 3), // min(5 - (3 * 0), 3) min(5, 3) = 3
                Arguments.of(1, 3, 2), // min(5 - (3 * 1), 3) = min(2, 3) = 2
                Arguments.of(0, 5, 5),// min(5 - (5 * 0), 5) min(5, 5) = 5
                Arguments.of(1, 5, 0), // min(5 - (5 * 1), 5) min(0, 5) = 0
                Arguments.of(10, 5, 0) // Pagination beyond available data (empty list)
        );
    }

    public static Stream<Arguments> getCustomerIdInputs() {
        return Stream.of(
                Arguments.of(1, 2),
                Arguments.of(2, 2),
                Arguments.of(3, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("getPaginationInputs")
    void testing_get_phone_numbers_with_valid_explicit_pagination_parameter_values_returns_ok_response(int page, int size, int expectedRecordsLength) {
        when(phoneNumberService.getAllFor(page, size)).thenReturn(getPhoneNumbersAsPageFor(page, size));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/phone-numbers")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                )  // Path
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.content.length()").isEqualTo(expectedRecordsLength);
    }

    @Test
    void testing_get_phone_numbers_with_valid_default_parameter_values_returns_ok_response() {
        when(phoneNumberService.getAllFor(anyInt(), anyInt())).thenReturn(getPhoneNumbersAsPageFor(0, 10));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/phone-numbers")
                        .build()
                )  // Path
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.content.length()").isEqualTo(5);

        verify(phoneNumberService).getAllFor(0, 10);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void testing_get_phone_numbers_with_invalid_size_pagination_parameter_value_returns_bad_request_response(int size) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/phone-numbers")
                        .queryParam("size", size)
                        .build()
                )  // Path
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation errors found.")
                .jsonPath("$.errors.length()").isEqualTo(1)
                .jsonPath("$.errors[0]").isEqualTo("Page size should be grater than or equal to one.");

        verify(phoneNumberService, never()).getAllFor(anyInt(), anyInt());
    }

    @Test
    void testing_get_phone_numbers_with_invalid_page_pagination_parameter_value_returns_bad_request_response() {
        webTestClient.get()
                .uri("/phone-numbers?page=-1")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation errors found.")
                .jsonPath("$.errors.length()").isEqualTo(1)
                .jsonPath("$.errors[0]").isEqualTo("Page number should be grater than or equal to zero.");

        verify(phoneNumberService, never()).getAllFor(anyInt(), anyInt());
    }

    @ParameterizedTest
    @MethodSource("getCustomerIdInputs")
    void testing_get_phone_numbers_by_customer_id_with_valid_records_in_database_returns_ok_response(long customerId, int expectedRecordsLength) {
        when(phoneNumberService.getAllFor(customerId)).thenReturn(getPhoneNumbersAsFluxForCustomer(customerId));

        webTestClient.get()
                .uri("/phone-numbers/customers/" + customerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(expectedRecordsLength);
    }

    @Test
    void testing_get_phone_numbers_by_customer_id_with_records_not_found_in_database_returns_bad_request_response() {
        long customerId = 9999;
        when(phoneNumberService.getAllFor(customerId)).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/phone-numbers/customers/" + customerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void testing_get_phone_numbers_by_customer_id_with_invalid_customer_id_format_returns_bad_request_response() {
        webTestClient.get()
                .uri("/phone-numbers/customers/one")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation errors found.")
                .jsonPath("$.errors.length()").isEqualTo(1)
                .jsonPath("$.errors[0]").isEqualTo("Customer ID must be a numeric.");

        verify(phoneNumberService, never()).getAllFor(anyInt());
    }

    @Test
    void testing_activate_phone_numbers_with_valid_input_returns_ok_response() {
        long phoneId = 1;

        when(phoneNumberService.updateFor(anyLong(), anyBoolean())).thenReturn(Mono.just(CUST_ONE_PHONE_ONE));

        webTestClient.patch()
                .uri("/phone-numbers/1/activation")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.active").isEqualTo(true);

        verify(phoneNumberService).updateFor(phoneId, true);
    }

    @Test
    void testing_activate_phone_numbers_with_phone_number_not_found_in_database() {
        long phoneId = 1;

        when(phoneNumberService.updateFor(anyLong(), anyBoolean())).thenReturn(Mono.error(new ResourceNotFoundException("Phone number not found.")));

        webTestClient.patch()
                .uri("/phone-numbers/1/activation")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Phone number not found.");

        verify(phoneNumberService).updateFor(phoneId, true);
    }

    @Test
    void testing_activate_phone_numbers_with_phone_number_already_in_designed_state_returns_state_conflict_response() {
        long phoneId = 1;

        when(phoneNumberService.updateFor(anyLong(), anyBoolean())).thenReturn(Mono.error(new ResourceStateConflictException("Phone number is already in the desired state.")));

        webTestClient.patch()
                .uri("/phone-numbers/1/activation")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Phone number is already in the desired state.");

        verify(phoneNumberService).updateFor(phoneId, true);
    }

    @Test
    void testing_activate_with_invalid_phone_number_id_format_returns_bad_request_response() {
        webTestClient.patch()
                .uri("/phone-numbers/one/activation")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation errors found.")
                .jsonPath("$.errors.length()").isEqualTo(1)
                .jsonPath("$.errors[0]").isEqualTo("Phone number ID must be a numeric.");

        verify(phoneNumberService, never()).updateFor(anyLong(), anyBoolean());
    }

    @Test
    void testing_deactivate_phone_numbers_with_valid_input_returns_ok_response() {
        long phoneId = 1;

        when(phoneNumberService.updateFor(anyLong(), anyBoolean())).thenReturn(Mono.just(CUST_ONE_PHONE_TWO));

        webTestClient.patch()
                .uri("/phone-numbers/1/deactivation")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.active").isEqualTo(false);

        verify(phoneNumberService).updateFor(phoneId, false);
    }

    @Test
    void testing_deactivate_phone_numbers_with_phone_number_not_found_in_database_returns_not_found_response() {
        long phoneId = 1;

        when(phoneNumberService.updateFor(anyLong(), anyBoolean())).thenReturn(Mono.error(new ResourceNotFoundException("Phone number not found.")));

        webTestClient.patch()
                .uri("/phone-numbers/1/deactivation")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Phone number not found.");

        verify(phoneNumberService).updateFor(phoneId, false);
    }

    @Test
    void testing_deactivate_phone_numbers_with_phone_number_already_in_designed_state_returns_state_conflict_response() {
        long phoneId = 1;

        when(phoneNumberService.updateFor(anyLong(), anyBoolean())).thenReturn(Mono.error(new ResourceStateConflictException("Phone number is already in the desired state.")));

        webTestClient.patch()
                .uri("/phone-numbers/1/deactivation")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Phone number is already in the desired state.");

        verify(phoneNumberService).updateFor(phoneId, false);
    }

    @Test
    void testing_deactivate_with_invalid_phone_number_id_format_returns_bad_request_response() {
        webTestClient.patch()
                .uri("/phone-numbers/one/deactivation")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation errors found.")
                .jsonPath("$.errors.length()").isEqualTo(1)
                .jsonPath("$.errors[0]").isEqualTo("Phone number ID must be a numeric.");

        verify(phoneNumberService, never()).updateFor(anyLong(), anyBoolean());
    }
}

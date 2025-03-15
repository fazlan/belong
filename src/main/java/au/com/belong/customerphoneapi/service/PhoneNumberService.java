package au.com.belong.customerphoneapi.service;

import au.com.belong.customerphoneapi.domain.PhoneNumber;
import au.com.belong.customerphoneapi.dto.PageDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PhoneNumberService {

    /**
     * Get all phone numbers for a given customer.
     *
     * @param customerId The customer identifier.
     * @return The phone numbers associated with the customer identifier.
     */
    Flux<PhoneNumber> getAllFor(long customerId);

    /**
     * Get all phone numbers with pagination bases on the pagination request parameters.
     *
     * @param page The page number.
     * @param size The page size.
     * @return The paginated phone numbers.
     */
    Mono<PageDTO<PhoneNumber>> getAllFor(int page, int size);

    /**
     * Activates (true) or deactivates (false) a given phone number.
     *
     * @param phoneId   The phone number identifier.
     * @param status The activated/deactivated status.
     * @return The updated phone number.
     */
    Mono<PhoneNumber> updateFor(long phoneId, boolean status);
}

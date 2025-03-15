package au.com.belong.customerphoneapi.controller;

import au.com.belong.customerphoneapi.domain.PhoneNumber;
import au.com.belong.customerphoneapi.dto.PageDTO;
import au.com.belong.customerphoneapi.service.PhoneNumberService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/phone-numbers")
public class PhoneNumberController {

    private final PhoneNumberService service;

    /**
     * Get all phone numbers with pagination bases on the pagination request parameters.
     *
     * <pre>
     * curl http://localhost:8080/phone-numbers?page=0&size=9
     * </pre>
     *
     * <pre>
     * {
     *   "content": [
     *     {
     *       "id": 1,
     *       "phone_number": "0488992263",
     *       "activated": true,
     *       "customer_id": 1
     *     },
     *     {
     *       "id": 2,
     *       "phone_number": "0422338899",
     *       "activated": false,
     *       "customer_id": 1
     *     },
     *     {
     *       "id": 3,
     *       "phone_number": "0412772652",
     *       "activated": false,
     *       "customer_id": 1
     *     },
     *     {
     *       "id": 4,
     *       "phone_number": "0482746027",
     *       "activated": false,
     *       "customer_id": 2
     *     }
     *   ],
     *   "page": 0,
     *   "size": 9,
     *   "last": true,
     *   "total_pages": 1,
     *   "total_records": 4
     * }
     * </pre>
     *
     * @param page The current page number.
     * @param size The size of the page.
     * @return Successful - The paginated phone number response based on the pagination request parameters.
     * <br/>
     * Unsuccessful - The error details with message.
     */
    @GetMapping
    public Mono<PageDTO<PhoneNumber>> getAllFor(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number should be grater than or equal to zero.")
            int page,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Page size should be grater than or equal to one.")
            int size) {

        Mono<PageDTO<PhoneNumber>> response = service.getAllFor(page, size);
        return response;
    }

    /**
     * Get all phone numbers for a given customer.
     * <pre>
     * curl http://localhost:8080/phone-numbers/customers/1
     * </pre>
     * <pre>
     * [
     *   {
     *     "id": 1,
     *     "phone_number": "0488992263",
     *     "activated": true,
     *     "customer_id": 1
     *   },
     *   {
     *     "id": 2,
     *     "phone_number": "0422338899",
     *     "activated": false,
     *     "customer_id": 1
     *   },
     *   {
     *     "id": 3,
     *     "phone_number": "0412772652",
     *     "activated": false,
     *     "customer_id": 1
     *   }
     * ]
     * </pre>
     *
     * @param customerId The customer identifier.
     * @return Successful - The phone numbers associated with the customer identifier.
     * <br/>
     * Unsuccessful - The error details with message.
     */
    @GetMapping("/customers/{customer_id}")
    public Flux<PhoneNumber> getAllFor(
            @PathVariable("customer_id")
            @Pattern(regexp = "\\d+", message = "Customer ID must be a numeric.")
            String customerId) {
        return service.getAllFor(Long.parseLong(customerId));
    }

    /**
     * Activates a given phone number.
     *
     * <pre>
     * curl http://localhost:8080/phone-numbers/1/activation
     * </pre>
     * <pre>
     *   {
     *     "id": 1,
     *     "phone_number": "0488992263",
     *     "activated": true,
     *     "customer_id": 1
     *   }
     * </pre>
     * @param phoneNumberId The phone number identifier to activate.
     * @return Successful - The updated phone number details.
     * <br/>
     * Unsuccessful - The error details with message.
     */
    // Activates (true) or deactivates (false) a given phone number
    @PatchMapping("/{phone_number_id}/activation")
    public Mono<PhoneNumber> activate(
            @PathVariable("phone_number_id")
            @Pattern(regexp = "\\d+", message = "Phone number ID must be a numeric.")
            String phoneNumberId) {
        return service.updateFor(Long.parseLong(phoneNumberId), true);
    }

    /**
     * Deactivates a given phone number.
     *
     * <pre>
     * curl http://localhost:8080/phone-numbers/1/deactivation
     * </pre>
     * <pre>
     *   {
     *     "id": 1,
     *     "phone_number": "0488992263",
     *     "activated": false,
     *     "customer_id": 1
     *   }
     * </pre>
     * @param phoneNumberId The phone number identifier to deactivate.
     * @return Successful - The updated phone number details.
     * <br/>
     * Unsuccessful - The error details with message.
     */
    // Activates (true) or deactivates (false) a given phone number
    @PatchMapping("/{phone_number_id}/deactivation")
    public Mono<PhoneNumber> deactivate(
            @PathVariable("phone_number_id")
            @Pattern(regexp = "\\d+", message = "Phone number ID must be a numeric.")
            String phoneNumberId) {
        return service.updateFor(Long.parseLong(phoneNumberId), false);
    }
}
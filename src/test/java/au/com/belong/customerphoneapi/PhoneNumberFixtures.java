package au.com.belong.customerphoneapi;

import au.com.belong.customerphoneapi.domain.PhoneNumber;
import au.com.belong.customerphoneapi.dto.PageDTO;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PhoneNumberFixtures {

    public static final PhoneNumber CUST_ONE_PHONE_ONE, CUST_ONE_PHONE_TWO, CUST_TWO_PHONE_ONE, CUST_TWO_PHONE_TWO, CUST_THREE_PHONE_ONE;
    private static final Flux<PhoneNumber> ALL_PHONE_NUMBERS;

    static {
        CUST_ONE_PHONE_ONE = createPhoneNumber(1L, "0411993721", true, 1L);
        CUST_ONE_PHONE_TWO = createPhoneNumber(2L, "0411566778", false, 1L);
        CUST_TWO_PHONE_ONE = createPhoneNumber(3L, "0422993721", false, 2L);
        CUST_TWO_PHONE_TWO = createPhoneNumber(4L, "0422566778", true, 2L);
        CUST_THREE_PHONE_ONE = createPhoneNumber(5L, "0433993721", true, 3L);

        ALL_PHONE_NUMBERS = Flux.just(CUST_ONE_PHONE_ONE, CUST_ONE_PHONE_TWO, CUST_TWO_PHONE_ONE, CUST_TWO_PHONE_TWO, CUST_THREE_PHONE_ONE);
    }

    private static PhoneNumber createPhoneNumber(long phoneId, String phoneNumber, boolean active, long customerId) {
        PhoneNumber phone = new PhoneNumber();
        phone.setId(phoneId);
        phone.setActive(active);
        phone.setCustomerId(customerId);
        phone.setPhoneNumber(phoneNumber);
        return phone;
    }

    private PhoneNumberFixtures() {
    }

    public static Flux<PhoneNumber> getPhoneNumbersAsFluxFor(int size) {
        return ALL_PHONE_NUMBERS.take(Math.min(size, 5));
    }

    public static Flux<PhoneNumber> getPhoneNumbersAsFluxForCustomer(long customerId) {
        return ALL_PHONE_NUMBERS.filter(phone -> phone.getCustomerId() == customerId);
    }

    public static Mono<PageDTO<PhoneNumber>> getPhoneNumbersAsPageFor(int page, int size) {
        return ALL_PHONE_NUMBERS
                .take(Math.min(Math.max(0, 5 - (page * size)), size))
                .collectList()
                .map(phones -> PageDTO.of(new PageImpl<>(phones, PageRequest.of(page, size), phones.size())));
    }
}

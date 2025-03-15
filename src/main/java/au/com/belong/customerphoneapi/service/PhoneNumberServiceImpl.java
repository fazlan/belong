package au.com.belong.customerphoneapi.service;

import au.com.belong.customerphoneapi.domain.PhoneNumber;
import au.com.belong.customerphoneapi.dto.PageDTO;
import au.com.belong.customerphoneapi.exception.ResourceStateConflictException;
import au.com.belong.customerphoneapi.repository.PhoneNumberRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PhoneNumberServiceImpl extends BaseCrudService<PhoneNumber, PhoneNumberRepository> implements PhoneNumberService {

    public PhoneNumberServiceImpl(PhoneNumberRepository repository) {
        super(repository);
    }

    @Override
    public Mono<PageDTO<PhoneNumber>> getAllFor(int page, int size) {
        return findAll(page, size);
    }

    @Override
    public Flux<PhoneNumber> getAllFor(long customerId) {
        return getRepository().findByCustomerId(customerId);
    }

    /**
     * Updates the phone number activate/deactivate status.
     * If the current state is same as requested, then throw a au.com.belong.customerphoneapi.exception.ResourceStateConflictException exception.
     *
     * @param phoneId   The phone number identifier.
     * @param status The activate/deactivate status.
     * @return The updated phone number.
     */
    @Override
    public Mono<PhoneNumber> updateFor(long phoneId, boolean status) {
        return update(phoneId, phoneNumber -> {
            if (phoneNumber.isActive() == status) {
                return Mono.error(new ResourceStateConflictException("Phone number is already in the desired state."));
            }
            phoneNumber.setActive(status);
            return Mono.just(phoneNumber);
        });
    }
}

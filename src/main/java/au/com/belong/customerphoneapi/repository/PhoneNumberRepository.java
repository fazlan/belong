package au.com.belong.customerphoneapi.repository;

import au.com.belong.customerphoneapi.domain.PhoneNumber;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PhoneNumberRepository extends BaseCrudRepository<PhoneNumber> {
    Flux<PhoneNumber> findByCustomerId(long customerId);
}
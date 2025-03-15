package au.com.belong.customerphoneapi.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

@NoRepositoryBean
public interface BaseCrudRepository<T> extends ReactiveCrudRepository<T, Long> {
    Flux<T> findAllBy(PageRequest pageRequest);
}

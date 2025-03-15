package au.com.belong.customerphoneapi.service;

import au.com.belong.customerphoneapi.dto.PageDTO;
import au.com.belong.customerphoneapi.exception.ResourceNotFoundException;
import au.com.belong.customerphoneapi.repository.BaseCrudRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public abstract class BaseCrudService<T, R extends BaseCrudRepository<T>> {
    private final R repository;

    /**
     * Returns all the matched rows records with a paginated response based on pagination request parameters.
     *
     * @param page The current page number.
     * @param size The current page size.
     * @return All The matched table rows for the given page number and size.
     */
    protected Mono<PageDTO<T>> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Mono<Long> totalEntityCount = repository.count();
        Mono<List<T>> paginatedEntities = repository.findAllBy(pageRequest).collectList();
        return paginatedEntities
                .zipWith(totalEntityCount, (content, count) -> new PageImpl<>(content, pageRequest, count))
                .map(PageDTO::of);
    }

    /**
     * Updates a table record identified by the id with the <code>updateFn</code> lambda function.
     *
     * @param id       The table record identifier.
     * @param updateFn The lambda function to execute and update the matching table row.
     * @return The updated table record.
     */
    protected Mono<T> update(long id, Function<T, Mono<T>> updateFn) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Record not found with ID " + id)))
                .flatMap(updateFn)
                .flatMap(repository::save);
    }
}


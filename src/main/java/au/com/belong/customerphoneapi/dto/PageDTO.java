package au.com.belong.customerphoneapi.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;


@Getter
@Builder
public class PageDTO<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final boolean last;
    private final long totalPages;
    private final long totalRecords;

    public static <R> PageDTO<R> of(Page<R> page) {
        return PageDTO
                .<R>builder()
                .last(page.isLast())
                .size(page.getSize())
                .page(page.getNumber())
                .content(page.getContent())
                .totalPages(page.getTotalPages())
                .totalRecords(page.getTotalElements())
                .build();
    }
}

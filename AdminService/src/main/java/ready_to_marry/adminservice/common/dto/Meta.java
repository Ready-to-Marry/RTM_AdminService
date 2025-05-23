package ready_to_marry.adminservice.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Meta {
    private int page;
    private int size;
    private long total;
    private int totalPages;

    public static Meta of(int page, int size, long total) {
        int totalPages = (int) Math.ceil((double) total / size);
        return Meta.builder()
                .page(page)
                .size(size)
                .total(total)
                .totalPages(totalPages)
                .build();
    }
}
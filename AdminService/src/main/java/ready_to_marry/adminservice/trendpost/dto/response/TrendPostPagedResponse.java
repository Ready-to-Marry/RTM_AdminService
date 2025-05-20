package ready_to_marry.adminservice.trendpost.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TrendPostPagedResponse {
    private List<TrendPostDTO> items;
    private int page;
    private int size;
    private long total;
}
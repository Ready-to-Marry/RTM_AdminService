package ready_to_marry.adminservice.profile.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminIdResponse {

    @JsonProperty("admin_id")
    private Long adminId;
}

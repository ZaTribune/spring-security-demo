package account.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordResponse {

    private String email;

    @JsonProperty(defaultValue = "The password has been updated successfully")
    private String status;
}

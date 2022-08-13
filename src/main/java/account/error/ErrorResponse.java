package account.error;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private String timestamp;
    private Integer status;
    private String error;

    private String message;
    private String path;
}

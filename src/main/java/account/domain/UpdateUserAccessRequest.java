package account.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UpdateUserAccessRequest {


    private String user;
    private Operation operation;

    public enum Operation {
        LOCK, UNLOCK
    }

}

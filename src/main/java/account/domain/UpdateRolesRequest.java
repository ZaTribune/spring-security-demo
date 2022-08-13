package account.domain;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UpdateRolesRequest {

    private String user;
    private String role;
    private Operation operation;
}

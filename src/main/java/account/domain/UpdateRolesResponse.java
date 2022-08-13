package account.domain;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRolesResponse {

    private Long id;
    private String name;
    private String lastname;
    private String email;
    private Object roles;
}

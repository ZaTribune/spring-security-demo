package account.domain;


import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String lastname;

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    private Object roles;

}

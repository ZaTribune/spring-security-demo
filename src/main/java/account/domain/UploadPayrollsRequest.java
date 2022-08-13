package account.domain;


import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadPayrollsRequest {

    @NotBlank
    private String employee;

    private String period;

    @Min(0)
    @NotNull
    private Long salary;
}

package account.domain;


import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

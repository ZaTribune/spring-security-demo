package account.domain;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPayrollResponse {
    private String name;
    private String lastname;
    private String period;
    private String salary;
}

package account.db.entity;


import account.db.converter.YearMonthConverter;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Convert(converter = YearMonthConverter.class)
    @DateTimeFormat(pattern = "MM-yyyy")
    private YearMonth period;

    private Long salary;

    @ManyToOne
    @JoinColumn(name = "app_use_id",referencedColumnName = "id")
    private AppUser appUser;
}

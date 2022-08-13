package account.db.repository;


import account.db.entity.AppUser;
import account.db.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.YearMonth;
import java.util.List;
import java.util.Optional;


public interface PayrollsRepository extends JpaRepository<Payroll, Long> {

    Optional<Payroll>findPayrollByPeriod(YearMonth period);
    Optional<Payroll>findPayrollByAppUserAndPeriod(AppUser appUser, YearMonth period);
    List<Payroll>findAllByAppUserOrderByPeriodDesc(AppUser appUser);

}

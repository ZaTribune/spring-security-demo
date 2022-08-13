package account.service;

import account.db.entity.AppUser;
import account.domain.GetPayrollResponse;
import account.domain.UploadPayrollsRequest;
import account.domain.UploadPayrollsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public interface PayrollsService {

    Logger LOGGER = Logger.getLogger(PayrollsService.class.getSimpleName());

    UploadPayrollsResponse addPayrolls(List<UploadPayrollsRequest> uploadPayrollsRequestList);

    UploadPayrollsResponse updatePayroll(UploadPayrollsRequest updateRequest);

    GetPayrollResponse getPayrollByPeriod(AppUser appUser, String period);

    List<GetPayrollResponse> getPayrolls(AppUser appUser);

    List<GetPayrollResponse> getAllPayrolls();

    default ResponseStatusException createException(HttpStatus status, String message) {
        LOGGER.log(Level.WARNING, message);
        throw new ResponseStatusException(status, message);
    }

    default String adjustSalary(Long salary) {
        String dollars;
        String cents;
        String salaryString = String.valueOf(salary);
        if (salary < 100) {
            dollars = "0";
            cents = salaryString;
        } else {
            dollars = salaryString.substring(0, salaryString.length() - 2);
            if (salary % 10 == 0 && salary > 100)
                cents = "0";
            else
                cents = salaryString.substring(dollars.length());
        }
        return String.format("%s dollar(s) %s cent(s)", dollars, cents);

    }
}

package account.controller.api;


import account.db.entity.AppUser;
import account.domain.GetPayrollResponse;
import account.domain.UploadPayrollsRequest;
import account.domain.UploadPayrollsResponse;
import account.service.PayrollsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api")
@AllArgsConstructor
@RestController
public class EmployersController {

    private PayrollsService payrollsService;

    @PostMapping("/acct/payments")
    public UploadPayrollsResponse uploadPayrolls(@AuthenticationPrincipal AppUser appUser,@RequestBody List<UploadPayrollsRequest> payrollsRequests) {
        log.info("upload payrolls [{}] - {}",payrollsRequests,appUser);
        return payrollsService.addPayrolls(payrollsRequests);
    }

    @PutMapping("/acct/payments")
    public UploadPayrollsResponse updatePayment(@AuthenticationPrincipal AppUser appUser,@RequestBody UploadPayrollsRequest uploadPayrollsRequest) {
        log.info("update payment [{}] - {}",uploadPayrollsRequest,appUser);
        return payrollsService.updatePayroll(uploadPayrollsRequest);
    }

    @GetMapping("/empl/payment")
    public Object getPayment(@AuthenticationPrincipal AppUser appUser,@RequestParam(name = "period",defaultValue = "")String period) {
        log.info("get payment for period [{}] - {}",period,appUser);
        if (!period.isEmpty())
              return payrollsService.getPayrollByPeriod(appUser,period);
        else
            return payrollsService.getPayrolls(appUser);
    }

    @GetMapping("/acct/payments/all")
    public List<GetPayrollResponse> getPayrolls() {
        log.info("get payrolls");
        return payrollsService.getAllPayrolls();
    }


}

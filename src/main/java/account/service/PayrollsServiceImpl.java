package account.service;

import account.db.entity.AppUser;
import account.db.entity.Payroll;
import account.db.repository.PayrollsRepository;
import account.db.repository.UserRepository;
import account.domain.GetPayrollResponse;
import account.domain.UploadPayrollsRequest;
import account.domain.UploadPayrollsResponse;
import account.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class PayrollsServiceImpl implements PayrollsService {


    private final PayrollsRepository payrollsRepository;

    private final UserRepository userRepository;

    private final DateUtils dateUtils = new DateUtils();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");

    @Autowired
    public PayrollsServiceImpl(PayrollsRepository payrollsRepository, UserRepository userRepository) {
        this.payrollsRepository = payrollsRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UploadPayrollsResponse addPayrolls(List<UploadPayrollsRequest> uploadPayrollsRequestList) {
        log.info("adding payrolls");
        List<Payroll> list = uploadPayrollsRequestList
                .stream()
                .map(dto -> {

                    if (!dateUtils.validateRequestFormat(dto.getPeriod()))
                        throw createException(HttpStatus.BAD_REQUEST, "Invalid period!");

                    if (dto.getSalary() < 0)
                        throw createException(HttpStatus.BAD_REQUEST, "Salary cannot be a negative value!");

                    AppUser appUser = userRepository.findAppUserByUsernameIgnoreCase(dto.getEmployee())
                            .orElseThrow(() -> createException(HttpStatus.BAD_REQUEST, "User Not Found!"));


                    payrollsRepository.findPayrollByAppUserAndPeriod(appUser, YearMonth.parse(dto.getPeriod(), formatter))
                            .ifPresent(p -> {
                                        throw createException(HttpStatus.BAD_REQUEST,
                                                String.format("A payroll within this period [%s] already Exists! - user:[%s]",
                                                        dto.getPeriod(), dto.getEmployee()));
                                    }
                            );
                    return Payroll.builder()
                            .appUser(appUser)
                            .period(YearMonth.parse(dto.getPeriod(), formatter))//already validated
                            .salary(dto.getSalary())
                            .build();
                })
                .collect(Collectors.toList());

        payrollsRepository.saveAll(list);
        return UploadPayrollsResponse.builder()
                .status("Added successfully!")
                .build();
    }

    @Override
    public UploadPayrollsResponse updatePayroll(UploadPayrollsRequest updateRequest) {
        log.info("update payroll [{}] for employee: [{}]", updateRequest.getPeriod(), updateRequest.getEmployee());
        //todo:should return object
        if (!dateUtils.validateRequestFormat(updateRequest.getPeriod()))
            throw createException(HttpStatus.BAD_REQUEST, "Invalid period!");

        AppUser appUser = userRepository.findAppUserByUsernameIgnoreCase(updateRequest.getEmployee())
                .orElseThrow(() -> createException(HttpStatus.BAD_REQUEST, "User Not Found!"));

        Payroll payroll = payrollsRepository.findPayrollByAppUserAndPeriod(appUser, YearMonth.parse(updateRequest.getPeriod(), formatter))
                .orElseThrow(() -> createException(HttpStatus.BAD_REQUEST, "Payroll Not Found!"));

        if (updateRequest.getSalary() < 0)
            throw createException(HttpStatus.BAD_REQUEST, "Salary cannot be a negative value!");

        payroll.setSalary(updateRequest.getSalary());

        payrollsRepository.save(payroll);

        return UploadPayrollsResponse.builder()
                .status("Updated successfully!")
                .build();
    }

    @Override
    public GetPayrollResponse getPayrollByPeriod(AppUser appUser, String period) {
        log.info("get payroll on period [{}] for employee: [{}]", period, appUser.getUsername());

        if (!dateUtils.validateRequestFormat(period))
            throw createException(HttpStatus.BAD_REQUEST, "Invalid period!");

        Payroll payroll = payrollsRepository.findPayrollByAppUserAndPeriod(appUser, YearMonth.parse(period, formatter))
                .orElseThrow(() -> createException(HttpStatus.BAD_REQUEST, "Payroll Not Found!"));


        return GetPayrollResponse.builder()
                .period(dateUtils.adjustResponseFormat(payroll.getPeriod()))
                .name(payroll.getAppUser().getName())
                .lastname(payroll.getAppUser().getLastname())
                .salary(adjustSalary(payroll.getSalary()))
                .build();
    }

    @Override
    public List<GetPayrollResponse> getPayrollByPeriod(String period) {
        log.info("get payrolls on period: [{}]", period);
        if (!dateUtils.validateRequestFormat(period))
            throw createException(HttpStatus.BAD_REQUEST, "Invalid period!");

        return payrollsRepository.findAllByPeriod(YearMonth.parse(period, formatter))
                .stream()
                .map(entity ->
                        GetPayrollResponse.builder()
                                .name(entity.getAppUser().getName())
                                .lastname(entity.getAppUser().getLastname())
                                .salary(adjustSalary(entity.getSalary()))
                                .period(dateUtils.adjustResponseFormat(entity.getPeriod()))
                                .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<GetPayrollResponse> getPayrolls(AppUser appUser) {
        log.info("get payrolls for employee: [{}]", appUser.getUsername());
        return payrollsRepository.findAllByAppUserOrderByPeriodDesc(appUser)
                .stream()
                .map(entity ->
                        GetPayrollResponse.builder()
                                .name(appUser.getName())
                                .lastname(appUser.getLastname())
                                .salary(adjustSalary(entity.getSalary()))
                                .period(dateUtils.adjustResponseFormat(entity.getPeriod()))
                                .build()

                )
                .collect(Collectors.toList());
    }

    public List<GetPayrollResponse> getAllPayrolls() {

        return payrollsRepository.findAll()
                .stream().map(payroll -> GetPayrollResponse.builder()
                        .period(dateUtils.adjustResponseFormat(payroll.getPeriod()))
                        .name(payroll.getAppUser().getName())
                        .lastname(payroll.getAppUser().getLastname())
                        .salary(String.valueOf(payroll.getSalary()))
                        .build())
                .collect(Collectors.toList());
    }

}

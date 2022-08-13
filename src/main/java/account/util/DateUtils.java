package account.util;


import lombok.extern.slf4j.Slf4j;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateUtils {

    private final DateTimeFormatter toFormat;

    private final DateTimeFormatter requestFormat;
    public DateUtils() {
        toFormat = DateTimeFormatter.ofPattern("MMMM-yyyy");
        requestFormat = DateTimeFormatter.ofPattern("MM-yyyy");
    }


    public boolean validateRequestFormat(String dateStr) {

        try {
            YearMonth.parse(dateStr,requestFormat);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String adjustResponseFormat(YearMonth date) {
        return toFormat.format(date);
    }
}

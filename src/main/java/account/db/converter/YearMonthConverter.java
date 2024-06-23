package account.db.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;


@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-yyyy");

    @Override
    public String convertToDatabaseColumn(YearMonth attribute) {
        return (attribute == null ? null : attribute.format(FORMATTER));
    }

    @Override
    public YearMonth convertToEntityAttribute(String dbData) {
        return (dbData == null ? null : YearMonth.parse(dbData, FORMATTER));
    }
}

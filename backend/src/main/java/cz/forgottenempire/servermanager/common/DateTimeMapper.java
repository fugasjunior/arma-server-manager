package cz.forgottenempire.servermanager.common;

import java.time.LocalDateTime;
import java.time.LocalTime;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DateTimeMapper {

    default String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toString() : null;
    }

    default String localTimeToString(LocalTime time) {
        return time != null ? time.toString() : null;
    }

    default LocalTime stringToLocalTime(String time) {
        return time != null && !time.isBlank() ? LocalTime.parse(time) : null;
    }
}

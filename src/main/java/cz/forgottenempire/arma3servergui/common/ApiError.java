package cz.forgottenempire.arma3servergui.common;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ApiError {
        private HttpStatus status;
        private String message;
        private List<String> errors;
}

package vbm.medrelais.webapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ApiError {

    public ApiError(String message) { this.message = message; }

    @Getter
    @AllArgsConstructor
    public enum APITypeError {

        ACCESS_DENIED("AccessDeniedDefault"),
        FIELD_VALIDATION("FieldValidationDefault");

        private final String label;
    }

    private APITypeError typeError;
    private String message;
}

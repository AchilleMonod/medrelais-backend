package vbm.medrelais.webapp.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import vbm.medrelais.exception.AttributionNotFoundException;
import vbm.medrelais.exception.CreneauNotFoundException;
import vbm.medrelais.exception.PraticienNotFoundException;
import vbm.medrelais.exception.RegleRecurrenceNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerAdviceTest {

    private final ApiExceptionHandlerAdvice advice = new ApiExceptionHandlerAdvice();

    // ─── Helper : MethodParameter factice pour MethodArgumentNotValidException ───

    /** Classe interne utilisée uniquement pour obtenir un MethodParameter via réflexion. */
    static class MethodHolder {
        @SuppressWarnings("unused")
        public void dummy(Object param) { /* utilisée uniquement pour obtenir un MethodParameter via réflexion */ }
    }

    private static MethodParameter dummyMethodParameter() throws NoSuchMethodException {
        return new MethodParameter(
                MethodHolder.class.getDeclaredMethod("dummy", Object.class), 0);
    }

    // ─── handlePraticienNotFound ──────────────────────────────────────────────

    @Test
    void handlePraticienNotFound_shouldReturnApiError() {
        PraticienNotFoundException ex = new PraticienNotFoundException(42L);

        ApiError result = advice.handlePraticienNotFound(ex);

        assertThat(result.getTypeError()).isEqualTo(ApiError.APITypeError.FIELD_VALIDATION);
        assertThat(result.getMessage()).contains("42");
    }

    // ─── handleCreneauNotFound ────────────────────────────────────────────────

    @Test
    void handleCreneauNotFound_shouldReturnApiError() {
        CreneauNotFoundException ex = new CreneauNotFoundException(7L);

        ApiError result = advice.handleCreneauNotFound(ex);

        assertThat(result.getTypeError()).isEqualTo(ApiError.APITypeError.FIELD_VALIDATION);
        assertThat(result.getMessage()).contains("7");
    }

    // ─── handleAttributionNotFound ────────────────────────────────────────────

    @Test
    void handleAttributionNotFound_shouldReturnApiError() {
        AttributionNotFoundException ex = new AttributionNotFoundException(3L);

        ApiError result = advice.handleAttributionNotFound(ex);

        assertThat(result.getTypeError()).isEqualTo(ApiError.APITypeError.FIELD_VALIDATION);
        assertThat(result.getMessage()).contains("3");
    }

    // ─── handleRegleRecurrenceNotFound ────────────────────────────────────────

    @Test
    void handleRegleRecurrenceNotFound_shouldReturnApiError() {
        RegleRecurrenceNotFoundException ex = new RegleRecurrenceNotFoundException(99L);

        ApiError result = advice.handleRegleRecurrenceNotFound(ex);

        assertThat(result.getTypeError()).isEqualTo(ApiError.APITypeError.FIELD_VALIDATION);
        assertThat(result.getMessage()).contains("99");
    }

    // ─── handleIllegalState ──────────────────────────────────────────────────

    @Test
    void handleIllegalState_shouldReturnApiErrorWithMessage() {
        IllegalStateException ex = new IllegalStateException("Le créneau n'est plus disponible");

        ApiError result = advice.handleIllegalState(ex);

        assertThat(result.getTypeError()).isEqualTo(ApiError.APITypeError.FIELD_VALIDATION);
        assertThat(result.getMessage()).isEqualTo("Le créneau n'est plus disponible");
    }

    // ─── handleValidation ─────────────────────────────────────────────────────

    @Test
    void handleValidation_withSingleFieldError_shouldReturnMessage() throws NoSuchMethodException {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "email", "format invalide"));
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(dummyMethodParameter(), bindingResult);

        ApiError result = advice.handleValidation(ex);

        assertThat(result.getTypeError()).isEqualTo(ApiError.APITypeError.FIELD_VALIDATION);
        assertThat(result.getMessage()).contains("email").contains("format invalide");
    }

    @Test
    void handleValidation_withMultipleFieldErrors_shouldConcatenateMessages() throws NoSuchMethodException {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "nom",   "ne doit pas être vide"));
        bindingResult.addError(new FieldError("target", "email", "format invalide"));
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(dummyMethodParameter(), bindingResult);

        ApiError result = advice.handleValidation(ex);

        assertThat(result.getMessage()).contains("nom").contains("email").contains("; ");
    }

    // ─── handleBadCredentials ─────────────────────────────────────────────────

    @Test
    void handleBadCredentials_shouldReturn401Message() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        ApiError result = advice.handleBadCredentials(ex);

        assertThat(result.getTypeError()).isEqualTo(ApiError.APITypeError.FIELD_VALIDATION);
        assertThat(result.getMessage()).isEqualTo("Email ou mot de passe incorrect");
    }

    // ─── handleAccessDenied ───────────────────────────────────────────────────

    @Test
    void handleAccessDenied_shouldReturn403Message() {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");

        ApiError result = advice.handleAccessDenied(ex);

        assertThat(result.getTypeError()).isEqualTo(ApiError.APITypeError.ACCESS_DENIED);
        assertThat(result.getMessage()).isEqualTo("Accès refusé");
    }

    // ─── handleUnexpected ────────────────────────────────────────────────────

    @Test
    void handleUnexpected_shouldReturnGenericMessage() {
        Exception ex = new RuntimeException("NullPointerException quelque part");

        ApiError result = advice.handleUnexpected(ex);

        assertThat(result.getTypeError()).isEqualTo(ApiError.APITypeError.FIELD_VALIDATION);
        assertThat(result.getMessage()).isEqualTo("Une erreur interne est survenue");
    }
}
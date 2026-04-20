package vbm.medrelais.webapp.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import vbm.medrelais.exception.BureauEtudesServiceException;
import vbm.medrelais.exception.ClientServiceException;
import vbm.medrelais.exception.DemandeDevisServiceException;
import vbm.medrelais.exception.EtudeServiceException;
import vbm.medrelais.exception.PropositionDevisServiceException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    // ─── handleBureauEtudesNotFound ───────────────────────────────────────────

    @Test
    void handleBureauEtudesNotFound_shouldReturnMessageAndFieldValidationType() {
        var ex = new BureauEtudesServiceException("Bureau introuvable");

        ApiError result = advice.handleBureauEtudesNotFound(ex);

        assertEquals("Bureau introuvable", result.getMessage());
        assertEquals(ApiError.APITypeError.FIELD_VALIDATION, result.getTypeError());
    }

    // ─── handleClientNotFound ─────────────────────────────────────────────────

    @Test
    void handleClientNotFound_shouldReturnMessageAndFieldValidationType() {
        var ex = new ClientServiceException("Client introuvable");

        ApiError result = advice.handleClientNotFound(ex);

        assertEquals("Client introuvable", result.getMessage());
        assertEquals(ApiError.APITypeError.FIELD_VALIDATION, result.getTypeError());
    }

    // ─── handleDemandeDevisNotFound ───────────────────────────────────────────

    @Test
    void handleDemandeDevisNotFound_shouldReturnMessageAndFieldValidationType() {
        var ex = new DemandeDevisServiceException("Demande introuvable");

        ApiError result = advice.handleDemandeDevisNotFound(ex);

        assertEquals("Demande introuvable", result.getMessage());
        assertEquals(ApiError.APITypeError.FIELD_VALIDATION, result.getTypeError());
    }

    // ─── handleEtudeNotFound ──────────────────────────────────────────────────

    @Test
    void handleEtudeNotFound_shouldReturnMessageAndFieldValidationType() {
        var ex = new EtudeServiceException("Étude introuvable");

        ApiError result = advice.handleEtudeNotFound(ex);

        assertEquals("Étude introuvable", result.getMessage());
        assertEquals(ApiError.APITypeError.FIELD_VALIDATION, result.getTypeError());
    }

    // ─── handlePropositionDevisNotFound ──────────────────────────────────────

    @Test
    void handlePropositionDevisNotFound_shouldReturnMessageAndFieldValidationType() {
        var ex = new PropositionDevisServiceException("Proposition introuvable");

        ApiError result = advice.handlePropositionDevisNotFound(ex);

        assertEquals("Proposition introuvable", result.getMessage());
        assertEquals(ApiError.APITypeError.FIELD_VALIDATION, result.getTypeError());
    }

    // ─── handleValidation ─────────────────────────────────────────────────────

    @Test
    void handleValidation_withMultipleFieldErrors_shouldAssembleMessage() throws NoSuchMethodException {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "nom",   "ne doit pas être vide"));
        bindingResult.addError(new FieldError("target", "email", "format invalide"));

        var ex = new MethodArgumentNotValidException(dummyMethodParameter(), bindingResult);

        ApiError result = advice.handleValidation(ex);

        assertEquals("nom : ne doit pas être vide; email : format invalide", result.getMessage());
        assertEquals(ApiError.APITypeError.FIELD_VALIDATION, result.getTypeError());
    }

    @Test
    void handleValidation_withNoFieldErrors_shouldReturnDefaultMessage() throws NoSuchMethodException {
        // Seule une erreur globale (pas de FieldError) → orElse("Données invalides")
        var bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.reject("error.global", "erreur globale");

        var ex = new MethodArgumentNotValidException(dummyMethodParameter(), bindingResult);

        ApiError result = advice.handleValidation(ex);

        assertEquals("Données invalides", result.getMessage());
        assertEquals(ApiError.APITypeError.FIELD_VALIDATION, result.getTypeError());
    }

    // ─── handleUnexpected ────────────────────────────────────────────────────

    @Test
    void handleUnexpected_shouldReturnGenericMessageAndNotExposeInternalDetails() {
        var ex = new RuntimeException("Erreur système critique - détails confidentiels");

        ApiError result = advice.handleUnexpected(ex);

        assertEquals("Une erreur interne est survenue", result.getMessage());
        assertEquals(ApiError.APITypeError.FIELD_VALIDATION, result.getTypeError());
    }
}
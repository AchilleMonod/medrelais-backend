package vbm.medrelais.webapp.exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vbm.medrelais.exception.AttributionNotFoundException;
import vbm.medrelais.exception.CreneauNotFoundException;
import vbm.medrelais.exception.PraticienNotFoundException;
import vbm.medrelais.exception.RegleRecurrenceNotFoundException;

@Hidden
@RestControllerAdvice
@Slf4j
public class ApiExceptionHandlerAdvice {

    @ExceptionHandler(PraticienNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handlePraticienNotFound(PraticienNotFoundException ex) {
        log.warn("Praticien introuvable : {}", ex.getMessage());
        return new ApiError(ApiError.APITypeError.FIELD_VALIDATION, ex.getMessage());
    }

    @ExceptionHandler(CreneauNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCreneauNotFound(CreneauNotFoundException ex) {
        log.warn("Créneau introuvable : {}", ex.getMessage());
        return new ApiError(ApiError.APITypeError.FIELD_VALIDATION, ex.getMessage());
    }

    @ExceptionHandler(AttributionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleAttributionNotFound(AttributionNotFoundException ex) {
        log.warn("Attribution introuvable : {}", ex.getMessage());
        return new ApiError(ApiError.APITypeError.FIELD_VALIDATION, ex.getMessage());
    }

    @ExceptionHandler(RegleRecurrenceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleRegleRecurrenceNotFound(RegleRecurrenceNotFoundException ex) {
        log.warn("Règle de récurrence introuvable : {}", ex.getMessage());
        return new ApiError(ApiError.APITypeError.FIELD_VALIDATION, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIllegalState(IllegalStateException ex) {
        log.warn("Conflit métier : {}", ex.getMessage());
        return new ApiError(ApiError.APITypeError.FIELD_VALIDATION, ex.getMessage());
    }

    /**
     * Validation des champs (@Valid / @NotBlank / @Email…) : retourne 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " : " + fe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Données invalides");
        log.warn("Erreur de validation : {}", message);
        return new ApiError(ApiError.APITypeError.FIELD_VALIDATION, message);
    }

    /**
     * Mauvaises credentials (login incorrect) : retourne 401.
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleBadCredentials(BadCredentialsException ex) {
        log.warn("Echec d'authentification : {}", ex.getMessage());
        return new ApiError(ApiError.APITypeError.FIELD_VALIDATION, "Email ou mot de passe incorrect");
    }

    /**
     * Accès refusé (rôle insuffisant) : retourne 403.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessDenied(AccessDeniedException ex) {
        log.warn("Accès refusé : {}", ex.getMessage());
        return new ApiError(ApiError.APITypeError.ACCESS_DENIED, "Accès refusé");
    }

    /**
     * Catch-all : toute exception non prévue.
     * ERROR car c'est un problème système non anticipé.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnexpected(Exception ex) {
        log.error("Erreur inattendue : {}", ex.getMessage(), ex);
        return new ApiError(ApiError.APITypeError.FIELD_VALIDATION, "Une erreur interne est survenue");
    }
}

package vbm.medrelais.webapp.exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vbm.medrelais.exception.BureauEtudesServiceException;
import vbm.medrelais.exception.ClientServiceException;
import vbm.medrelais.exception.DemandeDevisServiceException;
import vbm.medrelais.exception.EtudeServiceException;
import vbm.medrelais.exception.PropositionDevisServiceException;

@Hidden
@RestControllerAdvice
@Slf4j
public class ApiExceptionHandlerAdvice {

    /**
     * Bureau d'études introuvable.
     */
    @ExceptionHandler(BureauEtudesServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleBureauEtudesNotFound(BureauEtudesServiceException exception) {
        log.warn("Bureau d'études introuvable : {}", exception.getMessage());
        ApiError error = new ApiError(exception.getMessage());
        error.setTypeError(ApiError.APITypeError.FIELD_VALIDATION);
        return error;
    }

    /**
     * Client introuvable.
     */
    @ExceptionHandler(ClientServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleClientNotFound(ClientServiceException exception) {
        log.warn("Client introuvable : {}", exception.getMessage());
        ApiError error = new ApiError(exception.getMessage());
        error.setTypeError(ApiError.APITypeError.FIELD_VALIDATION);
        return error;
    }

    /**
     * Demande de devis introuvable.
     */
    @ExceptionHandler(DemandeDevisServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleDemandeDevisNotFound(DemandeDevisServiceException exception) {
        log.warn("Demande de devis introuvable : {}", exception.getMessage());
        ApiError error = new ApiError(exception.getMessage());
        error.setTypeError(ApiError.APITypeError.FIELD_VALIDATION);
        return error;
    }

    /**
     * Étude introuvable.
     */
    @ExceptionHandler(EtudeServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEtudeNotFound(EtudeServiceException exception) {
        log.warn("Étude introuvable : {}", exception.getMessage());
        ApiError error = new ApiError(exception.getMessage());
        error.setTypeError(ApiError.APITypeError.FIELD_VALIDATION);
        return error;
    }

    /**
     * Proposition de devis introuvable.
     */
    @ExceptionHandler(PropositionDevisServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handlePropositionDevisNotFound(PropositionDevisServiceException exception) {
        log.warn("Proposition de devis introuvable : {}", exception.getMessage());
        ApiError error = new ApiError(exception.getMessage());
        error.setTypeError(ApiError.APITypeError.FIELD_VALIDATION);
        return error;
    }

    /**
     * Validation des champs (@Valid / @NotBlank / @Email…) : retourne 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " : " + fe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Données invalides");
        log.warn("Erreur de validation : {}", message);
        ApiError error = new ApiError(message);
        error.setTypeError(ApiError.APITypeError.FIELD_VALIDATION);
        return error;
    }

    /**
     * Catch-all : toute exception non prévue.
     * ERROR car c'est un problème système non anticipé.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnexpected(Exception exception) {
        log.error("Erreur inattendue : {}", exception.getMessage(), exception);
        ApiError error = new ApiError("Une erreur interne est survenue");
        error.setTypeError(ApiError.APITypeError.FIELD_VALIDATION);
        return error;
    }
}

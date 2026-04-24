package vbm.medrelais.exception;

public class RegleRecurrenceNotFoundException extends RuntimeException {
    public RegleRecurrenceNotFoundException(Long id) {
        super("Règle de récurrence introuvable avec l'id : " + id);
    }
    public RegleRecurrenceNotFoundException(String message) {
        super(message);
    }
}


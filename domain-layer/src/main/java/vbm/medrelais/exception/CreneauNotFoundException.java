package vbm.medrelais.exception;

public class CreneauNotFoundException extends RuntimeException {
    public CreneauNotFoundException(Long id) {
        super("Créneau introuvable avec l'id : " + id);
    }
    public CreneauNotFoundException(String message) {
        super(message);
    }
}


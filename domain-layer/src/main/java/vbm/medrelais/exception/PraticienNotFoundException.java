package vbm.medrelais.exception;

public class PraticienNotFoundException extends RuntimeException {
    public PraticienNotFoundException(Long id) {
        super("Praticien introuvable avec l'id : " + id);
    }
    public PraticienNotFoundException(String message) {
        super(message);
    }
}


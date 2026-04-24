package vbm.medrelais.exception;

public class AttributionNotFoundException extends RuntimeException {
    public AttributionNotFoundException(Long id) {
        super("Attribution introuvable avec l'id : " + id);
    }
    public AttributionNotFoundException(String message) {
        super(message);
    }
}


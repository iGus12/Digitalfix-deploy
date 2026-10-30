package cl.digitalfix.notificacion.excepcion;

// Excepcion para datos invalidos que deben ir directo a la DLQ
public class ErrorNoRecuperableException extends RuntimeException {

    public ErrorNoRecuperableException(String mensaje) {
        super(mensaje);
    }
}

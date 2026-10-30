package cl.digitalfix.notificacion.gmail;

public record GmailMensaje(
        String para,
        String asunto,
        String mensaje,
        String ordenId
) {}
